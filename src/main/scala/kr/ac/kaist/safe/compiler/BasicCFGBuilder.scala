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

package kr.ac.kaist.safe.compiler

import scala.collection.immutable.{ HashMap, HashSet }
import scala.collection.mutable.{ Map => MMap, HashMap => MHashMap, Set => MSet, HashSet => MHashSet }

import kr.ac.kaist.safe.Safe
import kr.ac.kaist.safe.exceptions.StaticError
import kr.ac.kaist.safe.exceptions.UserError
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.nodes.EdgeType._
import kr.ac.kaist.safe.safe_util.{ NodeUtil => NU, NodeFactory => NF, IRFactory => IF, CapturedVariableCollector }
import kr.ac.kaist.safe.safe_util.AddressManager._
import kr.ac.kaist.safe.useful.ErrorLog

// basic cfg builder
object BasicCFGBuilder extends CFGBuilder {
  // collect error logs
  private val errLog: ErrorLog = new ErrorLog

  // unique name helper
  private val cfgIdMap: MMap[String, CFGId] = MHashMap()
  private var uniqueNameCounter: Int = _
  private def getUniqueName(text: String): String = {
    val name = if (!NU.isInternal(text) || NU.isGlobalName(text)) {
      text
    } else {
      uniqueNameCounter += 1
      text.dropRight(NU.significantBits) + uniqueNameCounter.toString
    }
    name
  }

  // collect catch variable
  private val catchVarMap: MSet[String] = MHashSet()

  // captured variable set
  private var captured: Set[String] = _

  // JavaScript label
  trait JSLabel {
    def of(lmap: LabelMap): Set[Block] = lmap.getOrElse(this, HashSet())
  }
  case object RetLabel extends JSLabel
  case object ThrowLabel extends JSLabel
  case object ThrowEndLabel extends JSLabel
  case object AfterCatchLabel extends JSLabel
  case class UserLabel(label: String) extends JSLabel

  // JavaScript label map
  type LabelMap = Map[JSLabel, Set[Block]]

  // cfg
  private var cfg: CFG = _

  // current function
  var currentFunc: CFGFunction = _

  /* root rule : IRRoot -> CFG  */
  def build(ir: IRRoot): (CFG, List[StaticError]) = {
    // rerset global variables
    resetValues

    // find captured variable
    captured = CapturedVariableCollector.collect(ir)

    ir match {
      case IRRoot(info, fds, vds, stmts) =>
        val globalVars: List[CFGId] = namesOfFunDecls(fds) ++ namesOfVars(vds)
        cfg = new CFG(globalVars, info) // create initial cfg

        val globalFunc: CFGFunction = cfg.globalFunc
        currentFunc = globalFunc // set global function as current function

        val startBlock: Block = globalFunc.createBlock
        cfg.addEdge(globalFunc.entry, startBlock)

        translateFunDecls(fds, globalFunc, startBlock)
        val (blocks: List[Block], lmap: LabelMap) = translateStmts(stmts, globalFunc, List(startBlock), HashMap())

        cfg.addEdge(blocks, globalFunc.exit)
        cfg.addEdge(ThrowLabel of lmap toList, globalFunc.exitExc, EdgeExc)
        cfg.addEdge(ThrowEndLabel of lmap toList, globalFunc.exitExc)
        cfg.addEdge(AfterCatchLabel of lmap toList, globalFunc.exitExc)

        (cfg, errLog.asList)
    }
  }

  // reset global values
  private def resetValues: Unit = {
    errLog.errors = Nil
    cfgIdMap.clear
    uniqueNameCounter = 0
    catchVarMap.clear
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
      val info: Info = stmt.info
      val bodyStr: String = IF.getBody(info.ast)

      val newFunc: CFGFunction = cfg.createFunction(argumentsName, argVars, localVars, nameStr, info, bodyStr, true)
      val oldFunc: CFGFunction = currentFunc
      currentFunc = newFunc

      val startBlock: Block = newFunc.createBlock
      cfg.addEdge(newFunc.entry, startBlock)

      translateFunDecls(fds, newFunc, startBlock)
      val (blocks: List[Block], lmap: LabelMap) = translateStmts(body, newFunc, List(startBlock), HashMap())

      cfg.addEdge(blocks, newFunc.exit)
      cfg.addEdge(RetLabel of lmap toList, newFunc.exit)
      cfg.addEdge(ThrowLabel of lmap toList, newFunc.exitExc, EdgeExc)
      cfg.addEdge(ThrowEndLabel of lmap toList, newFunc.exitExc)
      cfg.addEdge(AfterCatchLabel of lmap toList, newFunc.exitExc)

      currentFunc = oldFunc
      newFunc
  }

  /* fd* rule : IRFunDecl list x CFGFunction x Block -> Unit */
  private def translateFunDecls(fds: List[IRFunDecl], func: CFGFunction, block: Block): Unit = {
    fds.foreach(translateFunDecl(_, func, block))
  }

  /* fd rule : IRFunDecl x CFGFunction x Block -> Unit */
  private def translateFunDecl(fd: IRFunDecl, func: CFGFunction, block: Block): Unit = {
    // println ("[Func] %s".format(fd))
    fd match {
      case IRFunDecl(info, functional) =>
        val func: CFGFunction = translateFunctional(fd, functional)
        block.createInst(CFGFunExpr(_, info, id2cfgId(functional.name), None, func.id, newProgramAddr, newProgramAddr, None))
        List(block)
    }
  }

  /* stmt* rule : IRStmt list x CFGFunction x Block list x LabelMap x Block option -> Block list x LabelMap */
  private def translateStmts(stmts: List[IRStmt], func: CFGFunction, blocks: List[Block], lmap: LabelMap, loopBlock: Option[Block] = None): (List[Block], LabelMap) = {
    stmts.foldLeft((blocks, lmap)) { case ((tails, lmap), stmt) => translateStmt(stmt, func, tails, lmap, loopBlock) }
  }

  /* stmt rule : IRStmt x CFGFunction x Block list x LabelMap x Block option -> Block list x LabelMap */
  private def translateStmt(stmt: IRStmt, func: CFGFunction, blocks: List[Block], lmap: LabelMap, loopBlock: Option[Block] = None): (List[Block], LabelMap) = {
    // println ("[Stmt] %s".format(stmt))
    stmt match {
      case IRNoOp(info, desc) =>
        val tailBlock: Block = getTail(blocks, func)
        val filename: String = info.span.end.fileName

        val block = (desc, filename) match {
          case ("StartOfFile", s) if s.contains("#loading") =>
            val entryBlock: Block = func.createBlock
            cfg.addEdge(tailBlock, entryBlock)
            entryBlock
          case _ => tailBlock
        }
        block.createInst(CFGNoOp(_, info, desc))
        (List(block), lmap)
      case IRStmtUnit(info, stmts) =>
        translateStmts(stmts, func, blocks, lmap, loopBlock)
      case IRSeq(info, stmts) =>
        translateStmts(stmts, func, blocks, lmap, loopBlock)
      case vd: IRVarStmt =>
        errLog.signal("IRVarStmt should have been hoisted.", vd)
        (blocks, lmap)
      case fd: IRFunDecl =>
        errLog.signal("IRFunDecl should have been hoisted.", fd)
        (blocks, lmap)
      case IRFunExpr(info, lhs, functional) =>
        val func: CFGFunction = translateFunctional(stmt, functional)
        val tailBlock: Block = getTail(blocks, func)
        val (addr1, addr2) = (newProgramAddr, newProgramAddr)
        val (nameOpt: Option[CFGId], addrOpt: Option[Address]) = id2cfgId(functional.name) match {
          case id if id.kind == CapturedVar => (Some(id), Some(newProgramAddr))
          case _ => (None, None)
        }
        tailBlock.createInst(CFGFunExpr(_, info, id2cfgId(lhs), nameOpt, func.id, addr1, addr2, addrOpt))
        (List(tailBlock), lmap)
      /* PEI : when proto is not object*/
      case IRObject(info, lhs, members, proto) =>
        val tailBlock: Block = getTail(blocks, func)
        var protoIdOpt: Option[CFGExpr] = proto.map(p => id2cfgExpr(p))
        tailBlock.createInst(CFGAlloc(_, info, id2cfgId(lhs), protoIdOpt, newProgramAddr))
        members.foreach(translateMember(_, tailBlock, lhs))
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      case IRTry(info, body, name, catchIR, finIR) =>
        (name, catchIR, finIR) match {
          case (Some(x), Some(catb), None) =>
            catchVarMap.add(x.uniqueName)

            /* try block */
            val tryBlock: Block = func.createBlock
            cfg.addEdge(blocks, tryBlock)

            /* catch block */
            val catchBlock: Block = func.createBlock
            catchBlock.createInst(CFGCatch(_, info, id2cfgId(x)))

            /* try body */
            val (trybs: List[Block], trylmap: LabelMap) = translateStmt(body, func, List(tryBlock), HashMap(), loopBlock)

            cfg.addEdge(ThrowLabel of trylmap toList, catchBlock, EdgeExc)
            cfg.addEdge(ThrowEndLabel of trylmap toList, catchBlock)
            cfg.addEdge(AfterCatchLabel of trylmap toList, catchBlock)

            /* catch body */
            val (catchbs: List[Block], catchlmap: LabelMap) = translateStmt(catb, func, List(catchBlock), trylmap - ThrowLabel - ThrowEndLabel - AfterCatchLabel, loopBlock)

            /* tail blocks */
            val tailbs: List[Block] = trybs match {
              case block :: Nil if (ThrowLabel of trylmap).contains(block) =>
                val newBlock: Block = func.createBlock
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
            val tryBlock: Block = func.createBlock
            cfg.addEdge(blocks, tryBlock)

            /* finally block */
            val finBlock: Block = func.createBlock

            /* try body */
            val (trybs: List[Block], trylmap: LabelMap) = translateStmt(body, func, List(tryBlock), HashMap(), loopBlock)

            /* finally body */
            val (finbs: List[Block], finlmap: LabelMap) = translateStmt(finb, func, List(finBlock), lmap, loopBlock)

            /* edge : try -> finally */
            cfg.addEdge(trybs, finBlock)
            val reslmap = (trylmap - AfterCatchLabel).foldLeft(finlmap) {
              case (map, (label, bs)) => bs.isEmpty match {
                case false =>
                  val dupBlock: Block = func.createBlock
                  val (bs: List[Block], lm: LabelMap) = translateStmt(finb, func, List(dupBlock), map, loopBlock)
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
            catchVarMap.add(x.uniqueName)

            /* try block */
            val tryBlock: Block = func.createBlock
            cfg.addEdge(blocks, tryBlock)

            /* catch block */
            val catchBlock: Block = func.createBlock
            catchBlock.createInst(CFGCatch(_, info, id2cfgId(x)))

            /* finally block */
            val finBlock: Block = func.createBlock

            /* try body */
            val (trybs: List[Block], trylmap: LabelMap) = translateStmt(body, func, List(tryBlock), HashMap(), loopBlock)

            cfg.addEdge(ThrowLabel of trylmap toList, catchBlock, EdgeExc)
            cfg.addEdge(ThrowEndLabel of trylmap toList, catchBlock)
            cfg.addEdge(AfterCatchLabel of trylmap toList, catchBlock)

            /* catch body */
            val (catchbs: List[Block], catchlmap: LabelMap) = translateStmt(catb, func, List(catchBlock), trylmap - ThrowLabel - ThrowEndLabel - AfterCatchLabel, loopBlock)

            /* finally body */
            val (finbs: List[Block], finlmap: LabelMap) = translateStmt(finb, func, List(finBlock), lmap, loopBlock)

            /* edge : try+catch -> finally */
            cfg.addEdge(trybs ++ catchbs, finBlock)
            val reslmap: LabelMap = (catchlmap - AfterCatchLabel).foldLeft(finlmap) {
              case (map, (label, bs)) => bs.isEmpty match {
                case false =>
                  val dupBlock: Block = func.createBlock
                  val (bs: List[Block], lm: LabelMap) = translateStmt(finb, func, List(dupBlock), map, loopBlock)
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
            errLog.signal("Wrong IRTryStmt.", stmt)
            (blocks, lmap)
        }
      /* PEI : element assign */
      case IRArgs(info, lhs, elements) =>
        val tailBlock: Block = getTail(blocks, func)
        tailBlock.createInst(CFGAllocArg(_, info, id2cfgId(lhs), elements.length, newProgramAddr))
        elements.zipWithIndex.foreach {
          case (Some(elem), idx) => translateElement(info, elem, tailBlock, lhs, idx)
          case _ =>
        }
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      /* PEI : element assign */
      case IRArray(info, lhs, elements) =>
        val tailBlock: Block = getTail(blocks, func)
        tailBlock.createInst(CFGAllocArray(_, info, id2cfgId(lhs), elements.length, newProgramAddr))
        elements.zipWithIndex.foreach {
          case (Some(elem), idx) => translateElement(info, elem, tailBlock, lhs, idx)
          case _ =>
        }
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      /* PEI : element assign */
      case IRArrayNumber(info, lhs, elements) =>
        val tailBlock: Block = getTail(blocks, func)
        tailBlock.createInst(CFGAllocArray(_, info, id2cfgId(lhs), elements.length, newProgramAddr))
        elements.zipWithIndex.foreach {
          case (elem, idx) => translateDoubleElement(info, elem, tailBlock, lhs, idx)
        }
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      case IRBreak(info, label) =>
        val key: String = label.uniqueName
        val bs: Set[Block] = lmap.getOrElse(UserLabel(key), HashSet()) ++ blocks.toSet
        loopBlock match {
          case Some(l) => blocks.foreach((b) => cfg.addEdge(l, b, EdgeLoopCond))
          case None =>
        }
        (Nil, lmap.updated(UserLabel(key), bs))
      /* PEI : fun == "<>toObject" */
      case IRInternalCall(info, lhs, fun @ (IRTmpId(_, originalName, uniqueName, _)), arg1, arg2) =>
        val tailBlock: Block = getTail(blocks, func)
        val (addr: Option[Address], lm: LabelMap) = uniqueName match {
          case "<>Global<>toObject" | "<>Global<>iteratorInit" => (Some(newProgramAddr), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
          case _ => (None, lmap)
        }
        val argList: List[CFGExpr] = arg2 match {
          case Some(arg) => List(ir2cfgExpr(arg1), id2cfgExpr(arg))
          case None => List(ir2cfgExpr(arg1))
        }
        tailBlock.createInst(CFGInternalCall(_, info, id2cfgId(lhs), id2cfgId(fun), argList, addr))
        (List(tailBlock), lm)
      /* PEI : call, after-call */
      case IRCall(info, lhs, fun, thisB, args) =>
        val tailBlock: Block = getTail(blocks, func)
        val call: Call = tailBlock.createCall(CFGCall(_, info, id2cfgExpr(fun), id2cfgExpr(thisB), id2cfgExpr(args), newProgramAddr, newProgramAddr), id2cfgId(lhs))
        loopBlock match {
          case Some(l) =>
            cfg.addEdge(l, tailBlock, EdgeLoopCond)
            cfg.addEdge(l, call.afterCall, EdgeLoopCond)
          case None =>
        }
        (List(call.afterCall), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock).updated(AfterCatchLabel, (AfterCatchLabel of lmap) + call.afterCatch))
      /* PEI : construct, after-call */
      case IRNew(info, lhs, cons, args) if (args.length == 2) =>
        val tailBlock: Block = getTail(blocks, func)
        val call = tailBlock.createCall(CFGConstruct(_, info, id2cfgExpr(cons), id2cfgExpr(args(0)), id2cfgExpr(args(1)), newProgramAddr, newProgramAddr), id2cfgId(lhs))
        (List(call.afterCall), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock).updated(AfterCatchLabel, (AfterCatchLabel of lmap) + call.afterCatch))
      case c @ IRNew(_, _, _, _) =>
        errLog.signal("IRNew should have two elements in args.", c)
        (Nil, lmap)
      /* PEI : id lookup */
      case IRDelete(info, lhs, id) =>
        val tailBlock: Block = getTail(blocks, func)
        tailBlock.createInst(CFGDelete(_, info, id2cfgId(lhs), id2cfgExpr(id)))
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      /* PEI : id lookup */
      case IRDeleteProp(info, lhs, obj, index) =>
        val tailBlock: Block = getTail(blocks, func)
        tailBlock.createInst(CFGDeleteProp(_, info, id2cfgId(lhs), id2cfgExpr(obj), ir2cfgExpr(index)))
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      /* PEI : expr == IRId */
      case IRExprStmt(info, lhs, expr, _) =>
        val tailBlock: Block = getTail(blocks, func)
        tailBlock.createInst(CFGExprStmt(_, info, id2cfgId(lhs), ir2cfgExpr(expr)))
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      case IRIf(info, cond, trueIR, falseIR) =>
        /* true block */
        val trueBlock: Block = func.createBlock
        cfg.addEdge(blocks, trueBlock)

        /* false block */
        val falseBlock: Block = func.createBlock
        cfg.addEdge(blocks, falseBlock)

        /* Insert assert instructions */
        val condInfo: Info = cond.info
        trueBlock.createInst(CFGAssert(_, condInfo, ir2cfgExpr(cond), true))
        cond match {
          case IRBin(_, first, op, second) if NU.isAssertOperator(op) =>
            falseBlock.createInst(CFGAssert(_, condInfo, CFGBin(condInfo, ir2cfgExpr(first), NU.transIROp(op), ir2cfgExpr(second)), false))
          case _ =>
            falseBlock.createInst(CFGAssert(_, condInfo, CFGUn(condInfo, IF.makeOp("!"), ir2cfgExpr(cond)), false))
        }

        /* true body */
        val (truebs: List[Block], truelmap: LabelMap) = translateStmt(trueIR, func, List(trueBlock), lmap, loopBlock)

        /* false body */
        val endBlock: Block = func.createBlock
        loopBlock match {
          case Some(l) => cfg.addEdge(l, endBlock, EdgeLoopCond)
          case _ =>
        }
        falseIR match {
          case Some(stmt) =>
            val (falsebs: List[Block], falselmap: LabelMap) = translateStmt(stmt, func, List(falseBlock), truelmap, loopBlock)
            cfg.addEdge(truebs ++ falsebs, endBlock)
            (List(endBlock), falselmap.updated(ThrowLabel, (ThrowLabel of falselmap) + trueBlock + falseBlock))
          case None =>
            cfg.addEdge(falseBlock :: truebs, endBlock)
            (List(endBlock), truelmap.updated(ThrowLabel, (ThrowLabel of truelmap) + trueBlock + falseBlock))
        }
      case IRLabelStmt(info, labelIR, stmt) =>
        val block: Block = func.createBlock
        val (bs: List[Block], lm: LabelMap) = translateStmt(stmt, func, blocks, lmap, loopBlock)
        val label: JSLabel = UserLabel(labelIR.uniqueName)
        cfg.addEdge(bs, block)
        cfg.addEdge(label of lm toList, block)
        (List(block), lm - label)
      /* PEI : expr lookup */
      case IRReturn(info, expr) =>
        val tailBlock: Block = getTail(blocks, func)
        tailBlock.createInst(CFGReturn(_, info, expr.map(ir2cfgExpr _)))
        loopBlock match {
          case Some(l) => cfg.addEdge(l, tailBlock, EdgeLoopCond)
          case None =>
        }
        (Nil, lmap.updated(RetLabel, (RetLabel of lmap) + tailBlock).updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      /* PEI : id lookup */
      case IRStore(info, obj, index, rhs) =>
        val tailBlock: Block = getTail(blocks, func)
        tailBlock.createInst(CFGStore(_, info, id2cfgExpr(obj), ir2cfgExpr(index), ir2cfgExpr(rhs)))
        (List(tailBlock), lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      case IRThrow(info, expr) =>
        val tailBlock: Block = getTail(blocks, func)
        tailBlock.createInst(CFGThrow(_, info, ir2cfgExpr(expr)))
        (Nil, lmap.updated(ThrowLabel, (ThrowLabel of lmap) + tailBlock))
      case IRWhile(info, cond, body) =>
        // Checks whether this while loop is originated from for-in or not.
        // TODO: Need to find a more graceful way.
        val bForin: Boolean = body match {
          case IRSeq(_, stmts) if stmts.size > 0 => stmts(0) match {
            case IRInternalCall(_, _, fun @ (IRTmpId(_, _, "<>Global<>iteratorNext", _)), _, _) => true
            case _ => false
          }
          case _ => false
        }

        val unrollingCount =
          if (bForin) Safe.config.opt_unrollingCount else 0

        if (unrollingCount == 0) {
          /* tail block */
          val tailBlock: Block = getTail(blocks, func)
          /* while loop head */
          val headBlock: Block = func.createBlock
          /* loop body */
          val loopBodyBlock: Block = func.createBlock
          /* loop out */
          val loopOutBlock: Block = func.createBlock
          /* Insert assert instruction */
          val condInfo: Info = cond.info
          loopBodyBlock.createInst(CFGAssert(_, condInfo, ir2cfgExpr(cond), true))
          cond match {
            case IRBin(_, first, op, second) if NU.isAssertOperator(op) =>
              loopOutBlock.createInst(CFGAssert(_, condInfo, CFGBin(condInfo, ir2cfgExpr(first), NU.transIROp(op), ir2cfgExpr(second)), false))
            case _ =>
              loopOutBlock.createInst(CFGAssert(_, condInfo, CFGUn(condInfo, IF.makeOp("!"), ir2cfgExpr(cond)), false))
          }
          /* add edge from tail to loop head */
          cfg.addEdge(tailBlock, headBlock)
          /* add edge from loop head to loop body */
          cfg.addEdge(headBlock, loopBodyBlock)
          /* add edge from loop head to loop out*/
          cfg.addEdge(headBlock, loopOutBlock)
          /* build loop body */
          val (bs: List[Block], lm: LabelMap) = translateStmt(body, func, List(loopBodyBlock), lmap)
          /* add edge from tails of loop body to loop head */
          cfg.addEdge(bs, headBlock)
          (List(loopOutBlock), lm.updated(ThrowLabel, (ThrowLabel of lm) + loopBodyBlock + loopOutBlock))
        } else {
          var updatedlmap = lmap
          def newBranchBlocks(headBlock: Block): (Block, Block, List[Block]) = {
            val trueBlock: Block = func.createBlock // loop body
            val falseBlock: Block = func.createBlock // loop out

            /* Insert assert instruction */
            val condInfo: Info = cond.info
            trueBlock.createInst(CFGAssert(_, condInfo, ir2cfgExpr(cond), true))
            cond match {
              case IRBin(_, first, op, second) if NU.isAssertOperator(op) =>
                falseBlock.createInst(CFGAssert(_, condInfo, CFGBin(condInfo, ir2cfgExpr(first), NU.transIROp(op), ir2cfgExpr(second)), false))
              case _ =>
                falseBlock.createInst(CFGAssert(_, condInfo, CFGUn(condInfo, IF.makeOp("!"), ir2cfgExpr(cond)), false))
            }

            /* build loop body */
            val (leafBlocks: List[Block], newlmap: LabelMap) = translateStmt(body, func, List(trueBlock), updatedlmap)
            updatedlmap = newlmap.updated(ThrowLabel, (ThrowLabel of newlmap) + trueBlock + falseBlock)

            /* add edge from loop head to loop body */
            cfg.addEdge(headBlock, trueBlock)
            /* add edge from loop head to out*/
            cfg.addEdge(headBlock, falseBlock)

            (trueBlock, falseBlock, leafBlocks)
          }

          /* while loop head */
          val headBlock: Block = func.createBlock
          /* (loop body, loop out, loop body's leaf blocks) */
          var (lastBodyBlock: Block, lastOutBlock: Block, lastLeafBlocks: List[Block]) = newBranchBlocks(headBlock)
          /* add edge from tails of loop body to loop head */
          cfg.addEdge(lastLeafBlocks, headBlock)

          /* tail block */
          var tailBlock: Block = getTail(blocks, func)
          /* unrolling */
          for (i <- 0 until unrollingCount) {
            /* (loop body, loop out, loop body's leaf blocks) */
            val (bodyBlock: Block, outBlock: Block, leafBlocks: List[Block]) = newBranchBlocks(tailBlock)
            /* add edge from unrolling out to last out*/
            cfg.addEdge(outBlock, lastOutBlock)
            if (leafBlocks.length > 1) {
              tailBlock = func.createBlock
              cfg.addEdge(leafBlocks, tailBlock)
            } else tailBlock = leafBlocks.head
          }
          /* add edge from unrolled tail to loop head */
          cfg.addEdge(tailBlock, headBlock)

          (List(lastOutBlock), updatedlmap)
        }
      case _ => {
        System.err.println("* Warning: following IR statement is ignored: " + stmt)
        (blocks, lmap)
      }
    }
    /* statements */
    //case IREval(info, lhs, _, arg) => (Nil, label_map)
    //case IRWith(info, expr, stmt) => (Nil, label_map)
    //case IRGetProp(info, fun) => (Nil, label_map)
    //case IRSetProp(info, fun) => (Nil, label_map)
  }

  /* mem rule : IRField x Block x IRId -> Unit */
  private def translateMember(mem: IRMember, block: Block, lhs: IRId): Unit = {
    mem match {
      case IRField(info, prop, expr) =>
        val lhsExpr: CFGVarRef = CFGVarRef(info, id2cfgId(lhs))
        val indexExpr: CFGString = CFGString(prop.uniqueName)
        block.createInst(CFGStore(_, info, lhsExpr, indexExpr, ir2cfgExpr(expr)))
      case getOrSet =>
        errLog.signal("IRGetProp, IRSetProp is not supported.", getOrSet)
    }
  }

  /* elem rule : IRNodeInfo x IRExpr x Block x IRId x Int -> Unit */
  private def translateElement(info: IRNodeInfo, elem: IRExpr, block: Block, lhs: IRId, index: Int): Unit = {
    val lhsExpr: CFGExpr = CFGVarRef(info, id2cfgId(lhs))
    block.createInst(CFGStore(_, info, lhsExpr, CFGString(index.toString), ir2cfgExpr(elem)))
    ()
  }
  /* elem rule : IRNodeInfo x Double x Block x IRId x Int -> Unit */
  private def translateDoubleElement(info: IRNodeInfo, elem: Double, block: Block, lhs: IRId, index: Int): Unit = {
    val lhsExpr: CFGExpr = CFGVarRef(info, id2cfgId(lhs))
    block.createInst(CFGStore(_, info, lhsExpr, CFGString(index.toString), CFGNumber(elem.toString, elem.doubleValue)))
    ()
  }

  private def isInternalCall(fname: String): Boolean = NU.isGlobalName(fname)
  private def ir2cfgExpr(expr: IRExpr): CFGExpr = {
    expr match {
      /* PEI : id lookup */
      case IRLoad(info, obj, index) =>
        CFGLoad(info, id2cfgExpr(obj), ir2cfgExpr(index))
      /* PEI : op \in {instanceof, in}, id lookup */
      case IRBin(info, first, op, second) =>
        CFGBin(info, ir2cfgExpr(first), op, ir2cfgExpr(second))
      /* PEI : id lookup */
      case IRUn(info, op, expr) =>
        CFGUn(info, op, ir2cfgExpr(expr))
      case id: IRId => CFGVarRef(id.info, id2cfgId(id))
      case IRThis(info) => CFGThis(info)
      case IRNumber(_, text, num) => CFGNumber(text, num.doubleValue)
      case IRString(_, str) => CFGString(str)
      case IRBool(_, bool) => CFGBool(bool)
      case IRNull(_) => CFGNull()
    }
  }

  private def id2cfgExpr(id: IRId): CFGExpr = CFGVarRef(id.info, id2cfgId(id))
  private def idList2cfgIdList(id: List[IRId]): List[CFGId] = id.map(id2cfgId)
  private def id2cfgId(id: IRId): CFGId = {
    val text: String = id.uniqueName
    // println ("[Id] %s".format(text))
    cfgIdMap.getOrElse(text, {
      val name: String = getUniqueName(text)
      val cfgId: CFGId = id match {
        case IRUserId(info, originName, uniqueName, isGlobal, isWith) =>
          val kind: VarKind = if (isGlobal) GlobalVar
          else if (captured(text)) {
            if (catchVarMap(text)) CapturedCatchVar
            else CapturedVar
          } else PureLocalVar
          CFGUserId(info, name, kind, originName, isWith)
        case IRTmpId(info, originalName, uniqueName, isGlobal) =>
          if (isGlobal) CFGTempId(name, GlobalVar)
          else if (text.startsWith("<>arguments<>")) CFGUserId(info, name, PureLocalVar, "arguments", false)
          else CFGTempId(name, PureLocalVar)
      }
      cfgId match {
        case CFGUserId(_, _, kind, _, _) if kind == CapturedCatchVar || kind == CapturedVar => currentFunc.addCaptured(cfgId)
        case _ =>
      }
      cfgIdMap(text) = cfgId
      cfgId
    })
  }

  /* getTail : Block list x CFGFunction -> Block */
  private def getTail(blocks: List[Block], func: CFGFunction): Block = {
    blocks match {
      case Nil => func.createBlock
      case block :: Nil => block
      case _ =>
        val tailBlock: Block = func.createBlock
        blocks.foreach(cfg.addEdge(_, tailBlock))
        tailBlock
    }
  }
}
