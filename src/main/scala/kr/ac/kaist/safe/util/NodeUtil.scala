/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.nodes.Node
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.BASE_DIR
import kr.ac.kaist.safe.LINE_SEP
import java.io.BufferedWriter
import java.io.IOException
import scala.collection.immutable.{ HashMap, HashSet }

object NodeUtil {
  ////////////////////////////////////////////////////////////////
  // local mutable (TODO have to handle)
  ////////////////////////////////////////////////////////////////

  var iid = 0
  var nodesPrintId = 0
  var nodesPrintIdEnv: Map[String, String] = HashMap()
  var keepComments = false
  private var comment: Option[Comment] = None

  val INTERNAL_SYMBOL = "<>"
  val GLOBAL_PREFIX = "<>Global<>"
  val GENERATED_STR = "<>generated String Literal"

  val VAR_TRUE = freshGlobalName("true")
  val VAR_ONE = freshGlobalName("one")
  val IGNORE_NAME = freshGlobalName("ignore")
  val GLOBAL_NAME = freshGlobalName("global")
  val REF_ERR_NAME = freshGlobalName("referenceError")

  val MERGED_FILE_NAME = freshFile("Merged")
  val MERGED_SPAN = Span(MERGED_FILE_NAME)
  val MERGED_SOURCE_INFO = new ASTNodeInfo(MERGED_SPAN)

  val TEMP_AST = NoOp(ASTNodeInfo(Span()), "defaultAST")
  val TEMP_IR = IRNoOp(TEMP_AST, "defaultIR")

  val PRINT_WIDTH = 50

  // internal API
  val INTERNAL_API_PREFIX = "@"
  def internalAPIName(name: String): String = INTERNAL_API_PREFIX + name

  // internal API call
  // 9.3 ToNumber
  val INTERNAL_TO_NUM = internalAPIName("ToNumber")
  // 9.9 ToObject
  val INTERNAL_TO_OBJ = internalAPIName("ToObject")
  // 8.7 The Reference Specification Type
  val INTERNAL_GET_BASE = internalAPIName("GetBase")
  // Other helpers
  val INTERNAL_IS_OBJ = internalAPIName("isObject")
  val INTERNAL_ITER_INIT = internalAPIName("iteratorInit")
  val INTERNAL_HAS_NEXT = internalAPIName("iteratorHasNext")
  val INTERNAL_ITER_NEXT = internalAPIName("iteratorNext")
  val internalCallSet: Set[String] = HashSet(
    INTERNAL_TO_NUM,
    INTERNAL_TO_OBJ,
    INTERNAL_GET_BASE,
    INTERNAL_IS_OBJ,
    INTERNAL_ITER_INIT,
    INTERNAL_HAS_NEXT,
    INTERNAL_ITER_NEXT
  )
  def isInternalCall(id: String): Boolean = internalCallSet.contains(id)

  // internal API value
  val INTERNAL_TOP = internalAPIName("Top")
  val INTERNAL_UINT = internalAPIName("UInt")
  val INTERNAL_GLOBAL = internalAPIName("Global")
  val INTERNAL_BOOL_TOP = internalAPIName("BoolTop")
  val INTERNAL_NUM_TOP = internalAPIName("NumTop")
  val INTERNAL_STR_TOP = internalAPIName("StrTop")
  val INTERNAL_EVAL_ERR = internalAPIName("EvalErr")
  val INTERNAL_RANGE_ERR = internalAPIName("RangeErr")
  val INTERNAL_REF_ERR = internalAPIName("RefErr")
  val INTERNAL_SYNTAX_ERR = internalAPIName("SyntaxErr")
  val INTERNAL_TYPE_ERR = internalAPIName("TypeErr")
  val INTERNAL_URI_ERR = internalAPIName("URIErr")
  val INTERNAL_EVAL_ERR_PROTO = internalAPIName("EvalErrProto")
  val INTERNAL_RANGE_ERR_PROTO = internalAPIName("RangeErrProto")
  val INTERNAL_REF_ERR_PROTO = internalAPIName("RefErrProto")
  val INTERNAL_SYNTAX_ERR_PROTO = internalAPIName("SyntaxErrProto")
  val INTERNAL_TYPE_ERR_PROTO = internalAPIName("TypeErrProto")
  val INTERNAL_URI_ERR_PROTO = internalAPIName("URIErrProto")
  val INTERNAL_ERR_PROTO = internalAPIName("ErrProto")
  val INTERNAL_OBJ_CONST = internalAPIName("ObjConst")
  val INTERNAL_ARRAY_CONST = internalAPIName("ArrayConst")
  val internalValueSet: Set[String] = HashSet(
    INTERNAL_TOP,
    INTERNAL_UINT,
    INTERNAL_GLOBAL,
    INTERNAL_BOOL_TOP,
    INTERNAL_NUM_TOP,
    INTERNAL_STR_TOP,
    INTERNAL_EVAL_ERR,
    INTERNAL_RANGE_ERR,
    INTERNAL_REF_ERR,
    INTERNAL_SYNTAX_ERR,
    INTERNAL_TYPE_ERR,
    INTERNAL_URI_ERR,
    INTERNAL_EVAL_ERR_PROTO,
    INTERNAL_RANGE_ERR_PROTO,
    INTERNAL_REF_ERR_PROTO,
    INTERNAL_SYNTAX_ERR_PROTO,
    INTERNAL_TYPE_ERR_PROTO,
    INTERNAL_URI_ERR_PROTO,
    INTERNAL_ERR_PROTO,
    INTERNAL_OBJ_CONST,
    INTERNAL_ARRAY_CONST
  )
  def isInternalValue(id: String): Boolean = internalValueSet.contains(id)

  ////////////////////////////////////////////////////////////////
  // For all AST, IR, and CFG
  ////////////////////////////////////////////////////////////////

  // Models //////////////////////////////////////////////////////

  // For modeling in JavaScript
  val jsModelsName = "jsModels"
  val jsModelsBase = BASE_DIR + "/src/main/resources/" + jsModelsName + "/"
  val jsModels: List[String] =
    List[String](
      jsModelsBase + "__builtin__.js",
      jsModelsBase + "__dom__.js"
    )
  /*
      jsModelsBase + "__input__.js")
   */

  def isModeled(node: ASTNode): Boolean =
    node.info.span.fileName.contains(jsModelsName)
  def isModeled(block: CFGBlock): Boolean =
    block.span.fileName.contains(jsModelsName)

  // Names ///////////////////////////////////////////////////////
  // unique name generation
  def getIId: Int = { iid += 1; iid }
  def freshName(n: String): String =
    INTERNAL_SYMBOL + n + INTERNAL_SYMBOL + "%013d".format(getIId)
  // unique name generation for global names
  def freshGlobalName(n: String): String = GLOBAL_PREFIX + n
  def funexprName(span: Span): String = freshName("funexpr@" + span.toStringWithoutFiles)

  def isInternal(s: String): Boolean = s.containsSlice(INTERNAL_SYMBOL)
  def isGlobalName(s: String): Boolean = s.startsWith(GLOBAL_PREFIX)
  def isFunExprName(name: String): Boolean = name.containsSlice("<>funexpr")

  def convertUnicode(s: String): String =
    s.replace("\\u0041", "A")
      .replace("\\u0042", "B")
      .replace("\\u0043", "C")
      .replace("\\u0044", "D")
      .replace("\\u0045", "E")
      .replace("\\u0046", "F")
      .replace("\\u0047", "G")
      .replace("\\u0048", "H")
      .replace("\\u0049", "I")
      .replace("\\u004a", "J")
      .replace("\\u004b", "K")
      .replace("\\u004c", "L")
      .replace("\\u004d", "M")
      .replace("\\u004e", "N")
      .replace("\\u004f", "O")
      .replace("\\u0050", "P")
      .replace("\\u0051", "Q")
      .replace("\\u0052", "R")
      .replace("\\u0053", "S")
      .replace("\\u0054", "T")
      .replace("\\u0055", "U")
      .replace("\\u0056", "V")
      .replace("\\u0057", "W")
      .replace("\\u0058", "X")
      .replace("\\u0059", "Y")
      .replace("\\u005a", "Z")
      .replace("\\u0061", "a")
      .replace("\\u0062", "b")
      .replace("\\u0063", "c")
      .replace("\\u0064", "d")
      .replace("\\u0065", "e")
      .replace("\\u0066", "f")
      .replace("\\u0067", "g")
      .replace("\\u0068", "h")
      .replace("\\u0069", "i")
      .replace("\\u006a", "j")
      .replace("\\u006b", "k")
      .replace("\\u006c", "l")
      .replace("\\u006d", "m")
      .replace("\\u006e", "n")
      .replace("\\u006f", "o")
      .replace("\\u0070", "p")
      .replace("\\u0071", "q")
      .replace("\\u0072", "r")
      .replace("\\u0073", "s")
      .replace("\\u0074", "t")
      .replace("\\u0075", "u")
      .replace("\\u0076", "v")
      .replace("\\u0077", "w")
      .replace("\\u0078", "x")
      .replace("\\u0079", "y")
      .replace("\\u007a", "z")
      .replace("\\u0030", "0")
      .replace("\\u0031", "1")
      .replace("\\u0032", "2")
      .replace("\\u0033", "3")
      .replace("\\u0034", "4")
      .replace("\\u0035", "5")
      .replace("\\u0036", "6")
      .replace("\\u0037", "7")
      .replace("\\u0038", "8")
      .replace("\\u0039", "9")
      .replace("\\u005f", "_")

  // Defaults ////////////////////////////////////////////////////
  // dummy file name for source location information
  def freshFile(f: String): String = INTERNAL_SYMBOL + f

  def initNodesPrint: Unit = {
    nodesPrintId = 0
    nodesPrintIdEnv = HashMap()
  }

  def getNodesE(uniq: String): String = nodesPrintIdEnv.get(uniq) match {
    case Some(newUniq) => newUniq
    case None =>
      val newUniq: String = { nodesPrintId += 1; nodesPrintId.toString }
      nodesPrintIdEnv += (uniq -> newUniq)
      newUniq
  }

  def ppAST(s: StringBuilder, str: String): Unit =
    s.append(str.foldLeft("")((res, c) => c match {
      case '\u0008' => res + '\b'
      case '\t' => res + '\t'
      case '\n' => res + '\n'
      case '\f' => res + '\f'
      case '\r' => res + '\r'
      case '\u000b' => res + '\u000b'
      case '"' => res + '"'
      case '\'' => res + "'"
      case '\\' => res + '\\'
      case c => res + c
    }))

  def pp(str: String): String =
    str.foldLeft("")((res, c) => c match {
      case '\u0008' => res + "\\b"
      case '\t' => res + "\\t"
      case '\n' => res + "\\n"
      case '\f' => res + "\\f"
      case '\r' => res + "\\r"
      case '\u000b' => res + "\\v"
      case '"' => res + "\\\""
      case '\'' => res + "'"
      case '\\' => res + "\\"
      case c => res + c
    })

  def getIndent(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    for (i <- 0 to indent - 1) s.append("  ")
    s.toString
  }
  def join(indent: Int, all: List[Node], sep: String, result: StringBuilder): StringBuilder = all match {
    case Nil => result
    case _ => result.length match {
      case 0 => {
        join(indent, all.tail, sep, result.append(all.head.toString(indent)))
      }
      case _ =>
        if (result.length > PRINT_WIDTH && sep.equals(", "))
          join(indent, all.tail, sep, result.append(", " + LINE_SEP + getIndent(indent)).append(all.head.toString(indent)))
        else
          join(indent, all.tail, sep, result.append(sep).append(all.head.toString(indent)))
    }
  }

  ////////////////////////////////////////////////////////////////
  // AST
  ////////////////////////////////////////////////////////////////

  /*  make sure it is parenthesized */
  def prBody(body: List[SourceElement]): String =
    join(0, body, LINE_SEP, new StringBuilder("")).toString

  def makeASTNodeInfo(span: Span): ASTNodeInfo =
    if (keepComments && comment.isDefined) {
      val result = new ASTNodeInfo(span, comment)
      comment = None
      result
    } else new ASTNodeInfo(span, None)

  def escape(s: String): String = s.replaceAll("\\\\", "\\\\\\\\")
  def unescape(s: String): String = s.replaceAll("\\\\", "")

  def lineTerminating(c: Char): Boolean =
    List('\u000a', '\u2028', '\u2029', '\u000d').contains(c)

  def setKeepComments(flag: Boolean): Unit = { keepComments = flag }

  def initComment: Unit = { comment = None }

  def commentLog(span: Span, message: String): Unit =
    if (keepComments) {
      if (!comment.isDefined ||
        (!comment.get.txt.startsWith("/*") && !comment.get.txt.startsWith("//")))
        comment = Some[Comment](new Comment(makeASTNodeInfo(span), message))
      else {
        val com = comment.get
        if (!com.txt.equals(message))
          comment = Some[Comment](new Comment(
            makeASTNodeInfo(com.info.span + span),
            com.txt + LINE_SEP + message
          ))
      }
    }

  def adjustCallSpan(finish: Span, expr: LHS): Span = expr match {
    case Parenthesized(info, body) => body.span + finish
    case _ => finish
  }

  def inParentheses(str: String): String = {
    val charArr = str.toCharArray
    var parenthesized = true
    var depth = 0
    for (
      c <- charArr if parenthesized
    ) {
      if (c == '(') depth += 1
      else if (c == ')') depth -= 1
      else if (depth == 0) parenthesized = false
    }
    if (parenthesized) str
    else new StringBuilder("(").append(str).append(")").toString
  }

  def prFtn(s: StringBuilder, indent: Int, fds: List[FunDecl], vds: List[VarDecl],
    body: List[SourceElement]): Unit = {
    fds match {
      case Nil =>
      case _ =>
        s.append(getIndent(indent + 1)).append(join(indent + 1, fds, LINE_SEP + getIndent(indent + 1), new StringBuilder("")))
        s.append(LINE_SEP).append(getIndent(indent))
    }
    vds match {
      case Nil =>
      case _ =>
        s.append(getIndent(indent + 1))
        vds.foreach(vd => vd match {
          case VarDecl(_, n, _, _) =>
            s.append("var " + n.text + ";" + LINE_SEP + getIndent(indent + 1))
        })
        s.append(LINE_SEP).append(getIndent(indent))
    }
    s.append(getIndent(indent + 1)).append(join(indent + 1, body, LINE_SEP + getIndent(indent + 1), new StringBuilder("")))
  }

  def prUseStrictDirective(s: StringBuilder, indent: Int, fds: List[FunDecl], vds: List[VarDecl], body: SourceElements): Unit =
    prUseStrictDirective(s, indent, fds, vds, List(body))

  def prUseStrictDirective(s: StringBuilder, indent: Int, fds: List[FunDecl], vds: List[VarDecl], stmts: List[SourceElements]): Unit =
    fds.find(fd => fd.strict) match {
      case Some(_) => s.append(getIndent(indent)).append("\"use strict\";").append(LINE_SEP)
      case None => vds.find(vd => vd.strict) match {
        case Some(_) => s.append(getIndent(indent)).append("\"use strict\";").append(LINE_SEP)
        case None => stmts.find(stmts => stmts.strict) match {
          case Some(_) => s.append(getIndent(indent)).append("\"use strict\";").append(LINE_SEP)
          case None =>
        }
      }
    }

  object AddLinesProgram extends ASTWalker {
    var line = 0
    var offset = 0
    def addLines(node: Program, l: Int, o: Int): Program = {
      line = l; offset = o
      map = new HashMap[String, Span]
      walk(node)
    }

    // filter "(" for property access
    def getStartSpan(node: ASTNode): (ASTNode, Span) = {
      if (node.isInstanceOf[Parenthesized]) {
        getStartSpan(node.asInstanceOf[Parenthesized].expr)
      } else if (node.isInstanceOf[Dot]) {
        getStartSpan(node.asInstanceOf[Dot].obj)
      } else if (node.isInstanceOf[FunApp]) {
        getStartSpan(node.asInstanceOf[FunApp].fun)
      } else (node, node.info.span)
    }

    var map = new HashMap[String, Span]
    override def walk(i: ASTNodeInfo): ASTNodeInfo = {
      val span = i.span
      val key = span.toString
      if (map.contains(key)) new ASTNodeInfo(map.apply(key))
      else {
        val newSpan = span.addLines(line, offset)
        map += (key -> newSpan)
        new ASTNodeInfo(newSpan)
      }
    }

    override def walk(node: FunDecl): FunDecl = node match {
      case FunDecl(i, getFtn, isStrict) =>
        val span = i.span
        val key = span.toString
        val newInfo = if (map.contains(key)) new ASTNodeInfo(map.apply(key))
        else {
          val newSpan = span.addLines(line, offset)
          offset = 0
          map += (key -> newSpan)
          new ASTNodeInfo(newSpan)
        }
        super.walk(new FunDecl(newInfo, getFtn, isStrict))
    }

    override def walk(node: Expr): Expr = node match {
      case dot @ Dot(info, lhs, id) =>
        val (nlhs, sSpan) = getStartSpan(lhs)
        // make new SpanInfo...
        if (lhs != nlhs) {
          val eSpan = info.span
          val newSpan = sSpan + eSpan
          val newInfo = new ASTNodeInfo(newSpan)
          val key = newSpan.toString
          if (!map.contains(key))
            map += (key -> newSpan)
          super.walk(new Dot(newInfo, lhs, id))
        } else super.walk(dot)
      case f @ FunApp(info, lhs, list) =>
        val (nlhs, sSpan) = getStartSpan(lhs)
        // make new SpanInfo...
        if (lhs != nlhs) {
          val eSpan = info.span
          val newSpan = sSpan + eSpan
          val newInfo = new ASTNodeInfo(newSpan)
          val key = newSpan.toString
          if (!map.contains(key))
            map += (key -> newSpan)
          super.walk(new FunApp(newInfo, lhs, list))
        } else super.walk(f)
      case _ => super.walk(node)
    }
  }

  // Assumes that the filename remains the same.
  object AddLinesWalker extends ASTWalker {
    var line = 0
    var offset = 0
    def addLines(node: Expr, l: Int, o: Int): Expr = {
      line = l; offset = o
      map = new HashMap[String, Span]
      walk(node)
    }
    def addLines(node: LHS, l: Int, o: Int): LHS = {
      line = l; offset = o
      map = new HashMap[String, Span]
      walk(node)
    }
    def addLines(node: FunExpr, l: Int, o: Int): FunExpr = {
      line = l; offset = o
      map = new HashMap[String, Span]
      walk(node)
    }
    var map = Map[String, Span]()

    def walk(e: FunExpr): FunExpr =
      FunExpr(walk(e.info), walk(e.ftn))

    override def walk(e: LHS): LHS = e match {
      case fe: FunExpr => walk(fe)
      case _ => super.walk(e)
    }

    override def walk(i: ASTNodeInfo): ASTNodeInfo = {
      val span = i.span
      val key = span.toString
      if (map.contains(key)) new ASTNodeInfo(map(key))
      else {
        val newSpan = span.addLines(line, offset)
        map += (key -> newSpan)
        new ASTNodeInfo(newSpan)
      }
    }
  }

  // AST: Remove empty blocks, empty statements, debugger statements, ...
  object SimplifyWalker extends ASTWalker {
    var repeat = false

    def simplify(stmts: List[SourceElement]): List[Stmt] = {
      repeat = false
      val simplified = simpl(stmts.map(_.asInstanceOf[Stmt]))
      val result = if (repeat) simplify(simplified) else simplified
      result
    }

    def simpl(stmts: List[Stmt]): List[Stmt] = stmts match {
      case Nil => Nil
      case stmt :: rest => stmt match {
        case _: Debugger =>
          repeat = true; simplify(rest)
        case _: EmptyStmt =>
          repeat = true; simplify(rest)
        case ABlock(_, Nil, _) =>
          repeat = true; simplify(rest)
        case ABlock(_, ABlock(_, Nil, _) :: stmts, _) =>
          repeat = true;
          simplify(stmts) ++ simplify(rest)
        case ABlock(_, ABlock(_, ss, _) :: stmts, _) =>
          repeat = true;
          simplify(ss) ++ simplify(stmts) ++ simplify(rest)
        case ABlock(_, s @ List(stmt), _) =>
          repeat = true;
          simplify(s) ++ simplify(rest)
        case ABlock(info, sts, b) =>
          repeat = true;
          List(ABlock(info, simplify(sts), b)) ++ simplify(rest)
        case _ => List(stmt) ++ simplify(rest)
      }
    }

    override def walk(node: FunDecl): FunDecl = node match {
      case FunDecl(info, ftn, isStrict) =>
        FunDecl(walk(info), walk(ftn), isStrict)
    }

    override def walk(node: Program): Program = node match {
      case Program(info, TopLevel(i, fds, vds, program)) =>
        Program(info, TopLevel(i, fds.map(walk), vds,
          program.map(ss => ss match {
            case SourceElements(i, s, f) =>
              SourceElements(i, simplify(s.map(walk)), f)
          })))
    }

    override def walk(node: Stmt): Stmt = node match {
      case ABlock(info, List(stmt), b) =>
        ABlock(info, List(walk(stmt)), b)
      case ABlock(info, ABlock(_, Nil, _) :: stmts, b) => walk(ABlock(info, stmts, b))
      case ABlock(info, ABlock(_, ss, _) :: stmts, b) => walk(ABlock(info, ss ++ stmts, b))
      case ABlock(info, stmts, b) =>
        ABlock(info, simplify(stmts.map(walk)), b)
      case Switch(info, cond, frontCases, Some(stmts), backCases) =>
        Switch(info, cond, frontCases.map(walk),
          Some(simplify(stmts.map(walk))), backCases.map(walk))
      case _ => super.walk(node)
    }

    override def walk(node: Functional): Functional = node match {
      case Functional(i, fds, vds, SourceElements(info, body, strict), name, params, bodyS) =>
        Functional(i, fds.map(walk), vds,
          SourceElements(info, simplify(body.map(walk)), strict),
          name, params, bodyS)
    }
  }

  ////////////////////////////////////////////////////////////////
  // IR
  ////////////////////////////////////////////////////////////////

  def inlineIndent(stmt: IRStmt, s: StringBuilder, indent: Int): Unit = {
    stmt match {
      case IRStmtUnit(_, stmts) if stmts.length != 1 =>
        s.append(getIndent(indent)).append(stmt.toString(indent))
      case IRSeq(_, _) =>
        s.append(getIndent(indent)).append(stmt.toString(indent))
      case _ =>
        s.append(getIndent(indent + 1)).append(stmt.toString(indent + 1))
    }
  }

  // IR: Remove empty blocks, empty statements, ...
  // Do not remove IRSeq aggressively.
  // They denote internal IRStmts whose values do not contribute to the result.
  object SimplifyIRWalker extends IRWalker {
    override def walk(node: IRRoot): IRRoot = node match {
      case IRRoot(ast, fds, vds, irs) =>
        IRRoot(ast, fds.map { fd: IRFunDecl => walk(fd) }, vds, simplify(irs))
    }

    override def walk(node: IRFunctional): IRFunctional = node match {
      case IRFunctional(astF, f, n, params, args, fds, vds, body) =>
        IRFunctional(astF, f, n, params, simplify(args),
          fds.map { fd: IRFunDecl => walk(fd) }, vds, simplify(body))
    }

    override def walk(node: IRFunDecl): IRFunDecl = node match {
      case IRFunDecl(ast, ftn) =>
        IRFunDecl(ast, walk(ftn))
    }

    override def walk(node: IRStmt): IRStmt = node match {
      case IRStmtUnit(ast, stmts) =>
        IRStmtUnit(ast, simplify(stmts))

      case IRSeq(ast, stmts) =>
        IRSeq(ast, simplify(stmts))

      case _ => super.walk(node)
    }

    // Simplify a list of IRStmts
    def simplify(stmts: List[IRStmt]): List[IRStmt] = stmts match {
      case Nil => Nil
      case stmt :: rest => stmt match {
        // Remove an empty internal IRStmt list
        case IRSeq(_, Nil) => simplify(rest)

        // Remove a self assignment IRStmt
        case IRExprStmt(_, lhs, rhs: IRId, ref) if lhs.uniqueName.equals(rhs.uniqueName) => simplify(rest)

        // Simplify the following case:
        //     <>ignore<>1 = expr
        //     <>temp = <>ignore<>1
        // to the following:
        //     <>temp = expr
        /*
        case first:IRAssign => rest match {
          case (second@SIRExprStmt(_, _, _, right:IRId, _))::others =>
            if (first.getLhs.getUniqueName.equals(right.getUniqueName) &&
                right.getUniqueName.equals(IGNORE_NAME)) {
              (walk(replaceLhs(first, second.getLhs)).asInstanceOf[IRStmt])::simpl(others)
            } else walk(first).asInstanceOf[IRStmt]::simpl(rest)
          case _ => walk(first).asInstanceOf[IRStmt]::simpl(rest)
        }
        */

        case _ => walk(stmt) :: simplify(rest)
      }
    }
  }
}
