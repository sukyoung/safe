/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.nodes_util

import _root_.java.io.BufferedWriter
import _root_.java.io.IOException
import _root_.java.util.HashMap
import _root_.java.util.{List => JList}

import kr.ac.kaist.jsaf.exceptions.JSAFError.error
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeFactory => NF}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.{Shell, ShellParameters}

object NodeUtil {

  var isCloneDetector = false
  def setCloneDetector(flag: Boolean) = { isCloneDetector = flag }
  def getCloneDetector() = isCloneDetector

  var keepComments = false
  def setKeepComments(flag: Boolean) = { keepComments = flag }
  def getKeepComments() = keepComments

  def unwrapParen(expr: Expr) = expr match {
    case SParenthesized(info, body) => body
    case _ => expr
  }

  def isDummySpan(span: Span) = {
    val loc = span.getBegin
    loc.getLine == 0 && loc.column == 0
  }

  def adjustCallSpan(finish: Span, expr: LHS) = expr match {
    case SParenthesized(info, body) => new Span(getSpan(body).getBegin, finish.getEnd)
    case _ => finish
  }
  
  private val regex_jquery = """.*jquery[^/]*\.js""".r
  private val regex_jquery_plugin = """.*(tabs|ui|menu|carouFredSel|onebyone|prettyPhoto|touchwipe|autocomplete|backbone|cookie).*""".r
  private val regex_mobile = """.*mobile[^/]*\.js""".r

  private def list_regex_lib = List(
    regex_jquery)
  /* check library */
  def isModeledLibrary(srcname: String): Boolean = {
    list_regex_lib.exists((regex) =>
      regex_jquery.findFirstIn(srcname).nonEmpty && regex_jquery_plugin.findFirstIn(srcname).isEmpty && regex_mobile.findFirstIn(srcname).isEmpty)
  }


  // Assumes that the filename remains the same.
  object addLinesWalker extends Walker {
    var line = 0
    def addLines(node: Node, l: Int) = {
      line = l; map = new HashMap[String, Span]
      walk(node)
    }
    var map = new HashMap[String, Span]
    override def walk(node:Any):Any = node match {
      case i:ASTSpanInfo =>
        val span = i.getSpan
        val key = span.at
        if (map.containsKey(key)) new ASTSpanInfo(map.get(key))
        else {
          val newSpan = span.addLines(line)
          map.put(key, newSpan)
          new ASTSpanInfo(newSpan)
        }
      case _: Comment => node
      case _ => super.walk(node)
    }
  }

  // AST: Remove empty blocks, empty statements, debugger statements, ...
  object simplifyWalker extends Walker {
    var repeat = false
    def simplify(stmts: List[Stmt]): List[Stmt] = {
      repeat = false
      val simplified = simpl(stmts)
      val result = if (repeat) simplify(simplified) else simplified
      result
    }

    def simpl(stmts: List[Stmt]) = stmts match {
      case Nil => Nil
      case stmt::rest => stmt match {
        case _:Debugger => repeat = true; simplify(rest)
        case _:EmptyStmt => repeat = true; simplify(rest)
        case SBlock(_, Nil, _) => repeat = true; simplify(rest)
        case SBlock(_, SBlock(_, Nil, _)::stmts, _) =>
          repeat = true;
          simplify(stmts)++simplify(rest)
        case SBlock(_, SBlock(_, ss, _)::stmts, _) =>
          repeat = true;
          simplify(ss)++simplify(stmts)++simplify(rest)
        case SBlock(_, s@List(stmt), _) =>
          repeat = true;
          simplify(s)++simplify(rest)
        case SBlock(info, sts, b) =>
          repeat = true;
          List(SBlock(info, simplify(sts), b))++simplify(rest)
        case _ => List(stmt)++simplify(rest)
      }
    }

    override def walk(node:Any):Any = node match {
      case SBlock(info, List(stmt), b) =>
        SBlock(info, List(walk(stmt).asInstanceOf[Stmt]), b)
      case SBlock(info, SBlock(_, Nil, _)::stmts, b) => walk(SBlock(info, stmts, b))
      case SBlock(info, SBlock(_, ss, _)::stmts, b) => walk(SBlock(info, ss++stmts, b))
      case SBlock(info, stmts, b) =>
        SBlock(info, simplify(stmts.map(walk).asInstanceOf[List[Stmt]]), b)
      case SSwitch(info, cond, frontCases, Some(stmts), backCases) =>
        SSwitch(info, cond, super.walk(frontCases).asInstanceOf[List[Case]],
                Some(simplify(stmts.map(walk).asInstanceOf[List[Stmt]])),
                super.walk(backCases).asInstanceOf[List[Case]])
      case SProgram(info, STopLevel(fds, vds, program)) =>
        SProgram(info, STopLevel(super.walk(fds).asInstanceOf[List[FunDecl]], vds,
                                 program.map(ss => ss match {
                                   case SSourceElements(i, s, f) =>
                                     SSourceElements(i, simplify(s.map(walk).asInstanceOf[List[Stmt]]), f)
                                 })))
      case SFunctional(fds, vds, SSourceElements(info, body, strict), name, params) =>
        SFunctional(super.walk(fds).asInstanceOf[List[FunDecl]], vds,
                    SSourceElements(info,
                                    simplify(body.map(walk).asInstanceOf[List[Stmt]]),
                                    strict), name, params)
      case _: Comment => node
      case _ => super.walk(node)
    }
  }

  // IR: Remove empty blocks, empty statements, ...
  // Do not remove IRSeq aggressively.
  // They denote internal IRStmts whose values do not contribute to the result.
  object simplifyIRWalker extends Walker {
    override def walk(node:Any):Any = node match {
      case SIRRoot(info, fds, vds, irs) =>
        SIRRoot(info, super.walk(fds).asInstanceOf[List[IRFunDecl]], vds,
                simplify(irs.map(walk).asInstanceOf[List[IRStmt]]))

      case SIRFunctional(i, n, params, args, fds, vds, body) =>
        SIRFunctional(i, n, params, args.map(walk).asInstanceOf[List[IRStmt]],
                      super.walk(fds).asInstanceOf[List[IRFunDecl]], vds,
                      simplify(body.map(walk).asInstanceOf[List[IRStmt]]))

      case SIRStmtUnit(info, stmts) =>
        SIRStmtUnit(info, simplify(stmts.map(walk).asInstanceOf[List[IRStmt]]))

      case SIRSeq(info, stmts) =>
        SIRSeq(info, simplify(stmts.map(walk).asInstanceOf[List[IRStmt]]))

      case _ => super.walk(node)
    }

    // Simplify a list of IRStmts recursively until no change
    var repeat = false
    def simplify(stmts: List[IRStmt]): List[IRStmt] = {
      repeat = false
      val simplified = simpl(stmts)
      val result = if (repeat) simplify(simplified) else simplified
      result
    }

    def simpl(stmts: List[IRStmt]): List[IRStmt] = stmts match {
      case Nil => Nil
      case stmt::rest => stmt match {
        // Remove an empty internal IRStmt list
        case SIRSeq(_, Nil) => repeat = true; simpl(rest)

        // Remove a self assignment IRStmt
        case SIRExprStmt(_, lhs, rhs:IRId, ref) if lhs.getUniqueName.equals(rhs.getUniqueName) =>
          repeat = true; simpl(rest)

        // Simplify the following case:
        //     <>ignore<>1 = expr
        //     <>temp = <>ignore<>1
        // to the following:
        //     <>temp = expr
        /*
        case first:IRAssign => rest match {
          case (second@SIRExprStmt(_, _, _, right:IRId, _))::others =>
            if (first.getLhs.getUniqueName.equals(right.getUniqueName) &&
                right.getUniqueName.equals(ignoreName)) {
              (walk(replaceLhs(first, second.getLhs)).asInstanceOf[IRStmt])::simpl(others)
            } else walk(first).asInstanceOf[IRStmt]::simpl(rest)
          case _ => walk(first).asInstanceOf[IRStmt]::simpl(rest)
        }
        */

        case _ => walk(stmt).asInstanceOf[IRStmt]::simpl(rest)
      }
    }
  }

  val internalSymbol = "<>"
  val internalPrint = "_<>_print"
  val internalPrintIS = "_<>_printIS"
  val internalGetTickCount = "_<>_getTickCount"
  val futureReserved = Set(
        "implements",
        "interface",
        "let",
        "package",
        "private",
        "protected",
        "public",
        "static",
        "yield"
  )
  val globalPrefix = "<>Global<>"
  val concolicPrefix = "<>Concolic<>"
  def isInternal(s: String) = s.containsSlice(internalSymbol)
  def isGlobalName(s: String) = s.startsWith(globalPrefix)
  def isFunExprName(name: String) = name.containsSlice("<>funexpr")
  // dummy file name for source location information
  def freshFile(f: String) = internalSymbol + f
  // unique name generation for global names
  def freshGlobalName(n: String) = globalPrefix + n
  def freshConcolicName(n: String) = concolicPrefix + n
  val significantBits = 13
  // unique name generation
  def freshName(n: String) =
    internalSymbol + n + internalSymbol + System.nanoTime.toString.takeRight(significantBits)
  def getOriginalName(n: String) =
    if (isGlobalName(n)) n.drop(10)
    else {
      if (!isInternal(n)) n
      else n.drop(2).dropRight(significantBits)
    }
  val toObjectName = freshGlobalName("toObject")
  val ignoreName = freshGlobalName("ignore")
  val varTrue = freshGlobalName("true")
  val varOne = freshGlobalName("one")
  def funexprName(span: Span) = freshName("funexpr@"+span.toStringWithoutFiles)

  def isBuiltin(name: String) =
    List("Object", "Function", "Array", "String", "Boolean", "Number", "Math", "Date", "RegExp", "Error", "JSON").contains(name)

  def isFutureReserved(id: Id): Boolean = isFutureReserved(id.getText)
  def isFutureReserved(id: String): Boolean = futureReserved.contains(id)
  def isEvalOrArguments(id: Id): Boolean = isEvalOrArguments(id.getText)
  def isEvalOrArguments(id: String): Boolean = id.compareTo("eval") == 0 || id.compareTo("arguments") == 0
  def isUnderscore(id: Id): Boolean = id.getText.equals("_")

  def isName(lhs: LHS) = lhs match {
    case _:VarRef => true
    case _:Dot => true
    case _ => false
  }

  def isEval(n: Expr) = n match {
    case SVarRef(info, SId(_, text, _, _)) => 
      if(Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR) 
        false 
      else text.equals("eval")
    case _ => false
  }

  // merge the statements in each SourceElements
  // after hoisting
  def toStmts(sources: List[SourceElements]): List[Stmt] =
    sources.foldLeft(List[Stmt]())((l, s) => l++toList(s.getBody).asInstanceOf[List[Stmt]])

  def getName(lhs: LHS): String = lhs match {
    case SVarRef(_, id) => id.getText
    case SDot(_, front, id) => getName(front)+"."+id.getText
    case _:This => "this"
    case _ => ""
  }

  def prop2Id(prop: Property) = prop match {
    case SPropId(info, id) => id
    case SPropStr(info, str) => SId(info, str, None, false)
    case SPropNum(info, SDoubleLiteral(_,t,_)) => SId(info, t, None, false)
    case SPropNum(info, SIntLiteral(_,i,_)) => SId(info, i.toString, None, false)
  }

  def prop2Str(prop: Property) = prop match {
    case SPropId(_, id) => id.getText
    case SPropStr(_, str) => str
    case SPropNum(info, SDoubleLiteral(_, _, num)) => num.toString
    case SPropNum(info, SIntLiteral(_, num, _)) => num.toString
  }

  def member2Str(member: Member) = member match {
    case SField(_, prop, _) => prop2Str(prop)
    case SGetProp(_, prop, _) => prop2Str(prop)
    case SSetProp(_, prop, _) => prop2Str(prop)
  }

  def escape(s: String) = {
    s.replaceAll("\\\\", "\\\\\\\\")
  }

  def lineTerminating(c: Char): Boolean =
    List('\u000a', '\u2028', '\u2029', '\u000d').contains(c)

  def unescapeJava(s: String) =
    if (-1 == s.indexOf('\\')) s
    else {
      val length = s.length
      val buf = new StringBuilder(length)
      var i = 0
      while (i < length) {
        var c = s.charAt(i)
        if ('\\' != c) {
          buf.append(c)
          i += 1
        } else  {
          i += 1
          if (i >= length) {
            throw new IllegalArgumentException("incomplete escape sequence")
          }
          c = s.charAt(i)
          c match {
            case '"' => buf.append('"')
            case '\'' => buf.append('\'')
            case '\\' => buf.append('\\')
            case 'b' => buf.append('\b')
            case 'f' => buf.append('\f')
            case 'n' => buf.append('\n')
            case 'r' => buf.append('\r')
            case 't' => buf.append('\t')
            case 'v' => buf.append('\u000b')
            case 'x' =>
              i += 2
              if (i >= length) {
                throw new IllegalArgumentException("incomplete universal character"+
                                                   " name " + s.substring(i-1))
              }
              val n = Integer.parseInt(s.substring(i-1, i+1), 16)
              buf.append(n.asInstanceOf[Char])
            case 'u' =>
              i += 4
              if (i >= length) {
                throw new IllegalArgumentException("incomplete universal character"+
                                                   " name " + s.substring(i-3))
              }
              val n = Integer.parseInt(s.substring(i-3, i+1), 16)
              buf.append(n.asInstanceOf[Char])
            case c if lineTerminating(c) =>
            case _ => buf.append(c)
          }
          i += 1
        }
      }
    buf.toString
    }

  /*
   * Remark: Should be modified to recognize EscapeSequence and LineContinuation exactly.
   */
  def isEscapeSeqOrLineCont(char: Char) = char match {
    case '\b' | '\t' | '\n' | '\f' | '\r' | '\u000B' | '\"' | '\'' | '\\' => true
    case _ => false
  }
  def removeEscapeSeqLineCont(str: String) = str.filterNot(isEscapeSeqOrLineCont)

  def isOctalDigit(char: Char) = char match {
    case '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' => true
    case _ => false
  }
  def isZeroToThree(char: Char) = char match {
    case '0' | '1' | '2' | '3' => true
    case _ => false
  }
  def isFourToSeven(char: Char) = char match {
    case '4' | '5' | '6' | '7' => true
    case _ => false
  }
  def isDecimalDigit(char: Char) = char match {
    case '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' => true
    case _ => false
  }

  /*
   * Remark: Should be modified to recognize the OctalIntegerLiteral exactly.
   *
   * OctalEscapeSequence ::=
   * 	OctalDigit [lookahead does not include DecimalDigit] |
   * 	ZeroToThree OctalDigit [lookahead does not include DecimalDigit] |
   *    FourToSeven OctalDigit |
   * 	ZeroToThree OctalDigit OctalDigit
   */
  def hasOctalEscapeSequence(str: String) = {
    def loop(origin: String): Boolean = {
      val index = origin.indexOf("\\")
      val text = origin.substring(index+1)
      if (index < 0) false
      else if (text.length > 0 && isOctalDigit(text.charAt(0)) &&
               (text.length == 1 || (text.length >= 2 &&
                                     !isOctalDigit(text.charAt(1))))) {
        true
      } else if (text.length >= 2) {
        val first = text.charAt(0)
        val second = text.charAt(1)
        if (isZeroToThree(first) && isOctalDigit(second)) {
          if (text.length == 2) true
          else {
            val third = text.charAt(2)
            if (!isDecimalDigit(third) || isOctalDigit(third)) true
            else loop(text)
          }
        } else if (isFourToSeven(first) && isOctalDigit(second)) true
        else loop(text)
      } else loop(text)
    }
    loop(str)
  }

  def log(writer: BufferedWriter, span: Span, msg: String) =
    try {
      span.appendTo(writer, true, true)
      writer.write("\n"+msg+"\n")
    } catch {
    case e:IOException =>
      error("Writing to a log file for the parser failed!")
    }

  def log(writer: BufferedWriter, msg: String) =
    try {
      writer.write( msg + "\n" )
    } catch {
    case e:IOException =>
      error("Writing to a log file for the parser failed!")
    }

  /* Methods for ASTNode */

  def getInfo(n: ASTNode) = n.getInfo

  def getSpan(n: ASTNode): Span = n.getInfo.getSpan
  def getSpan(n: IRAbstractNode): Span = n.getInfo.getSpan
  def getSpan(n: IRExpr): Span = n.getInfo.getSpan
  def getSpan(n: SpanInfo): Span = n.getSpan

  def getFileName(n: ASTNode): String = getSpan(n).getFileName
  def getBegin(n: ASTNode) = getSpan(n).getBegin
  def getEnd(n: ASTNode) = getSpan(n).getEnd
  def getLine(n: ASTNode) = getSpan(n).getBegin.getLine

  def spanInfoAll(nodes: List[ASTNode]) = new ASTSpanInfo(spanAll(nodes))

  def spanAll(nodes: List[ASTNode], span: Span): Span = nodes match {
    case Nil => span
    case _ => spanAll(nodes)
  }

  def spanAll(nodes: List[ASTNode]): Span = nodes match {
    case Nil => error("Cannot make a span from an empty list of nodes.")
    case hd::_ =>
      new Span(getSpan(hd).getBegin, getSpan(nodes.last).getEnd)
  }

  def spanAll(span1: Span, span2: Span): Span =
    new Span(span1.getBegin, span2.getEnd)

  def jspanAll(nodes: JList[Expr]): Span =
    spanAll(toList(nodes).asInstanceOf[List[ASTNode]])

  def getFileName(n: IRAbstractNode): String = n.getInfo.getSpan.getFileName

  /* stringName **********************************************************/
  def stringName(node: Node) = node match {
    case SId(_, text, _, _) => text
    case _ => node.getClass.getSimpleName
  }

  def stringName(node: IRNode) = node match {
    case id:IRId => id.getUniqueName
    case _ => node.getClass.getSimpleName
  }

  /* add Path to ModExpSpecifier ******************************************/
  def addPath(node: ModExpSpecifier, path: Path): ModExpSpecifier = node match {
    case SModExpAlias(i, n, p) => SModExpAlias(i, n, NF.makePath(p, path))
    case SModExpName(i, p) => SModExpName(i, NF.makePath(p, path))
    case _ => error("Cannot add a path to a catch-all export statement.")
  }

  def readSpan(span: String) = {
    var next = span.slice(3, span.size)

    var index = next.indexOf('\"')
    val fname = next.slice(0, index)
    next = next.slice(index+2, next.size)

    index = next.indexOf(':')
    var line = Integer.parseInt(next.slice(0,index),10)
    next = next.slice(index+1, next.size)

    index = next.indexOf('~')
    var column = Integer.parseInt(next.slice(0,index),10)
    val beginning = new SourceLocRats(fname, line, column, 0)
    next = next.slice(index+1, next.size)

    if (next.contains(':')) {
      index = next.indexOf(':')
      line = Integer.parseInt(next.slice(0,index),10)
      next = next.slice(index+1, next.size)
    }

    column = Integer.parseInt(next.slice(0,next.size),10)
    val ending = new SourceLocRats(fname, line, column, 0)

    new Span(beginning, ending)
  }

  def dropAnySingleLeadingUnderscore(s: String) =
    if (s.startsWith("_")) s.substring(1)
    else s
}
