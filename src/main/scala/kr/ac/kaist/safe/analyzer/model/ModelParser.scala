/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.model

import kr.ac.kaist.safe.{ SafeConfig, CmdCFGBuild }
import kr.ac.kaist.safe.parser.{ Parser => JSParser }
import kr.ac.kaist.safe.errors.error.ModelParseError
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.phase._
import kr.ac.kaist.safe.util.{ PredAllocSite, UserAllocSite, AllocSite }
import kr.ac.kaist.safe.LINE_SEP
import java.io._
import java.nio.charset.Charset
import scala.io.Source
// Rename Success and Failure to avoid name conflicts with ParseResult
import scala.util.{ Try, Success => Succ, Failure => Fail }
import scala.util.parsing.combinator._

case class JSModel(heap: Heap, funcs: List[(String, CFGFunction)], fidMax: Int) {
  def +(other: JSModel): JSModel = {
    // 1. rearrange function id in other.funcs
    val newFuncs = other.funcs.foldLeft(this.funcs) {
      case (funList, (body, cfgFunc)) => {
        cfgFunc.id = cfgFunc.id - this.fidMax
        (body, cfgFunc) :: funList
      }
    }
    // 2. rearrange function id in other.heap
    val mdfHeapMap = other.heap.map.foldLeft(Map(): Map[Loc, Obj]) {
      case (heapMap, (loc, obj)) => {
        val mdfimap = obj.imap.foldLeft(Map(): Map[IName, IValue]) {
          case (inimap, (name, value)) => {
            value match {
              case FId(id) => {
                val newFid = id - this.fidMax
                inimap + (name -> FId(newFid))
              }
              case _ => inimap + (name -> value)
            }
          }
        }
        val mdfobj = Obj(obj.nmap, mdfimap)
        heapMap + (loc -> mdfobj)
      }
    }
    val mdfHeap = Heap(mdfHeapMap)
    // 3. Heap + (AbsHeap.scala)
    val newHeap = this.heap + mdfHeap
    val newFidMax = this.fidMax + other.fidMax
    JSModel(newHeap, newFuncs, newFidMax)
    // 4. rearrange function id again
    // function id may be lost because of merging algorithm
  }
}

// Parser for models using regular expression parsers
trait ModelParser extends JavaTokenParsers with RegexParsers {
  // primitive parser
  val num: Parser[Double] =
    floatingPointNumber ^^ { _.toDouble } |
      "NaN" ^^^ Double.NaN |
      "Infinity" ^^^ Double.PositiveInfinity |
      "-Infinity" ^^^ Double.NegativeInfinity
  val int: Parser[Int] = wholeNumber ^^ { _.toInt }
  val str: Parser[String] = stringLiteral ^^ { case s => s.substring(1, s.length - 1) }
  val bool: Parser[Boolean] = "true" ^^^ true | "false" ^^^ false
  val shortBool: Parser[Boolean] = "T" ^^^ true | "F" ^^^ false

  // JavaScript primitive value
  val jsNum: Parser[Num] = num ^^ { Num(_) }
  val jsStr: Parser[Str] = str ^^ { Str(_) }
  val jsNull: Parser[Null] = "null" ^^^ { Null }
  val jsBool: Parser[Bool] = bool ^^ { Bool(_) }
  val jsShortBool: Parser[Bool] = shortBool ^^ { Bool(_) }
  val jsUndef: Parser[Undef] = "undefined" ^^^ { Undef }
  val jsPValue: Parser[PValue] = jsNum | jsStr | jsNull | jsBool | jsUndef

  // JavaScript primitive type
  val jsStrT: Parser[StringT.type] = "string" ^^^ { StringT }
  val jsNumT: Parser[NumberT.type] = "number" ^^^ { NumberT }
  val jsBoolT: Parser[BoolT.type] = "bool" ^^^ { BoolT }
  val jsPrimType: Parser[Value] = jsStrT | jsNumT | jsBoolT

  // JavaScript value
  val jsLoc: Parser[Loc] = "#" ~> """[_\[\]0-9a-zA-Z.<>]+""".r ^^ { Loc(_) }
  val jsValue: Parser[Value] = jsPValue | jsLoc | jsPrimType

  // JavaScript data property
  val jsDataProp: Parser[DataProp] = "<" ~> (
    jsValue ~
    ("," ~> jsShortBool) ~
    ("," ~> jsShortBool) ~
    ("," ~> jsShortBool)
  ) <~ ">" ^^ {
      case value ~ writable ~ enumerable ~ configurable =>
        DataProp(value, writable, enumerable, configurable)
    }

  // JavaScript internal property
  val jsIPrototype = "[[Prototype]]" ^^^ { IPrototype }
  val jsIClass = "[[Class]]" ^^^ { IClass }
  val jsIExtensible = "[[Extensible]]" ^^^ { IExtensible }
  val jsIPrimitiveValue = "[[PrimitiveValue]]" ^^^ { IPrimitiveValue }
  val jsICall = "[[Call]]" ^^^ { ICall }
  val jsIConstruct = "[[Construct]]" ^^^ { IConstruct }
  val jsIScope = "[[Scope]]" ^^^ { IScope }
  val jsIHasInstance = "[[HasInstance]]" ^^^ { IHasInstance }
  val jsIName: Parser[IName] = {
    jsIPrototype | jsIClass | jsIExtensible | jsIPrimitiveValue |
      jsICall | jsIConstruct | jsIScope | jsIHasInstance
  }
  val jsFId: Parser[FId] = "fun(" ~> int <~ ")" ^^ { case n => FId(-n) }
  val jsIValue: Parser[IValue] = jsValue | jsFId

  // JavaScript object
  type PMap = Map[String, DataProp]
  type IMap = Map[IName, IValue]
  val jsMember = (str <~ ":") ~ jsDataProp ^^ { case n ~ d => (n, d) }
  val jsIMember = (jsIName <~ ":") ~ jsIValue ^^ { case n ~ v => (n, v) }
  val jsObjMapTuple: Parser[(PMap, IMap)] = repsep(jsMember | jsIMember, ",") ^^ {
    case lst => ((Map[String, DataProp](), Map[IName, IValue]()) /: lst) {
      case ((pmap, imap), kv) => kv match {
        case (name: String, dp: DataProp) => (pmap + (name -> dp), imap)
        case (iname: IName, iv: IValue) => (pmap, imap + (iname -> iv))
        case _ => throw new IllegalStateException(s"Unexpected parse result: $kv")
      }
    }
  }
  val jsObject: Parser[Obj] = "{" ~> jsObjMapTuple <~ (","?) <~ "}" ^^ {
    case (pmap, imap) => Obj(pmap, imap)
  }

  // JavaScript Heap
  val jsHeap: Parser[Heap] = "{" ~> (
    repsep((jsLoc <~ ":") ~ jsObject, ",")
  ) <~ (","?) <~ "}" ^^ {
      case lst => {
        val map = lst.foldLeft(Map[Loc, Obj]()) {
          case (map, loc ~ obj) => {
            map + (loc -> obj)
          }
        }
        Heap(map)
      }
    }

  // JavaScript function
  val jsFun: Parser[(String, CFGFunction)] = """[\\""" ~> """[^\\]*""".r <~ """\\]""" ^^ {
    case fun => JSParser.stringToAST(fun) match {
      case Succ((pgm, log)) => {
        if (log.hasError) println(log)
        val safeConfig = SafeConfig(CmdCFGBuild, silent = true)

        // rewrite AST
        val astRewriteConfig = ASTRewriteConfig()
        val rPgm = ASTRewrite(pgm, safeConfig, astRewriteConfig).get

        // translate
        val translateConfig = TranslateConfig()
        val ir = Translate(rPgm, safeConfig, translateConfig).get

        // cfg build
        val cfgBuildConfig = CFGBuildConfig()
        var funCFG = CFGBuild(ir, safeConfig, cfgBuildConfig).get
        var func = funCFG.getFunc(1).get

        Succ((fun, func))
      }
      case Fail(e) => {
        println(ModelParseError(e.toString))
        Fail(e)
      }
    }
  } ^? { case Succ(pgm) => pgm }
  val jsFuncs: Parser[(List[(String, CFGFunction)], Map[Int, Int])] = "{" ~> (
    repsep((int <~ ":") ~ jsFun, ",")
  ) <~ (","?) <~ "}" ^^ {
      case lst => {
        var fidMap = Map[Int, Int]()
        var size = 0
        val result = lst.foldLeft(List[(String, CFGFunction)]()) {
          case (funcs, mid ~ ((body, func))) => {
            size += 1
            fidMap += (mid -> size)
            func.id = -mid
            // allocation site mutation
            // TODO is predefined allocation site good? how about incremental user allocation site?
            def mutate(asite: AllocSite): PredAllocSite = asite match {
              case UserAllocSite(id) =>
                PredAllocSite(s"-$mid[$id]")
              case pred: PredAllocSite => pred
            }
            func.getAllBlocks.foreach(_.getInsts.foreach {
              case i: CFGAlloc => i.asite = mutate(i.asite)
              case i: CFGAllocArray => i.asite = mutate(i.asite)
              case i: CFGAllocArg => i.asite = mutate(i.asite)
              case i: CFGCallInst => i.asite = mutate(i.asite)
              case i: CFGInternalCall => i.asiteOpt = i.asiteOpt.map(mutate(_))
              case _ =>
            })
            (body, func) :: funcs
          }
        }
        (result, fidMap)
      }
    }

  // JavaScript model
  val jsModel: Parser[JSModel] =
    ("Heap" ~> ":" ~> jsHeap) ~ ("Function" ~> ":" ~> jsFuncs) ^^ {
      case heap ~ ((funcs, map)) => {
        // Check map whether it needs rewrite or not
        if (map.keySet == map.values.toSet) {
          JSModel(heap, funcs.sortBy { case (_, f) => -f.id }, funcs.length)
        } else {
          val newFuncs = funcs.foldLeft(List[(String, CFGFunction)]()) {
            case (funList, (body, cfgFunc)) => {
              cfgFunc.id = -map(-cfgFunc.id)
              (body, cfgFunc) :: funList
            }
          }
          val newHeapMap = heap.map.foldLeft(Map(): Map[Loc, Obj]) {
            case (heapMap, (loc, obj)) => {
              val mdfimap = obj.imap.foldLeft(Map(): Map[IName, IValue]) {
                case (inimap, (name, value)) => {
                  value match {
                    case FId(id) => {
                      val newFid = -map(-id)
                      inimap + (name -> FId(newFid))
                    }
                    case _ => inimap + (name -> value)
                  }
                }
              }
              val mdfobj = Obj(obj.nmap, mdfimap)
              heapMap + (loc -> mdfobj)
            }
          }
          val newHeap = Heap(newHeapMap)
          val newModel = JSModel(newHeap, newFuncs, funcs.length)
          newModel
        }
      }
    }
}
object Model extends ModelParser {
  def apply(str: String): JSModel = parseAll(jsModel, str).get
  def parseFile(fileName: String): JSModel = {
    val fs = new FileInputStream(new File(fileName))
    val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
    val in = new BufferedReader(sr)
    val result = parseAll(jsModel, in).get
    in.close; sr.close; fs.close
    result
  }
  def parseDir(dir: String): JSModel = {
    val fileNames: List[String] = new File(dir).list.toList
    val mergeModel = fileNames.foldLeft(JSModel(Heap(Map()), Nil, 0)) {
      case (model, fileName) if fileName.endsWith(".jsmodel") =>
        model + parseFile(dir + fileName)
      case (model, _) => model
    }
    mergeModel
  }
}
