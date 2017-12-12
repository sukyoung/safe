/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.models

import kr.ac.kaist.safe.{ SafeConfig, CmdCFGBuild }
import kr.ac.kaist.safe.parser.{ Parser => JSParser }
import kr.ac.kaist.safe.errors.error.ModelParseError
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.phase._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.LINE_SEP
import java.io._
import java.nio.charset.Charset
import scala.io.Source
import scala.collection.immutable.HashMap
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
    val mdfHeapMap = other.heap.map.foldLeft(HashMap(): Map[Loc, Object]) {
      case (heapMap, (loc, obj)) => {
        val mdfimap = obj.imap.foldLeft(HashMap(): Map[IName, IValue]) {
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
        val mdfobj = Object(obj.amap, mdfimap)
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
  // TODO Complete the toString function
  override def toString: String = {
    val s: StringBuilder = new StringBuilder
    val S: String = "  "
    val L = LINE_SEP
    // heap
    s.append(s"Heap: {$L")
    s.append(heap.map.map {
      case (loc, obj) => s"$S$loc: {$L" +
        (obj.imap.map {
          case (iname, iv) => s"$S$S$iname: $iv"
        }.mkString(s",$L")) + ({
          if (!obj.imap.isEmpty && !obj.amap.isEmpty) s",$L"
          else ""
        }) + (obj.amap.map {
          case (name, v) => s"""$S$S"$name": $v"""
        }).mkString(s",$L") + s"$L$S}"
    }.mkString(s",$L"))
    s.append(s"$L}$L$L")

    // function map
    s.append(s"Function: {")
    s.append(
      ({
        if (funcs.isEmpty) ""
        else {
          s"$L" + funcs.zipWithIndex.map {
            case ((body, _), idx) =>
              s"""$S${idx + 1}: [\\\\$L$S$body\\\\]"""
          }.mkString(s",$L")
        }
      })
    )
    s.append(s"$L}$L")

    s.toString
  }
}

// Argument parser by using Scala RegexParsers.
object ModelParser extends RegexParsers with JavaTokenParsers {
  def apply(code: String): Try[JSModel] = {
    val sr = new StringReader(code)
    val in = new BufferedReader(sr)
    val result = parseModel(in)
    in.close; sr.close
    result
  }
  def parseFile(fileName: String): Try[JSModel] = {
    val fs = new FileInputStream(new File(fileName))
    val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
    val in = new BufferedReader(sr)
    val result = parseModel(in)
    in.close; sr.close; fs.close
    result
  }
  def mergeJsModels(dir: String): JSModel = {
    val fileNames: List[String] = new File(dir).list.toList
    val mergeModel = fileNames.foldLeft(JSModel(Heap(HashMap()), Nil, 0)) {
      case (model, fileName) =>
        model + ModelParser.parseFile(dir + fileName).get
    }
    mergeModel
  }

  //////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////
  // parse
  private def parseModel(reader: BufferedReader): Try[JSModel] = {
    parse(jsModel, reader) match {
      case Success(result, _) => Succ(result)
      case fail @ NoSuccess(_, _) => Fail(ModelParseError(fail.toString))
    }
  }
  // repeat rules
  private def emptyList[T]: Parser[List[T]] = success(Nil)
  private def repsepE[T](p: => Parser[T], sep: String): Parser[List[T]] =
    p ~! (("," ~> repsepE(p, sep)) | emptyList) ^^ { case x ~ xs => x :: xs } | emptyList

  // primitive parser
  private lazy val num: Parser[Double] =
    floatingPointNumber ^^ { _.toDouble } |
      "NaN" ^^^ Double.NaN |
      "Infinity" ^^^ Double.PositiveInfinity |
      "-Infinity" ^^^ Double.NegativeInfinity
  private lazy val int: Parser[Int] = wholeNumber ^^ { _.toInt }
  private lazy val str: Parser[String] = "\"" ~> "[^\"]*".r <~ "\""
  private lazy val t: Parser[Boolean] = "true" ^^^ { true }
  private lazy val f: Parser[Boolean] = "false" ^^^ { false }
  private lazy val bool: Parser[Boolean] = t | f
  private lazy val T: Parser[Boolean] = "T" ^^^ { true }
  private lazy val F: Parser[Boolean] = "F" ^^^ { false }
  private lazy val shortBool: Parser[Boolean] = T | F
  private lazy val any: Parser[String] = """[^\\]*""".r

  // JavaScript primitive value
  private lazy val jsNum: Parser[Num] = num ^^ { Num(_) }
  private lazy val jsStr: Parser[Str] = str ^^ { Str(_) }
  private lazy val jsNull: Parser[Null] = "null" ^^^ { Null }
  private lazy val jsBool: Parser[Bool] = bool ^^ { Bool(_) }
  private lazy val jsShortBool: Parser[Bool] = shortBool ^^ { Bool(_) }
  private lazy val jsShortBoolE: Parser[Bool] =
    jsShortBool | "" ~> failure("illegal start of boolean(T/F)")
  private lazy val jsUndef: Parser[Undef] = "undefined" ^^^ { Undef }
  private lazy val jsPValue: Parser[PValue] = jsNum | jsStr | jsNull | jsBool | jsUndef

  // JavaScript primitive type
  private lazy val jsStrT: Parser[StringT.type] = "string" ^^^ { StringT }
  private lazy val jsNumT: Parser[NumberT.type] = "number" ^^^ { NumberT }
  private lazy val jsBoolT: Parser[BoolT.type] = "bool" ^^^ { BoolT }
  private lazy val jsPrimType: Parser[Value] = jsStrT | jsNumT | jsBoolT

  // JavaScript value
  private lazy val jsLoc: Parser[Loc] = "#" ~> """[_\[\]0-9a-zA-Z.<>]+""".r ^^ { Loc(_) }
  private lazy val jsValue: Parser[Value] = jsPValue | jsLoc | jsPrimType
  private lazy val jsValueE: Parser[Value] = jsValue | failure("illegal start of value")

  // JavaScript data property
  private lazy val jsDataProp: Parser[DataProp] = "<" ~> (
    jsValueE ~
    ("," ~> jsShortBoolE) ~
    ("," ~> jsShortBoolE) ~
    ("," ~> jsShortBoolE)
  ) <~ ">" ^^ {
      case value ~ writable ~ enumerable ~ configurable =>
        DataProp(value, writable, enumerable, configurable)
    }

  // JavaScript internal property
  private lazy val jsIPrototype = "[[Prototype]]" ^^^ { IPrototype }
  private lazy val jsIClass = "[[Class]]" ^^^ { IClass }
  private lazy val jsIExtensible = "[[Extensible]]" ^^^ { IExtensible }
  private lazy val jsIPrimitiveValue = "[[PrimitiveValue]]" ^^^ { IPrimitiveValue }
  private lazy val jsICall = "[[Call]]" ^^^ { ICall }
  private lazy val jsIConstruct = "[[Construct]]" ^^^ { IConstruct }
  private lazy val jsIScope = "[[Scope]]" ^^^ { IScope }
  private lazy val jsIHasInstance = "[[HasInstance]]" ^^^ { IHasInstance }
  private lazy val jsIName: Parser[IName] = {
    jsIPrototype | jsIClass | jsIExtensible | jsIPrimitiveValue |
      jsICall | jsIConstruct | jsIScope | jsIHasInstance
  }
  private lazy val jsFId: Parser[FId] = "fun(" ~> int <~ ")" ^^ { case n => FId(-n) }
  private lazy val jsIValue: Parser[IValue] = jsValue | jsFId
  private lazy val jsIValueE: Parser[IValue] =
    jsIValue | "" ~> failure("illegal start of IValue")

  // JavaScript object
  private type PMap = Map[String, DataProp]
  private type IMap = Map[IName, IValue]
  private def jsObjMapTuple: Parser[(PMap, IMap)] = {
    lazy val empty: Parser[(PMap, IMap)] = success((HashMap(), HashMap()))
    lazy val jsMember = (str <~ ":") ~! jsDataProp ^^ { case n ~ d => (n, d) }
    lazy val jsIMember = (jsIName <~ ":") ~! jsIValueE ^^ { case n ~ v => (n, v) }
    lazy val next = ("," ~> jsObjMapTuple) | empty
    jsMember ~! next ^^
      { case (name, dp) ~ ((pmap, imap)) => (pmap + (name -> dp), imap) } |
      jsIMember ~! next ^^
      { case (iname, iv) ~ ((pmap, imap)) => (pmap, imap + (iname -> iv)) } |
      empty
  }
  private lazy val jsObject: Parser[Object] = "{" ~> jsObjMapTuple <~ "}" ^^ {
    case (pmap, imap) => Object(pmap, imap)
  }

  // JavaScript Heap
  private lazy val jsHeap: Parser[Heap] = "{" ~> (
    repsepE((jsLoc <~ ":") ~! jsObject, ",")
  ) <~ "}" ^^ {
      case lst => {
        val map = lst.foldLeft(HashMap[Loc, Object]()) {
          case (map, loc ~ obj) => {
            map + (loc -> obj)
          }
        }
        Heap(map)
      }
    }

  // JavaScript function
  private lazy val jsFun: Parser[(String, CFGFunction)] = """[\\""" ~> any <~ """\\]""" ^^ {
    case fun => JSParser.stringToAST(fun) match {
      case Succ((pgm, log)) => {
        if (log.hasError) println(log)
        val safeConfig = SafeConfig(CmdCFGBuild, silent = true)

        // rewrite AST
        val astRewriteConfig = ASTRewriteConfig()
        val rPgm = ASTRewrite(pgm, safeConfig, astRewriteConfig).get

        // compile
        val compileConfig = CompileConfig()
        val ir = Compile(rPgm, safeConfig, compileConfig).get

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
  private lazy val jsFuncs: Parser[(List[(String, CFGFunction)], Map[Int, Int])] = "{" ~> (
    repsepE((int <~ ":") ~! jsFun, ",")
  ) <~ "}" ^^ {
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
                PredAllocSite(s"-$mid$id")
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
  private lazy val jsModel: Parser[JSModel] =
    ("Heap" ~> ":" ~> jsHeap) ~! ("Function" ~> ":" ~> jsFuncs) ^^ {
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
          val newHeapMap = heap.map.foldLeft(HashMap(): Map[Loc, Object]) {
            case (heapMap, (loc, obj)) => {
              val mdfimap = obj.imap.foldLeft(HashMap(): Map[IName, IValue]) {
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
              val mdfobj = Object(obj.amap, mdfimap)
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
