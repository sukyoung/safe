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

package kr.ac.kaist.safe.cfg_builder

import scala.collection.immutable.{ HashMap, HashSet }
import kr.ac.kaist.safe.config.Config
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.errors.warning._
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.cfg_builder.EdgeType._
import kr.ac.kaist.safe.phase.CFGBuildConfig
import kr.ac.kaist.safe.util.{ NodeUtil => NU, EJSLogNot }
import kr.ac.kaist.safe.analyzer.domain.Address

// default CFG builder
class DefaultCFGBuilder(
    ir: IRRoot,
    config: Config,
    cfgConfig: CFGBuildConfig
) extends CFGBuilder {
  ////////////////////////////////////////////////////////////////
  // results
  ////////////////////////////////////////////////////////////////

  lazy val (cfg: CFG, excLog: ExcLog) = init

  ////////////////////////////////////////////////////////////////
  // private global
  ////////////////////////////////////////////////////////////////

  // collect catch variable
  private var catchVarMap: Set[String] = _
  // captured variable set
  private var captured: Set[String] = _
  // unique id to CFG id
  private var cfgIdMap: Map[String, CFGId] = _
  // unique name counter
  private var uniqueNameCounter: Int = _
  // current function
  private var currentFunc: CFGFunction = _
  // JavaScript Label Map
  private trait JSLabel {
    def of(lmap: LabelMap): Set[CFGNormalBlock] =
      lmap.getOrElse(this, HashSet())
  }
  private case object RetLabel extends JSLabel
  private case object ThrowLabel extends JSLabel
  private case object ThrowEndLabel extends JSLabel
  private case object AfterCatchLabel extends JSLabel
  private case class UserLabel(label: String) extends JSLabel
  private type LabelMap = Map[JSLabel, Set[CFGNormalBlock]]

  ////////////////////////////////////////////////////////////////
  // main
  ////////////////////////////////////////////////////////////////

  build

  ////////////////////////////////////////////////////////////////
  // helper function
  ////////////////////////////////////////////////////////////////

  // initialize global variables
  private def init: (CFG, ExcLog) = {
    val cvResult = new CapturedVariableCollector(ir, config, cfgConfig)
    catchVarMap = HashSet()
    captured = cvResult.result
    cfgIdMap = HashMap()
    uniqueNameCounter = 0
    ir match {
      case IRRoot(_, fds, vds, _) =>
        val globalVars: List[CFGId] = namesOfFunDecls(fds) ++ namesOfVars(vds)
        val cfg = new CFG(globalVars, ir)
        currentFunc = cfg.globalFunc
        (cfg, cvResult.excLog)
    }
  }

  /* root rule : IRRoot -> CFG  */
  def build: Unit = ir match {
    case IRRoot(_, fds, _, stmts) =>
      val globalFunc = cfg.globalFunc
      val globalVars = globalFunc.localVars
      val startBlock: CFGNormalBlock = globalFunc.createBlock
      cfg.addEdge(globalFunc.entry, startBlock)

      translateFunDecls(fds, globalFunc, startBlock)
      val (blocks: List[CFGNormalBlock], lmap: LabelMap) = translateStmts(stmts, globalFunc, List(startBlock), HashMap())

      cfg.addEdge(blocks, globalFunc.exit)
      cfg.addEdge(ThrowLabel of lmap toList, globalFunc.exitExc, EdgeExc)
      cfg.addEdge(ThrowEndLabel of lmap toList, globalFunc.exitExc)
      cfg.addEdge(AfterCatchLabel of lmap toList, globalFunc.exitExc)
  }

  /* fdvars rule : IRFunDecl list -> LocalVars
   * collects variable names from sequence of IRFunDecl, function "name" ... */
  private def namesOfFunDecls(fds: List[IRFunDecl]): List[CFGId] = {
    fds.foldLeft(List[CFGId]())((vars, fd) => id2cfgId(fd.ftn.name) :: vars).reverse
  }

  /* vd* rule : IRVar list -> LocalVars
   * collects variable names from sequence of IRVarStmt, var "name" */
  private def namesOfVars(vds: List[IRVarStmt]): List[CFGId] = {
    vds.foldLeft(List[CFGId]())((vars, vd) => id2cfgId(vd.lhs) :: vars).reverse
  }

  // TODO: Is it okay not to flatten recursively?
  // flatten IRSeq
  private def flatten(stmts: List[IRStmt]): List[IRStmt] = {
    stmts.foldRight(List[IRStmt]())((stmt, l) => stmt match {
      case IRSeq(_, stmts) => stmts ++ l
      case _ => stmt :: l
    })
  }

  /* arg* rule : IRStmt list -> ArgVars
   * collects variable names from sequence of IRLoad, "name" := arguments[n] */
  private def namesOfArgs(loads: List[IRStmt]): List[CFGId] = {
    // When arguments may not be a list of IRExprStmts
    // because of using compiler.IRSimplifier
    // to move IRBin, IRUn, and IRLoad out of IRExpr
    flatten(loads).foldLeft(List[CFGId]())((args, load) => load match {
      case IRExprStmt(_, lhs, _, _) => lhs match {
        case IRUserId(_, _, _, _, _) => id2cfgId(lhs) :: args
        case lhs if lhs.originalName.startsWith("<>arguments") => id2cfgId(lhs) :: args
        case _ => args
      }
      case _ => args
    }).reverse
  }

  /* translate IRFunctional */
  private def translateFunctional(stmt: IRStmt, functional: IRFunctional): CFGFunction = functional match {
    case IRFunctional(_, _, name, params, args, fds, vds, body) =>
      val argVars: List[CFGId] = namesOfArgs(args)
      val localVars: List[CFGId] = (namesOfFunDecls(fds) ++ namesOfVars(vds)).filterNot(argVars.contains)
      // TODO: reorder to make argumentsName to the top
      val argumentsName: String = id2cfgId(params(1)).toString
      val nameStr: String = name.originalName
      val bodyStr: String = functional.ast.body

      val newFunc: CFGFunction = cfg.createFunction(argumentsName, argVars, localVars, nameStr, stmt, bodyStr, true)
      val oldFunc: CFGFunction = currentFunc
      currentFunc = newFunc

      val startBlock: CFGNormalBlock = newFunc.createBlock
      cfg.addEdge(newFunc.entry, startBlock)

      translateFunDecls(fds, newFunc, startBlock)
      val (blocks: List[CFGNormalBlock], lmap: LabelMap) = translateStmts(body, newFunc, List(startBlock), HashMap())

      cfg.addEdge(blocks, newFunc.exit)
      cfg.addEdge(RetLabel of lmap toList, newFunc.exit)
      cfg.addEdge(ThrowLabel of lmap toList, newFunc.exitExc, EdgeExc)
      cfg.addEdge(ThrowEndLabel of lmap toList, newFunc.exitExc)
      cfg.addEdge(AfterCatchLabel of lmap toList, newFunc.exitExc)

      currentFunc = oldFunc
      newFunc
  }

  /* fd* rule : IRFunDecl list x CFGFunction x CFGNormalBlock -> Unit */
  private def translateFunDecls(fds: List[IRFunDecl], func: CFGFunction, block: CFGNormalBlock): Unit = {
    fds.foreach(translateFunDecl(_, func, block))
  }

  /* fd rule : IRFunDecl x CFGFunction x CFGNormalBlock -> Unit */
  private def translateFunDecl(fd: IRFunDecl, func: CFGFunction, block: CFGNormalBlock): Unit = {
    // println ("[Func] %s".format(fd))
    fd match {
      case IRFunDecl(_, functional) =>
        val func: CFGFunction = translateFunctional(fd, functional)
        block.createInst(CFGFunExpr(func.ir, _, id2cfgId(functional.name), None, func, newProgramAddr, newProgramAddr, None))
        List(block)
    }
  }

  /* stmt* rule : IRStmt list x CFGFunction x CFGNormalBlock list x LabelMap x CFGNormalBlock option -> CFGNormalBlock list x LabelMap */
  private def translateStmts(stmts: List[IRStmt], func: CFGFunction, blocks: List[CFGNormalBlock], lmap: LabelMap): (List[CFGNormalBlock], LabelMap) = {
    stmts.foldLeft((blocks, lmap)) { case ((tails, lmap), stmt) => translateStmt(stmt, func, tails, lmap) }
  }

  /* stmt rule : IRStmt x CFGFunction x CFGNormalBlock list x LabelMap x CFGNormalBlock option -> CFGNormalBlock list x LabelMap */
  private def translateStmt(stmt: IRStmt, func: CFGFunction, blocks: List[CFGNormalBlock], lmap: LabelMap): (List[CFGNormalBlock], LabelMap) = {
    stmt match {
      case IRNoOp(_, desc) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        val filename: String = stmt.ast.info.span.fileName

        val block = (desc, filename) match {
          case ("StartOfFile", s) if s.contains("#loading") =>
            val entryBlock: CFGNormalBlock = func.createBlock
            cfg.addEdge(tailBlock, entryBlock)
            entryBlock
          case _ => tailBlock
        }
        block.createInst(CFGNoOp(stmt, _, desc))
        (List(block), lmap)
      case IRStmtUnit(_, stmts) =>
        translateStmts(stmts, func, blocks, lmap)
      case IRSeq(_, stmts) =>
        translateStmts(stmts, func, blocks, lmap)
      case vd: IRVarStmt =>
        excLog.signal(NotHoistedError(vd))
        (blocks, lmap)
      case fd: IRFunDecl =>
        excLog.signal(NotHoistedError(fd))
        (blocks, lmap)
      case IRFunExpr(_, lhs, functional) =>
        val newFunc: CFGFunction = translateFunctional(stmt, functional)
        val (addr1, addr2) = (newProgramAddr, newProgramAddr)
        val (nameOpt: Option[CFGId], addrOpt: Option[Address]) = id2cfgId(functional.name) match {
          case id if id.kind == CapturedVar => (Some(id), Some(newProgramAddr))
          case _ => (None, None)
        }
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        tailBlock.createInst(CFGFunExpr(newFunc.ir, _, id2cfgId(lhs), nameOpt, newFunc, addr1, addr2, addrOpt))
        (List(tailBlock), lmap)
      /* PEI : when proto is not object*/
      case IRObject(_, lhs, members, proto) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        var protoIdOpt: Option[CFGExpr] = proto.map(p => id2cfgExpr(p))
        tailBlock.createInst(CFGAlloc(stmt, _, id2cfgId(lhs), protoIdOpt, newProgramAddr))
        members.foreach(translateMember(_, tailBlock, lhs))
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      case irTry @ IRTry(_, body, name, catchIR, finIR) =>
        (name, catchIR, finIR) match {
          case (Some(x), Some(catb), None) =>
            catchVarMap += x.uniqueName

            /* try block */
            val tryBlock: CFGNormalBlock = func.createBlock
            cfg.addEdge(blocks, tryBlock)

            /* catch block */
            val catchBlock: CFGNormalBlock = func.createBlock
            catchBlock.createInst(CFGCatch(stmt, _, id2cfgId(x)))

            /* try body */
            val (trybs: List[CFGNormalBlock], trylmap: LabelMap) = translateStmt(body, func, List(tryBlock), HashMap())

            cfg.addEdge(ThrowLabel of trylmap toList, catchBlock, EdgeExc)
            cfg.addEdge(ThrowEndLabel of trylmap toList, catchBlock)
            cfg.addEdge(AfterCatchLabel of trylmap toList, catchBlock)

            /* catch body */
            val (catchbs: List[CFGNormalBlock], catchlmap: LabelMap) = translateStmt(catb, func, List(catchBlock), trylmap - ThrowLabel - ThrowEndLabel - AfterCatchLabel)

            /* tail blocks */
            val tailbs: List[CFGNormalBlock] = trybs match {
              case block :: Nil if (ThrowLabel of trylmap).contains(block) =>
                val newBlock: CFGNormalBlock = func.createBlock
                cfg.addEdge(trybs, newBlock)
                cfg.addEdge(catchbs, newBlock)
                List(newBlock)
              case _ => trybs ++ catchbs
            }
            val lm: LabelMap = catchlmap.foldLeft(trylmap) {
              case (m, (label, bs)) => m.contains(label) match {
                case true => m.updated(label, (label of m) ++ bs)
                case false => m.updated(label, bs)
              }
            }
            (tailbs, lm)
          case (None, None, Some(finb)) =>
            /* try block */
            val tryBlock: CFGNormalBlock = func.createBlock
            cfg.addEdge(blocks, tryBlock)

            /* finally block */
            val finBlock: CFGNormalBlock = func.createBlock

            /* try body */
            val (trybs: List[CFGNormalBlock], trylmap: LabelMap) = translateStmt(body, func, List(tryBlock), HashMap())

            /* finally body */
            val (finbs: List[CFGNormalBlock], finlmap: LabelMap) = translateStmt(finb, func, List(finBlock), lmap)

            /* edge : try -> finally */
            cfg.addEdge(trybs, finBlock)
            val reslmap = (trylmap - AfterCatchLabel).foldLeft(finlmap) {
              case (map, (label, bs)) => bs.isEmpty match {
                case false =>
                  val dupBlock: CFGNormalBlock = func.createBlock
                  val (bs: List[CFGNormalBlock], lm: LabelMap) = translateStmt(finb, func, List(dupBlock), map)
                  label match {
                    case ThrowLabel =>
                      cfg.addEdge(AfterCatchLabel of trylmap toList, dupBlock)
                      cfg.addEdge(bs.toList, dupBlock, EdgeExc)
                      lm.updated(ThrowEndLabel, (ThrowEndLabel of lm) ++ bs)
                    case _ =>
                      cfg.addEdge(bs.toList, dupBlock)
                      lm.updated(label, (label of lm) ++ bs)
                  }
                case true => map
              }
            }
            (finbs, reslmap)
          case (Some(x), Some(catb), Some(finb)) =>
            catchVarMap += x.uniqueName

            /* try block */
            val tryBlock: CFGNormalBlock = func.createBlock
            cfg.addEdge(blocks, tryBlock)

            /* catch block */
            val catchBlock: CFGNormalBlock = func.createBlock
            catchBlock.createInst(CFGCatch(stmt, _, id2cfgId(x)))

            /* finally block */
            val finBlock: CFGNormalBlock = func.createBlock

            /* try body */
            val (trybs: List[CFGNormalBlock], trylmap: LabelMap) = translateStmt(body, func, List(tryBlock), HashMap())

            cfg.addEdge(ThrowLabel of trylmap toList, catchBlock, EdgeExc)
            cfg.addEdge(ThrowEndLabel of trylmap toList, catchBlock)
            cfg.addEdge(AfterCatchLabel of trylmap toList, catchBlock)

            /* catch body */
            val (catchbs: List[CFGNormalBlock], catchlmap: LabelMap) = translateStmt(catb, func, List(catchBlock), trylmap - ThrowLabel - ThrowEndLabel - AfterCatchLabel)

            /* finally body */
            val (finbs: List[CFGNormalBlock], finlmap: LabelMap) = translateStmt(finb, func, List(finBlock), lmap)

            /* edge : try+catch -> finally */
            cfg.addEdge(trybs ++ catchbs, finBlock)
            val reslmap: LabelMap = (catchlmap - AfterCatchLabel).foldLeft(finlmap) {
              case (map, (label, bs)) => bs.isEmpty match {
                case false =>
                  val dupBlock: CFGNormalBlock = func.createBlock
                  val (bs: List[CFGNormalBlock], lm: LabelMap) = translateStmt(finb, func, List(dupBlock), map)
                  label match {
                    case ThrowLabel =>
                      cfg.addEdge(AfterCatchLabel of catchlmap toList, dupBlock)
                      cfg.addEdge(bs.toList, dupBlock, EdgeExc)
                      lm.updated(ThrowEndLabel, (ThrowEndLabel of lm) ++ bs)
                    case _ =>
                      cfg.addEdge(bs.toList, dupBlock)
                      lm.updated(label, (label of lm) ++ bs)
                  }
                case true => map
              }
            }
            (finbs, reslmap)
          case _ =>
            excLog.signal(WrongTryStmtError(irTry))
            (blocks, lmap)
        }
      /* PEI : element assign */
      case IRArgs(_, lhs, elements) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        tailBlock.createInst(CFGAllocArg(stmt, _, id2cfgId(lhs), elements.length, newProgramAddr))
        elements.zipWithIndex.foreach {
          case (Some(elem), idx) => translateElement(stmt, elem, tailBlock, lhs, idx)
          case _ =>
        }
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      /* PEI : element assign */
      case IRArray(_, lhs, elements) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        tailBlock.createInst(CFGAllocArray(stmt, _, id2cfgId(lhs), elements.length, newProgramAddr))
        elements.zipWithIndex.foreach {
          case (Some(elem), idx) => translateElement(stmt, elem, tailBlock, lhs, idx)
          case _ =>
        }
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      /* PEI : element assign */
      case IRArrayNumber(_, lhs, elements) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        tailBlock.createInst(CFGAllocArray(stmt, _, id2cfgId(lhs), elements.length, newProgramAddr))
        elements.zipWithIndex.foreach {
          case (elem, idx) => translateDoubleElement(stmt, elem, tailBlock, lhs, idx)
        }
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      case IRBreak(_, label) =>
        val key: String = label.uniqueName
        val bs: Set[CFGNormalBlock] = lmap.getOrElse(UserLabel(key), HashSet()) ++ blocks.toSet
        (Nil, lmap.updated(UserLabel(key), bs))
      /* PEI : fun == "<>toObject" */
      case IRInternalCall(_, lhs, fun @ (IRTmpId(_, originalName, uniqueName, _)), arg1, arg2) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        val (addr: Option[Address], lm: LabelMap) = uniqueName match {
          case "<>Global<>toObject" | "<>Global<>iteratorInit" => (Some(newProgramAddr), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
          case _ => (None, lmap)
        }
        val argList: List[CFGExpr] = arg2 match {
          case Some(arg) => List(ir2cfgExpr(arg1), id2cfgExpr(arg))
          case None => List(ir2cfgExpr(arg1))
        }
        tailBlock.createInst(CFGInternalCall(stmt, _, id2cfgId(lhs), id2cfgId(fun), argList, addr))
        (List(tailBlock), lm)
      /* PEI : call, after-call */
      case IRCall(_, lhs, fun, thisB, args) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        val f = tailBlock.func
        val call = f.createCall(CFGCall(stmt, _, id2cfgExpr(fun), id2cfgExpr(thisB), id2cfgExpr(args), newProgramAddr, newProgramAddr), id2cfgId(lhs))
        cfg.addEdge(tailBlock, call)

        val nextAfterCallBlock = f.createBlock
        val nextAfterCatchBlock = f.createBlock
        cfg.addEdge(call.afterCall, nextAfterCallBlock)
        cfg.addEdge(call.afterCatch, nextAfterCatchBlock)

        (List(nextAfterCallBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock).updated(AfterCatchLabel, (AfterCatchLabel of lmap) + nextAfterCatchBlock))
      /* PEI : construct, after-call */
      case IRNew(_, lhs, cons, args) if (args.length == 2) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        val f = tailBlock.func
        val call = f.createCall(CFGConstruct(stmt, _, id2cfgExpr(cons), id2cfgExpr(args(0)), id2cfgExpr(args(1)), newProgramAddr, newProgramAddr), id2cfgId(lhs))
        cfg.addEdge(tailBlock, call)

        val nextAfterCallBlock = f.createBlock
        val nextAfterCatchBlock = f.createBlock
        cfg.addEdge(call.afterCall, nextAfterCallBlock)
        cfg.addEdge(call.afterCatch, nextAfterCatchBlock)
        (List(nextAfterCallBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock).updated(AfterCatchLabel, (AfterCatchLabel of lmap) + nextAfterCatchBlock))
      case c @ IRNew(_, _, _, _) =>
        excLog.signal(NewArgNumError(c))
        (Nil, lmap)
      /* PEI : id lookup */
      case IRDelete(_, lhs, id) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        tailBlock.createInst(CFGDelete(stmt, _, id2cfgId(lhs), id2cfgExpr(id)))
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      /* PEI : id lookup */
      case IRDeleteProp(_, lhs, obj, index) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        tailBlock.createInst(CFGDeleteProp(stmt, _, id2cfgId(lhs), id2cfgExpr(obj), ir2cfgExpr(index)))
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      /* PEI : expr == IRId */
      case IRExprStmt(_, lhs, expr, _) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        tailBlock.createInst(CFGExprStmt(stmt, _, id2cfgId(lhs), ir2cfgExpr(expr)))
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      case IRIf(_, cond, trueIR, falseIR) =>
        /* true block */
        val trueBlock: CFGNormalBlock = func.createBlock
        cfg.addEdge(blocks, trueBlock)

        /* false block */
        val falseBlock: CFGNormalBlock = func.createBlock
        cfg.addEdge(blocks, falseBlock)

        /* Insert assert instructions */
        trueBlock.createInst(CFGAssert(cond, _, ir2cfgExpr(cond), true))
        cond match {
          case IRBin(_, first, op, second) if op.isAssertOperator =>
            falseBlock.createInst(CFGAssert(cond, _, CFGBin(ir2cfgExpr(first), op.kind.trans, ir2cfgExpr(second)), false))
          case _ =>
            falseBlock.createInst(CFGAssert(cond, _, CFGUn(EJSLogNot, ir2cfgExpr(cond)), false))
        }

        /* true body */
        val (truebs: List[CFGNormalBlock], truelmap: LabelMap) = translateStmt(trueIR, func, List(trueBlock), lmap)

        /* false body */
        val endBlock: CFGNormalBlock = func.createBlock
        falseIR match {
          case Some(stmt) =>
            val (falsebs: List[CFGNormalBlock], falselmap: LabelMap) = translateStmt(stmt, func, List(falseBlock), truelmap)
            cfg.addEdge(truebs ++ falsebs, endBlock)
            (List(endBlock), falselmap.updated(ThrowLabel, (ThrowLabel of falselmap) + trueBlock + falseBlock))
          case None =>
            cfg.addEdge(falseBlock :: truebs, endBlock)
            (List(endBlock), truelmap.updated(ThrowLabel, (ThrowLabel of truelmap) + trueBlock + falseBlock))
        }
      case IRLabelStmt(_, labelIR, stmt) =>
        val block: CFGNormalBlock = func.createBlock
        val (bs: List[CFGNormalBlock], lm: LabelMap) = translateStmt(stmt, func, blocks, lmap)
        val label: JSLabel = UserLabel(labelIR.uniqueName)
        cfg.addEdge(bs, block)
        cfg.addEdge(label of lm toList, block)
        (List(block), lm - label)
      /* PEI : expr lookup */
      case IRReturn(_, expr) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        tailBlock.createInst(CFGReturn(stmt, _, expr.map(ir2cfgExpr _)))
        (Nil, lmap.updated(RetLabel, (RetLabel of lmap) + tailBlock).updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      /* PEI : id lookup */
      case IRStore(_, obj, index, rhs) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        tailBlock.createInst(CFGStore(stmt, _, id2cfgExpr(obj), ir2cfgExpr(index), ir2cfgExpr(rhs)))
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      case IRThrow(_, expr) =>
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        tailBlock.createInst(CFGThrow(stmt, _, ir2cfgExpr(expr)))
        (Nil, lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      case IRWhile(_, cond, body) =>
        // Checks whether this while loop is originated from for-in or not.
        // TODO: Need to find a more graceful way.
        val bForin: Boolean = body match {
          case IRSeq(_, stmts) if stmts.size > 0 => stmts(0) match {
            case IRInternalCall(_, _, fun @ (IRTmpId(_, _, "<>Global<>iteratorNext", _)), _, _) => true
            case _ => false
          }
          case _ => false
        }

        /* tail block */
        val tailBlock: CFGNormalBlock = getTail(blocks, func)
        /* while loop head */
        val headBlock: CFGNormalBlock = func.createBlock
        /* loop body */
        val loopBodyBlock: CFGNormalBlock = func.createBlock
        /* loop out */
        val loopOutBlock: CFGNormalBlock = func.createBlock
        /* Insert assert instruction */
        loopBodyBlock.createInst(CFGAssert(cond, _, ir2cfgExpr(cond), true))
        cond match {
          case IRBin(_, first, op, second) if op.isAssertOperator =>
            loopOutBlock.createInst(CFGAssert(cond, _, CFGBin(ir2cfgExpr(first), op.kind.trans, ir2cfgExpr(second)), false))
          case _ =>
            loopOutBlock.createInst(CFGAssert(cond, _, CFGUn(EJSLogNot, ir2cfgExpr(cond)), false))
        }
        /* add edge from tail to loop head */
        cfg.addEdge(tailBlock, headBlock)
        /* add edge from loop head to loop body */
        cfg.addEdge(headBlock, loopBodyBlock)
        /* add edge from loop head to loop out*/
        cfg.addEdge(headBlock, loopOutBlock)
        /* build loop body */
        val (bs: List[CFGNormalBlock], lm: LabelMap) = translateStmt(body, func, List(loopBodyBlock), lmap)
        /* add edge from tails of loop body to loop head */
        cfg.addEdge(bs, headBlock)
        (List(loopOutBlock), lm.updated(ThrowLabel, (ThrowLabel of lm) + loopBodyBlock + loopOutBlock))
      case _ => {
        if (config.verbose || cfgConfig.verbose) excLog.signal(IRIgnored(stmt))
        (blocks, lmap)
      }
    }
    /* statements */
    //case IREval(info, lhs, _, arg) => (Nil, labelMap)
    //case IRWith(info, expr, stmt) => (Nil, labelMap)
    //case IRGetProp(info, fun) => (Nil, labelMap)
    //case IRSetProp(info, fun) => (Nil, labelMap)
  }

  /* mem rule : IRField x CFGNormalBlock x IRId -> Unit */
  private def translateMember(mem: IRMember, block: CFGNormalBlock, lhs: IRId): Unit = {
    mem match {
      case IRField(_, prop, expr) =>
        val lhsExpr: CFGVarRef = CFGVarRef(id2cfgId(lhs))
        val indexExpr: CFGString = CFGString(prop.uniqueName)
        block.createInst(CFGStore(mem, _, lhsExpr, indexExpr, ir2cfgExpr(expr)))
      case getOrSet =>
        excLog.signal(NotSupportedIRError(getOrSet))
    }
  }

  /* elem rule : IRNode x IRExpr x CFGNormalBlock x IRId x Int -> Unit */
  private def translateElement(ir: IRNode, elem: IRExpr, block: CFGNormalBlock, lhs: IRId, index: Int): Unit = {
    val lhsExpr: CFGExpr = CFGVarRef(id2cfgId(lhs))
    val str = CFGString(index.toString)
    block.createInst(CFGStore(ir, _, lhsExpr, str, ir2cfgExpr(elem)))
    ()
  }

  /* elem rule : IRNode x Double x CFGNormalBlock x IRId x Int -> Unit */
  private def translateDoubleElement(ir: IRNode, elem: Double, block: CFGNormalBlock, lhs: IRId, index: Int): Unit = {
    val lhsExpr: CFGExpr = CFGVarRef(id2cfgId(lhs))
    val str = CFGString(index.toString)
    val num = CFGNumber(elem.toString, elem.doubleValue)
    block.createInst(CFGStore(ir, _, lhsExpr, str, num))
    ()
  }

  private def isInternalCall(fname: String): Boolean = NU.isGlobalName(fname)
  private def ir2cfgExpr(expr: IRExpr): CFGExpr = {
    expr match {
      /* PEI : id lookup */
      case IRLoad(_, obj, index) =>
        CFGLoad(id2cfgExpr(obj), ir2cfgExpr(index))
      /* PEI : op \in {instanceof, in}, id lookup */
      case IRBin(_, first, op, second) =>
        CFGBin(ir2cfgExpr(first), op.kind, ir2cfgExpr(second))
      /* PEI : id lookup */
      case IRUn(_, op, expr) =>
        CFGUn(op.kind, ir2cfgExpr(expr))
      case id: IRId => CFGVarRef(id2cfgId(id))
      case IRThis(_) => CFGThis()
      case IRNumber(_, text, num) => CFGNumber(text, num.doubleValue)
      case IRString(_, str) => CFGString(str)
      case IRBool(_, bool) => CFGBool(bool)
      case IRNull(_) => CFGNull()
    }
  }

  ////////////////////////////////////////////////////////////////
  // Helper
  ////////////////////////////////////////////////////////////////

  // get tail block
  private def getTail(blocks: List[CFGNormalBlock], func: CFGFunction): CFGNormalBlock = {
    blocks match {
      case Nil => func.createBlock
      case block :: Nil => block
      case _ =>
        val tailBlock: CFGNormalBlock = func.createBlock
        blocks.foreach(cfg.addEdge(_, tailBlock))
        tailBlock
    }
  }

  // get unique name
  private def getUniqueName(text: String): String = {
    val name = if (!NU.isInternal(text) || NU.isGlobalName(text)) {
      text
    } else {
      uniqueNameCounter += 1
      text.dropRight(Config.SIGNIFICANT_BITS) + uniqueNameCounter.toString
    }
    name
  }

  // IR id to CFG expr
  private def id2cfgExpr(id: IRId): CFGExpr = CFGVarRef(id2cfgId(id))

  // IR id list to CFG id list
  private def idList2cfgIdList(id: List[IRId]): List[CFGId] = id.map(id2cfgId)

  // IR id to CFG id
  private def id2cfgId(id: IRId): CFGId = {
    val text: String = id.uniqueName
    cfgIdMap.getOrElse(text, {
      val name: String = getUniqueName(text)
      val cfgId: CFGId = id match {
        case IRUserId(_, originName, uniqueName, isGlobal, isWith) =>
          val kind: VarKind = if (isGlobal) GlobalVar
          else if (captured(text)) {
            if (catchVarMap(text)) CapturedCatchVar
            else CapturedVar
          } else PureLocalVar
          CFGUserId(name, kind, originName, isWith)
        case IRTmpId(_, originalName, uniqueName, isGlobal) =>
          if (isGlobal) CFGTempId(name, GlobalVar)
          else if (text.startsWith("<>arguments<>")) CFGUserId(name, PureLocalVar, "arguments", false)
          else CFGTempId(name, PureLocalVar)
      }
      cfgId match {
        case CFGUserId(_, kind, _, _) if kind == CapturedCatchVar || kind == CapturedVar => currentFunc.addCaptured(cfgId)
        case _ =>
      }
      cfgIdMap += (text -> cfgId)
      cfgId
    })
  }

  // get new program address
  private def newProgramAddr: Address = config.addrManager.newProgramAddr
}
