/**
 * *****************************************************************************
 * Copyright (c) 2016-2020, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.ast_rewriter

import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.errors.warning._
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.parser.Parser
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span }

class BlockIdInstrumentor(program: Program) {
  ////////////////////////////////////////////////////////////////
  // results
  ////////////////////////////////////////////////////////////////

  lazy val result: Program = {
    val block = ABlockWalker.walk(program)
    val loop = LoopWalker.walk(block)
    //val circuit = ShortCircuitWalker.walk(loop)
    BlockListWalker.walk(loop)
    //val added = BlockIdWalker.walk(program)
    //LabelWalker.walk(added)
  }
  lazy val excLog: ExcLog = new ExcLog
  private var bId: Option[Int] = None
  private var uId: Int = 0
  private var outer: List[ASTNode] = List()
  def getId(): Option[Int] = {
    bId match {
      case None => bId = Some(0)
      case Some(id) => bId = Some(id + 1)
    }
    bId
  }
  def getUId(): Int = {
    val ret = uId
    uId += 1
    ret
  }

  private object BlockIdWalker extends ASTWalker {
    override def walk(node: TopLevel): TopLevel = {
      val newNode = node.copy(stmts = node.stmts.map({
        case stmts => stmts.copy(body = stmts.body match {
          case h :: t =>
            h.copyStmt(info = h.info.copy(blockId = getId)) :: t
          case ow =>
            ow
        })
      }))
      super.walk(newNode)
    }

    override def walk(node: Functional): Functional = {
      val newNode = node.copy(stmts = node.stmts.copy(body = node.stmts.body match {
        case h :: t =>
          h.copyStmt(info = h.info.copy(blockId = getId)) :: t
        case ow => ow
      }))
      super.walk(newNode)
    }

    override def walk(node: Stmt): Stmt = node match {
      case If(info, cond, trueB, falseB) =>
        val newInfo = info.copy(blockId = getId)
        val newTB = trueB.copyStmt(info = trueB.info.copy(blockId = getId))
        val newFB = falseB match {
          case Some(stmt) => Some(stmt.copyStmt(info = stmt.info.copy(blockId = getId)))
          case None => None
        }
        If(walk(newInfo), walk(cond), walk(newTB), newFB.map(walk))
      case _ =>
        super.walk(node)
    }

    override def walk(node: Expr): Expr = node match {
      case Cond(info, cond, trueB, falseB) =>
        val newInfo = info.copy(blockId = getId)
        val newTB = trueB.copyExpr(info = trueB.info.copy(blockId = getId))
        val newFB = falseB.copyExpr(info = falseB.info.copy(blockId = getId))
        Cond(walk(newInfo), walk(cond), walk(newTB), walk(newFB))
      case FunApp(info, fun, args) =>
        val newInfo = info.copy(blockId = getId)
        FunApp(walk(newInfo), walk(fun), args.map(walk))
      case _ =>
        super.walk(node)
    }
  }

  private object LabelWalker extends ASTWalker {
    override def walk(node: Stmt): Stmt = {
      val newNode = super.walk(node)
      newNode.info.blockId match {
        case Some(id) =>
          LabelStmt(newNode.info, Label(newNode.info, Id(newNode.info, "label_" + id.toString, None, false)), newNode)
        case None => newNode
      }
    }
  }

  private object BlockListWalker extends ASTWalker {
    def blockList(stmts: List[Stmt]): List[Stmt] = {
      val (outer, cur) = stmts.foldLeft[(List[Stmt], List[Stmt])]((List(), List()))({
        case (acc, stmt) =>
          val (outer, cur) = acc
          stmt match {
            case If(info, cond, trueB, falseB) =>
              var tmpId = Id(info, "____tmp_" + getUId, None, false)
              var varDecl = VarDecl(info, tmpId, Some(cond), false)
              val varStmt = VarStmt(info, List(varDecl))
              val varRef = VarRef(info, tmpId)

              val rcur = (varStmt :: cur).reverse
              val cBlock = ABlock(rcur.head.info, rcur, false)

              (If(info, varRef, trueB, falseB) :: cBlock :: outer, List())
            case stmt: ABlock =>
              if (cur.length > 0) {
                val rcur = cur.reverse
                val cBlock = ABlock(rcur.head.info, rcur, false)
                (stmt :: cBlock :: outer, List())
              } else {
                (stmt :: outer, cur)
              }
            case For(info, init, cond, action, body) =>
              if (cur.length > 0) {
                val rcur = cur.reverse
                val cBlock = ABlock(rcur.head.info, rcur, false)
                (stmt :: cBlock :: outer, List())
              } else {
                (stmt :: outer, cur)
              }
            case _ =>
              (outer, stmt :: cur)
          }
      })
      if (cur.length > 0) {
        val rcur = cur.reverse
        val cBlock = ABlock(rcur.head.info, rcur, false)
        (cBlock :: outer).reverse
      } else {
        outer.reverse
      }
    }

    override def walk(node: Stmts): Stmts = node match {
      case Stmts(info, body, isStrict) =>
        Stmts(walk(info), blockList(body.map(walk)), isStrict)
    }
    override def walk(node: Stmt): Stmt = node match {
      case ABlock(info, stmts, internal) =>
        stmts.foreach(b => println(b.toString(0)))
        val newStmts = blockList(stmts.map(walk)) match {
          case (h: ABlock) :: Nil => stmts
          case newStmts => newStmts
        }
        ABlock(walk(info), newStmts, internal)
      case _ => super.walk(node)
    }
  }

  private object ABlockWalker extends ASTWalker {
    override def walk(node: Stmt): Stmt = node match {
      case If(info, cond, trueB, falseB) =>
        val newTB = trueB match {
          case st: ABlock => st
          case st @ _ => ABlock(st.info, List(st), false)
        }
        val newFB = falseB match {
          case Some(st) => st match {
            case st: ABlock => falseB
            case st @ _ => Some(ABlock(st.info, List(st), false))
          }
          case None => falseB
        }
        If(walk(info), walk(cond), walk(newTB), newFB.map(walk))
      case DoWhile(info, body, cond) =>
        val newBody = body match {
          case st: ABlock => st
          case st @ _ => ABlock(st.info, List(st), false)
        }
        DoWhile(walk(info), walk(newBody), walk(cond))
      case While(info, cond, body) =>
        val newBody = body match {
          case st: ABlock => st
          case st @ _ => ABlock(st.info, List(st), false)
        }
        While(walk(info), walk(cond), walk(newBody))
      case For(info, init, cond, action, body) =>
        val newBody = body match {
          case st: ABlock => st
          case st @ _ => ABlock(st.info, List(st), false)
        }
        For(walk(info), init.map(walk), cond.map(walk), action.map(walk), walk(newBody))
      case ForIn(info, lhs, expr, body) =>
        val newBody = body match {
          case st: ABlock => st
          case st @ _ => ABlock(st.info, List(st), false)
        }
        ForIn(walk(info), walk(lhs), walk(expr), walk(newBody))
      case ForVar(info, vars, cond, action, body) =>
        val newBody = body match {
          case st: ABlock => st
          case st @ _ => ABlock(st.info, List(st), false)
        }
        ForVar(walk(info), vars.map(walk), cond.map(walk), action.map(walk), walk(newBody))
      case ForVarIn(info, vari, expr, body) =>
        val newBody = body match {
          case st: ABlock => st
          case st @ _ => ABlock(st.info, List(st), false)
        }
        ForVarIn(walk(info), walk(vari), walk(expr), walk(newBody))
      case With(info, expr, stmt) =>
        val newStmt = stmt match {
          case st: ABlock => st
          case st @ _ => ABlock(st.info, List(st), false)
        }
        With(walk(info), walk(expr), walk(newStmt))
      case _ => super.walk(node)
    }
  }

  private object LoopWalker extends ASTWalker {
    override def walk(node: Stmt): Stmt = node match {
      case DoWhile(info, body, cond) =>
        val newCond = walk(cond)
        val notCond = PrefixOpApp(newCond.info, Op(newCond.info, "!"), newCond)
        val newBody = walk(body)
        val added = newBody match {
          case ABlock(info, stmts, internal) => ABlock(info, stmts :+ If(notCond.info, notCond, ABlock(notCond.info, List(Break(notCond.info, None)), false), None), internal)
          case _ => newBody
        }
        For(walk(info), None, None, None, added)
      case While(info, cond, body) =>
        val newCond = walk(cond)
        val notCond = PrefixOpApp(newCond.info, Op(newCond.info, "!"), newCond)
        val newBody = walk(body)
        val added = newBody match {
          case ABlock(info, stmts, internal) => ABlock(info, If(notCond.info, notCond, ABlock(notCond.info, List(Break(notCond.info, None)), false), None) :: stmts, internal)
          case _ => newBody
        }
        For(walk(info), None, None, None, added)
      case For(info, init, cond, action, body) =>
        val newCond = cond.map(walk)
        val notCond = newCond match {
          case Some(newCond) => Some(PrefixOpApp(newCond.info, Op(newCond.info, "!"), newCond))
          case None => None
        }
        val newAction = action.map(walk)
        val newBody = walk(body)
        val added = newBody match {
          case ABlock(info, stmts, internal) =>
            val actionStmts = newAction match {
              case Some(newAction) =>
                stmts :+ ExprStmt(newAction.info, newAction, false)
              case None => stmts
            }
            val condStmts = notCond match {
              case Some(notCond) =>
                If(notCond.info, notCond, ABlock(notCond.info, List(Break(notCond.info, None)), false), None) :: actionStmts
              case None => actionStmts
            }
            ABlock(info, condStmts, internal)
          case _ => newBody
        }
        For(walk(info), init.map(walk), None, None, added)
      case _ => super.walk(node)
    }
  }

  (result, excLog)
}
