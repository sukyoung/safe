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

import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.config.Config
import java.io.BufferedWriter
import java.io.IOException
import scala.collection.immutable.HashMap

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
  val INTERNAL_PRINT = "_<>_print"
  val INTERNAL_PRINT_IS = "_<>_printIS"
  val INTERNAL_GET_TICK_COUNT = "_<>_getTickCount"
  val GLOBAL_PREFIX = "<>Global<>"
  val GENERATED_STR = "<>generated String Literal"

  val VAR_TRUE = freshGlobalName("true")
  val VAR_ONE = freshGlobalName("one")
  val TO_OBJ_NAME = freshGlobalName("toObject")
  val IGNORE_NAME = freshGlobalName("ignore")
  val GLOBAL_NAME = freshGlobalName("global")
  val REF_ERR_NAME = freshGlobalName("referenceError")

  val MERGED_FILE_NAME = freshFile("Merged")
  val MERGED_SPAN = Span(MERGED_FILE_NAME)
  val MERGED_SOURCE_INFO = new ASTNodeInfo(MERGED_SPAN)

  val TEMP_AST = NoOp(ASTNodeInfo(Span()), "defaultAST")
  val TEMP_IR = IRNoOp(TEMP_AST, "defaultIR")

  val PRINT_WIDTH = 50

  ////////////////////////////////////////////////////////////////
  // For all AST, IR, and CFG
  ////////////////////////////////////////////////////////////////

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
          join(indent, all.tail, sep, result.append(", " + Config.LINE_SEP + getIndent(indent)).append(all.head.toString(indent)))
        else
          join(indent, all.tail, sep, result.append(sep).append(all.head.toString(indent)))
    }
  }

  ////////////////////////////////////////////////////////////////
  // AST
  ////////////////////////////////////////////////////////////////

  /*  make sure it is parenthesized */
  def prBody(body: List[SourceElement]): String =
    join(0, body, Config.LINE_SEP, new StringBuilder("")).toString

  def makeASTNodeInfo(span: Span): ASTNodeInfo =
    if (keepComments && comment.isDefined) {
      val result = new ASTNodeInfo(span, comment)
      comment = None
      result
    } else new ASTNodeInfo(span, None)

  def escape(s: String): String = s.replaceAll("\\\\", "\\\\\\\\")

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
            com.txt + Config.LINE_SEP + message
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
        s.append(getIndent(indent + 1)).append(join(indent + 1, fds, Config.LINE_SEP + getIndent(indent + 1), new StringBuilder("")))
        s.append(Config.LINE_SEP).append(getIndent(indent))
    }
    vds match {
      case Nil =>
      case _ =>
        s.append(getIndent(indent + 1))
        vds.foreach(vd => vd match {
          case VarDecl(_, n, _, _) =>
            s.append("var " + n.text + ";" + Config.LINE_SEP + getIndent(indent + 1))
        })
        s.append(Config.LINE_SEP).append(getIndent(indent))
    }
    s.append(getIndent(indent + 1)).append(join(indent + 1, body, Config.LINE_SEP + getIndent(indent + 1), new StringBuilder("")))
  }

  def prUseStrictDirective(s: StringBuilder, indent: Int, fds: List[FunDecl], vds: List[VarDecl], body: SourceElements): Unit =
    prUseStrictDirective(s, indent, fds, vds, List(body))

  def prUseStrictDirective(s: StringBuilder, indent: Int, fds: List[FunDecl], vds: List[VarDecl], stmts: List[SourceElements]): Unit =
    fds.find(fd => fd.strict) match {
      case Some(_) => s.append(getIndent(indent)).append("\"use strict\";").append(Config.LINE_SEP)
      case None => vds.find(vd => vd.strict) match {
        case Some(_) => s.append(getIndent(indent)).append("\"use strict\";").append(Config.LINE_SEP)
        case None => stmts.find(stmts => stmts.strict) match {
          case Some(_) => s.append(getIndent(indent)).append("\"use strict\";").append(Config.LINE_SEP)
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
