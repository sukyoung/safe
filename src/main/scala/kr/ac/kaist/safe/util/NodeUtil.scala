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
import java.lang.{ Double => JDouble }
import java.lang.{ Integer => JInt }
import java.math.BigDecimal
import java.math.BigInteger
import scala.collection.immutable.HashMap

object NodeUtil {
  ////////////////////////////////////////////////////////////////
  // For all AST, IR, and CFG
  ////////////////////////////////////////////////////////////////

  val significantBits = 13

  // Spans ///////////////////////////////////////////////////////
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
  def getSpan(n: ASTNodeInfo): Span = n.span
  def getFileName(n: ASTNode): String = getSpan(n).fileName
  def getBegin(n: ASTNode): SourceLoc = getSpan(n).begin
  def getEnd(n: ASTNode): SourceLoc = getSpan(n).end
  def getLine(n: ASTNode): Int = getSpan(n).begin.line
  def getOffset(n: ASTNode): Int = getSpan(n).begin.offset

  def spanAll(span1: Span, span2: Span): Span =
    new Span(span1.begin, span2.end)

  // Names ///////////////////////////////////////////////////////
  // unique name generation
  var uid = 0
  def getUId: Int = { uid += 1; uid }
  def freshName(n: String): String =
    internalSymbol + n + internalSymbol + "%013d".format(getUId)
  // unique name generation for global names
  def freshGlobalName(n: String): String = globalPrefix + n
  def funexprName(span: Span): String = freshName("funexpr@" + span.toStringWithoutFiles)

  def isInternal(s: String): Boolean = s.containsSlice(internalSymbol)
  def isGlobalName(s: String): Boolean = s.startsWith(globalPrefix)
  def isFunExprName(name: String): Boolean = name.containsSlice("<>funexpr")

  val internalSymbol = "<>"
  val internalPrint = "_<>_print"
  val internalPrintIS = "_<>_printIS"
  val internalGetTickCount = "_<>_getTickCount"
  val globalPrefix = "<>Global<>"
  val generatedString = "<>generated String Literal"
  val varTrue = freshGlobalName("true")
  val varOne = freshGlobalName("one")
  val toObjectName = freshGlobalName("toObject")
  val ignoreName = freshGlobalName("ignore")
  val globalName = freshGlobalName("global")
  val referenceErrorName = freshGlobalName("referenceError")

  // Defaults ////////////////////////////////////////////////////
  // dummy file name for source location information
  def freshFile(f: String): String = internalSymbol + f
  // For use only when there is no hope of attaching a true span.
  def defaultSpan(villain: String): Span =
    if (villain.length != 0) makeSpan(villain) else defaultSpan
  def defaultSpan: Span = makeSpan("defaultSpan")

  var nodesPrintId = 0
  var nodesPrintIdEnv: List[(String, String)] = Nil
  def initNodesPrint: Unit = {
    nodesPrintId = 0
    nodesPrintIdEnv = Nil
  }
  def getNodesE(uniq: String): String = nodesPrintIdEnv.find(p => p._1.equals(uniq)) match {
    case None =>
      val new_uniq = { nodesPrintId += 1; nodesPrintId.toString }
      nodesPrintIdEnv = (uniq, new_uniq) :: nodesPrintIdEnv
      new_uniq
    case Some((_, new_uniq)) => new_uniq
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
    for (i <- 0 to indent - 1) s.append(" ")
    s.toString
  }

  val printWidth = 50
  def join(indent: Int, all: List[Node], sep: String, result: StringBuilder): StringBuilder = all match {
    case Nil => result
    case _ => result.length match {
      case 0 => {
        join(indent, all.tail, sep, result.append(all.head.toString(indent)))
      }
      case _ =>
        if (result.length > printWidth && sep.equals(", "))
          join(indent, all.tail, sep, result.append(", " + Config.LINE_SEP + getIndent(indent)).append(all.head.toString(indent)))
        else
          join(indent, all.tail, sep, result.append(sep).append(all.head.toString(indent)))
    }
  }

  ////////////////////////////////////////////////////////////////
  // AST
  ////////////////////////////////////////////////////////////////

  // For use only when there is no hope of attaching a true span.
  val defaultAst = makeNoOp(makeASTNodeInfo(defaultSpan("defaultAST")), "defaultAST")

  /*  make sure it is parenthesized */
  def prBody(body: List[SourceElement]): String =
    join(0, body, Config.LINE_SEP, new StringBuilder("")).toString

  def getBody(ast: ASTNode): String = ast match {
    case FunExpr(_, Functional(_, _, _, _, _, _, bodyS)) => bodyS
    case FunDecl(_, Functional(_, _, _, _, _, _, bodyS), _) => bodyS
    case GetProp(_, _, Functional(_, _, _, _, _, _, bodyS)) => bodyS
    case SetProp(_, _, Functional(_, _, _, _, _, _, bodyS)) => bodyS
    case _ => "Not a function body"
  }

  def isName(lhs: LHS): Boolean = lhs match {
    case _: VarRef => true
    case _: Dot => true
    case _ => false
  }

  def isEval(n: Expr): Boolean = n match {
    case VarRef(info, Id(_, text, _, _)) => text.equals("eval")
    case _ => false
  }

  def makeASTNodeInfo(span: Span): ASTNodeInfo =
    if (getKeepComments && comment.isDefined) {
      val result = new ASTNodeInfo(span, comment)
      comment = None
      result
    } else new ASTNodeInfo(span, None)

  def makeProgram(info: ASTNodeInfo, ses: List[SourceElements]): Program =
    new Program(info, new TopLevel(info, Nil, Nil, ses))

  def makeNoOp(info: ASTNodeInfo, desc: String): NoOp =
    new NoOp(info, desc)

  /*
     * DecimalLiteral ::=
     *   DecimalIntegerLiteral . DecimalDigits? ExponentPart?
     * | DecimalIntegerLiteral ExponentPart?
     * | . DecimalDigits ExponentPart?
     *
     * DecimalIntegerLiteral ::=
     *   0
     * | NonZeroDigit DecimalDigits?
     *
     * DecimalDigit ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
     *
     * NonZeroDigit ::= 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
     *
     * ExponentPart ::= (e | E) (+ | -)? DecimalDigit+
     */
  def makeNumberLiteral(writer: BufferedWriter, span: Span,
    beforeDot: String, dot: String,
    afterDot: String, exponent: String): NumberLiteral = {
    if ((beforeDot + dot).equals("") ||
      ((beforeDot + afterDot).equals("") && !dot.equals("")) ||
      (!beforeDot.equals("") && dot.equals("") && !afterDot.equals("")))
      log(writer, "Syntax Error: expected a numeral but got " +
        beforeDot + dot + afterDot + exponent)
    if (!beforeDot.equals("") && !beforeDot.equals("0") && beforeDot.charAt(0) == '0')
      log(writer, "Syntax Error: a numeral begins with 0.")
    if (dot.equals("")) {
      if (exponent.equals("")) new IntLiteral(makeASTNodeInfo(span), new BigInteger(beforeDot), 10)
      else {
        var exp = 0
        val second = exponent.charAt(1)
        if (Character.isDigit(second))
          exp = JInt.parseInt(exponent.substring(1))
        else if (second.equals('-'))
          exp = -1 * JInt.parseInt(exponent.substring(2))
        else exp = JInt.parseInt(exponent.substring(2))
        if (exp < 0) {
          var str = beforeDot + dot + afterDot + exponent
          str = new BigDecimal(str).toString
          new DoubleLiteral(makeASTNodeInfo(span), str, JDouble.valueOf(str))
        } else new IntLiteral(
          makeASTNodeInfo(span),
          new BigInteger(beforeDot).multiply(BigInteger.TEN.pow(exp)),
          10
        )
      }
    } else {
      val str = beforeDot + dot + afterDot + exponent
      new DoubleLiteral(makeASTNodeInfo(span), str, JDouble.valueOf(str))
    }
  }

  def unwrapParen(expr: Expr): Expr = expr match {
    case Parenthesized(info, body) => body
    case _ => expr
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

  def escape(s: String): String = s.replaceAll("\\\\", "\\\\\\\\")

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

  private var keepComments = false
  def setKeepComments(flag: Boolean): Unit = { keepComments = flag }
  def getKeepComments: Boolean = keepComments

  private var comment: Option[Comment] = None
  def initComment: Unit = { comment = None }
  def commentLog(span: Span, message: String): Unit =
    if (getKeepComments) {
      if (!comment.isDefined ||
        (!comment.get.txt.startsWith("/*") && !comment.get.txt.startsWith("//")))
        comment = Some[Comment](new Comment(makeASTNodeInfo(span), message))
      else {
        val com = comment.get
        if (!com.txt.equals(message))
          comment = Some[Comment](new Comment(
            makeASTNodeInfo(spanAll(com.info.span, span)),
            com.txt + Config.LINE_SEP + message
          ))
      }
    }

  def spanInfoAll(nodes: List[ASTNode]): ASTNodeInfo = new ASTNodeInfo(spanAll(nodes))

  def spanAll(nodes: List[ASTNode], span: Span): Span = nodes match {
    case Nil => span
    case _ => spanAll(nodes)
  }

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
      writer.write(msg + Config.LINE_SEP)
    } catch {
      case e: IOException =>
        sys.error("Writing to a log file for the parser failed!")
    }

  def isOneline(node: Any): Boolean = node match {
    case ABlock => false
    case Some(in) => isOneline(in)
    case _ => !(node.isInstanceOf[ABlock])
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
  def getSpan(n: IRNodeInfo): Span = n.span
  def getFileName(n: IRNode): String = n.info.span.fileName

  def makeIROp(name: String, kind: Int = 0): IROp =
    new IROp(new IRNodeInfo(defaultSpan(name), false, defaultAst), name,
      if (kind == 0) EJSOp.strToEJSOp(name) else kind)

  def isAssertOperator(op: IROp): Boolean = EJSOp.isEquality(op.kind)

  // Transposition rules for each relational IR Operator
  def transIROp(op: IROp): IROp = {
    op.kind match {
      case EJSOp.BIN_COMP_REL_LESS => makeIROp(">=") // < --> >=
      case EJSOp.BIN_COMP_REL_GREATER => makeIROp("<=") // > --> <=
      case EJSOp.BIN_COMP_REL_LESSEQUAL => makeIROp(">") // <= --> >
      case EJSOp.BIN_COMP_REL_GREATEREQUAL => makeIROp("<") // >= --> <
      case EJSOp.BIN_COMP_EQ_EQUAL => makeIROp("!=") // == --> !=
      case EJSOp.BIN_COMP_EQ_NEQUAL => makeIROp("==") // != --> ==
      case EJSOp.BIN_COMP_EQ_SEQUAL => makeIROp("!==") // === --> !==
      case EJSOp.BIN_COMP_EQ_SNEQUAL => makeIROp("===") // !== --> ===
      case EJSOp.BIN_COMP_REL_IN => makeIROp("notIn") // in --> notIn
      case EJSOp.BIN_COMP_REL_INSTANCEOF => makeIROp("notInstanceof") // instanceof --> notInstanceof
      case EJSOp.BIN_COMP_REL_NOTIN => makeIROp("in") // notIn --> in
      case EJSOp.BIN_COMP_REL_NOTINSTANCEOF => makeIROp("instanceof") // notInstanceof --> instanceof
      case _ => op
    }
  }

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
