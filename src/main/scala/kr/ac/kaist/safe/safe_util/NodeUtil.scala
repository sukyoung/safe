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

package kr.ac.kaist.safe.safe_util

import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.safe_util.{ IRFactory => IF }
import java.io.BufferedWriter
import java.io.IOException
import scala.collection.immutable.HashMap

object NodeUtil {
  ////////////////////////////////////////////////////////////////
  // AST
  ////////////////////////////////////////////////////////////////

  def makeProgram(info: ASTNodeInfo, ses: List[SourceElements]): Program =
    new Program(info, new TopLevel(info, Nil, Nil, ses))

  def makeNoOp(info: ASTNodeInfo, desc: String): NoOp =
    new NoOp(info, desc)

  def unwrapParen(expr: Expr): Expr = expr match {
    case Parenthesized(info, body) => body
    case _ => expr
  }

  private var keepComments = false
  def setKeepComments(flag: Boolean): Unit = { keepComments = flag }
  def getKeepComments: Boolean = keepComments

  val internalSymbol = "<>"
  val internalPrint = "_<>_print"
  val internalPrintIS = "_<>_printIS"
  val internalGetTickCount = "_<>_getTickCount"
  val globalPrefix = "<>Global<>"
  // dummy file name for source location information
  def freshFile(f: String): String = internalSymbol + f
  // unique name generation for global names
  def freshGlobalName(n: String): String = globalPrefix + n
  val varTrue = freshGlobalName("true")
  val varOne = freshGlobalName("one")
  def funexprName(span: Span): String = freshName("funexpr@" + span.toStringWithoutFiles)
  val significantBits = 13
  // unique name generation
  var uid = 0
  def getUId: Int = { uid += 1; uid }
  def freshName(n: String): String =
    internalSymbol + n + internalSymbol + "%013d".format(getUId)
  def isInternal(s: String): Boolean = s.containsSlice(internalSymbol)
  def isGlobalName(s: String): Boolean = s.startsWith(globalPrefix)
  def isFunExprName(name: String): Boolean = name.containsSlice("<>funexpr")
  def isName(lhs: LHS): Boolean = lhs match {
    case _: VarRef => true
    case _: Dot => true
    case _ => false
  }
  def isEval(n: Expr): Boolean = n match {
    case VarRef(info, Id(_, text, _, _)) => text.equals("eval")
    case _ => false
  }

  val toObjectName = freshGlobalName("toObject")
  val ignoreName = freshGlobalName("ignore")

  def makeSpan(start: Span, finish: Span): Span =
    new Span(start.begin, finish.end)

  def makeSpan(file: String, startLine: Int, endLine: Int, startC: Int, endC: Int, startOffset: Int, endOffset: Int): Span =
    new Span(
      new SourceLoc(file, startLine, startC, startOffset),
      new SourceLoc(file, endLine, endC, endOffset)
    )

  def makeSpan(villain: String): Span = {
    val sl = new SourceLoc(villain, 0, 0, 0)
    new Span(sl, sl)
  }

  /**
   * In some situations, a begin-to-end span is not really right, and something
   * more like a set of spans ought to be used.  Even though this is not yet
   * implemented, the name is provided to allow expression of intent.
   */
  def getSpan(n: ASTNode): Span = n.info.span
  def getFileName(n: ASTNode): String = getSpan(n).fileName
  def getBegin(n: ASTNode): SourceLoc = getSpan(n).begin
  def getEnd(n: ASTNode): SourceLoc = getSpan(n).end
  def getLine(n: ASTNode): Int = getSpan(n).begin.line
  def getOffset(n: ASTNode): Int = getSpan(n).begin.offset

  def spanInfoAll(nodes: List[ASTNode]): ASTNodeInfo = new ASTNodeInfo(spanAll(nodes))

  def spanAll(nodes: List[ASTNode], span: Span): Span = nodes match {
    case Nil => span
    case _ => spanAll(nodes)
  }

  def spanAll(span1: Span, span2: Span): Span =
    new Span(span1.begin, span2.end)

  def spanAll(nodes: List[ASTNode]): Span = nodes match {
    case Nil => sys.error("Cannot make a span from an empty list of nodes.")
    case hd :: _ =>
      new Span(getSpan(hd).begin, getSpan(nodes.last).end)
  }

  def span(n: ASTNode): Span = n.info.span

  def adjustCallSpan(finish: Span, expr: LHS): Span = expr match {
    case Parenthesized(info, body) => new Span(span(body).begin, finish.end)
    case _ => finish
  }

  def log(writer: BufferedWriter, msg: String): Unit =
    try {
      writer.write(msg + "\n")
    } catch {
      case e: IOException =>
        sys.error("Writing to a log file for the parser failed!")
    }

  // merge the statements in each SourceElements
  // after hoisting
  def toStmts(sources: List[SourceElements]): List[Stmt] =
    sources.foldLeft(List[Stmt]())((l, s) => l ++ s.body.asInstanceOf[List[Stmt]])

  def getName(lhs: LHS): String = lhs match {
    case VarRef(_, id) => id.text
    case Dot(_, front, id) => getName(front) + "." + id.text
    case _: This => "this"
    case _ => ""
  }

  def prop2Id(prop: Property): Id = prop match {
    case PropId(info, id) => id
    case PropStr(info, str) => Id(info, str, None, false)
    case PropNum(info, DoubleLiteral(_, t, _)) => Id(info, t, None, false)
    case PropNum(info, IntLiteral(_, i, _)) => Id(info, i.toString, None, false)
  }

  def prop2Str(prop: Property): String = prop match {
    case PropId(_, id) => id.text
    case PropStr(_, str) => str
    case PropNum(info, DoubleLiteral(_, _, num)) => num.toString
    case PropNum(info, IntLiteral(_, num, _)) => num.toString
  }

  def member2Str(member: Member): String = member match {
    case Field(_, prop, _) => prop2Str(prop)
    case GetProp(_, prop, _) => prop2Str(prop)
    case SetProp(_, prop, _) => prop2Str(prop)
  }

  def escape(s: String): String =
    s.replaceAll("\\\\", "\\\\\\\\")

  def unescapeJava(s: String): String =
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
        } else {
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
                throw new IllegalArgumentException("incomplete universal character" +
                  " name " + s.substring(i - 1))
              }
              val n = Integer.parseInt(s.substring(i - 1, i + 1), 16)
              buf.append(n.asInstanceOf[Char])
            case 'u' =>
              i += 4
              if (i >= length) {
                throw new IllegalArgumentException("incomplete universal character" +
                  " name " + s.substring(i - 3))
              }
              val n = Integer.parseInt(s.substring(i - 3, i + 1), 16)
              buf.append(n.asInstanceOf[Char])
            case c if lineTerminating(c) =>
            case _ => buf.append(c)
          }
          i += 1
        }
      }
      buf.toString
    }

  def lineTerminating(c: Char): Boolean =
    List('\u000a', '\u2028', '\u2029', '\u000d').contains(c)

  object addLinesProgram extends ASTWalker {
    var line = 0
    var offset = 0
    def addLines(node: Node, l: Int, o: Int): Node = {
      line = l; offset = o
      map = new HashMap[String, Span]
      walk(node).asInstanceOf[Node]
    }

    // filter "(" for property access
    def getStartSourceLoc(node: ASTNode): (ASTNode, SourceLoc) = {
      if (node.isInstanceOf[Parenthesized]) {
        getStartSourceLoc(node.asInstanceOf[Parenthesized].expr)
      } else if (node.isInstanceOf[Dot]) {
        getStartSourceLoc(node.asInstanceOf[Dot].obj)
      } else if (node.isInstanceOf[FunApp]) {
        getStartSourceLoc(node.asInstanceOf[FunApp].fun)
      } else (node, node.info.span.begin)
    }

    var map = new HashMap[String, Span]
    override def walk(node: Any): Any = {
      node match {
        case f: FunDecl =>
          f match {
            case FunDecl(i, getFtn, isStrict) =>
              val span = i.span
              val key = span.at
              val newInfo = if (map.contains(key)) new ASTNodeInfo(map.apply(key))
              else {
                val newSpan = span.addLines(line, offset)
                offset = 0
                map += (key -> newSpan)
                new ASTNodeInfo(newSpan)
              }
              super.walk(new FunDecl(newInfo, getFtn, isStrict))
          }
        case i: ASTNodeInfo =>
          val span = i.span
          val key = span.at
          if (map.contains(key)) new ASTNodeInfo(map.apply(key))
          else {
            val newSpan = span.addLines(line, offset)
            map += (key -> newSpan)
            new ASTNodeInfo(newSpan)
          }
        case dot: Dot => {
          dot match {
            case Dot(info, lhs, id) =>
              val (nlhs, s_offset) = getStartSourceLoc(lhs)
              // make new SpanInfo...
              if (lhs != nlhs) {
                val e_offset = info.span.end
                val newSpan = new Span(s_offset, e_offset)
                val newInfo = new ASTNodeInfo(newSpan)
                val key = newSpan.at
                if (!map.contains(key))
                  map += (key -> newSpan)
                super.walk(new Dot(newInfo, lhs, id))
              } else super.walk(node)
          }
        }
        case f: FunApp => {
          f match {
            case FunApp(info, lhs, list) =>
              val (nlhs, s_offset) = getStartSourceLoc(lhs)
              // make new SpanInfo...
              if (lhs != nlhs) {
                val e_offset = info.span.end
                val newSpan = new Span(s_offset, e_offset)
                val newInfo = new ASTNodeInfo(newSpan)
                val key = newSpan.at
                if (!map.contains(key))
                  map += (key -> newSpan)
                super.walk(new FunApp(newInfo, lhs, list))
              } else super.walk(node)
          }
        }
        case _: Comment => node
        case _ => super.walk(node)
      }
    }
  }

  // Assumes that the filename remains the same.
  object addLinesWalker extends ASTWalker {
    var line = 0
    var offset = 0
    def addLines(node: Node, l: Int, o: Int): Node = {
      line = l; offset = o
      map = new HashMap[String, Span]
      walk(node).asInstanceOf[Node]
    }
    var map = Map[String, Span]()
    override def walk(node: Any): Any = node match {
      case i: ASTNodeInfo =>
        val span = i.span
        val key = span.at
        if (map.contains(key)) new ASTNodeInfo(map(key))
        else {
          val newSpan = span.addLines(line, offset)
          map += (key -> newSpan)
          new ASTNodeInfo(newSpan)
        }
      case _: Comment => node
      case _ => super.walk(node)
    }
  }

  // AST: Remove empty blocks, empty statements, debugger statements, ...
  object simplifyWalker extends ASTWalker {
    var repeat = false
    def simplify(stmts: List[Stmt]): List[Stmt] = {
      repeat = false
      val simplified = simpl(stmts)
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

    override def walk(node: Any): Any = node match {
      case ABlock(info, List(stmt), b) =>
        ABlock(info, List(walk(stmt).asInstanceOf[Stmt]), b)
      case ABlock(info, ABlock(_, Nil, _) :: stmts, b) => walk(ABlock(info, stmts, b))
      case ABlock(info, ABlock(_, ss, _) :: stmts, b) => walk(ABlock(info, ss ++ stmts, b))
      case ABlock(info, stmts, b) =>
        ABlock(info, simplify(stmts.map(walk).asInstanceOf[List[Stmt]]), b)
      case Switch(info, cond, frontCases, Some(stmts), backCases) =>
        Switch(info, cond, super.walk(frontCases).asInstanceOf[List[Case]],
          Some(simplify(stmts.map(walk).asInstanceOf[List[Stmt]])),
          super.walk(backCases).asInstanceOf[List[Case]])
      case Program(info, TopLevel(i, fds, vds, program)) =>
        Program(info, TopLevel(i, super.walk(fds).asInstanceOf[List[FunDecl]], vds,
          program.map(ss => ss match {
            case SourceElements(i, s, f) =>
              SourceElements(i, simplify(s.map(walk).asInstanceOf[List[Stmt]]), f)
          })))
      case Functional(i, fds, vds, SourceElements(info, body, strict), name, params, bodyS) =>
        Functional(i, super.walk(fds).asInstanceOf[List[FunDecl]], vds,
          SourceElements(
            info,
            simplify(body.map(walk).asInstanceOf[List[Stmt]]),
            strict
          ), name, params, bodyS)
      case _: Comment => node
      case _ => super.walk(node)
    }
  }

  ////////////////////////////////////////////////////////////////
  // IR
  ////////////////////////////////////////////////////////////////
  def getSpan(n: IRNode): Span = n.info.span
  def getFileName(n: IRNode): String = n.info.span.fileName

  def isAssertOperator(op: IROp): Boolean = {
    EJSOp.isEquality(op.kind)
  }

  // Transposition rules for each relational IR Operator
  def transIROp(op: IROp): IROp = {
    op.kind match {
      case EJSOp.BIN_COMP_REL_LESS => IF.makeOp(">=") // < --> >=
      case EJSOp.BIN_COMP_REL_GREATER => IF.makeOp("<=") // > --> <=
      case EJSOp.BIN_COMP_REL_LESSEQUAL => IF.makeOp(">") // <= --> >
      case EJSOp.BIN_COMP_REL_GREATEREQUAL => IF.makeOp("<") // >= --> <
      case EJSOp.BIN_COMP_EQ_EQUAL => IF.makeOp("!=") // == --> !=
      case EJSOp.BIN_COMP_EQ_NEQUAL => IF.makeOp("==") // != --> ==
      case EJSOp.BIN_COMP_EQ_SEQUAL => IF.makeOp("!==") // === --> !==
      case EJSOp.BIN_COMP_EQ_SNEQUAL => IF.makeOp("===") // !== --> ===
      case EJSOp.BIN_COMP_REL_IN => IF.makeOp("notIn") // in --> notIn
      case EJSOp.BIN_COMP_REL_INSTANCEOF => IF.makeOp("notInstanceof") // instanceof --> notInstanceof
      case EJSOp.BIN_COMP_REL_NOTIN => IF.makeOp("in") // notIn --> in
      case EJSOp.BIN_COMP_REL_NOTINSTANCEOF => IF.makeOp("instanceof") // notInstanceof --> instanceof
      case _ => op
    }
  }

  // IR: Remove empty blocks, empty statements, ...
  // Do not remove IRSeq aggressively.
  // They denote internal IRStmts whose values do not contribute to the result.
  object simplifyIRWalker extends IRWalker {
    override def walk(node: Any): Any = node match {
      case IRRoot(info, fds, vds, irs) =>
        IRRoot(info, walk(fds).asInstanceOf[List[IRFunDecl]], vds, simplify(irs))

      case IRFunctional(i, f, n, params, args, fds, vds, body) =>
        IRFunctional(i, f, n, params, simplify(args), walk(fds).asInstanceOf[List[IRFunDecl]], vds, simplify(body))

      case IRStmtUnit(info, stmts) =>
        IRStmtUnit(info, simplify(stmts))

      case IRSeq(info, stmts) =>
        IRSeq(info, simplify(stmts))

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
                right.getUniqueName.equals(ignoreName)) {
              (walk(replaceLhs(first, second.getLhs)).asInstanceOf[IRStmt])::simpl(others)
            } else walk(first).asInstanceOf[IRStmt]::simpl(rest)
          case _ => walk(first).asInstanceOf[IRStmt]::simpl(rest)
        }
        */

        case _ => walk(stmt).asInstanceOf[IRStmt] :: simplify(rest)
      }
    }
  }

  ////////////////////////////////////////////////////////////////
  // CFG
  ////////////////////////////////////////////////////////////////

}
