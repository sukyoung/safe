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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._

import scala.collection.immutable.{ HashMap, HashSet }

class Semantics(
    cfg: CFG,
    worklist: Worklist
) {
  lazy val excLog: ExcLog = new ExcLog

  private val AF = AbsBool.False
  private val AT = AbsBool.True
  private val AB = AbsBool.Bot

  // Interprocedural edges
  private var ipSuccMap: Map[ControlPoint, Map[ControlPoint, (OldAddrSet, DecEnvRecord)]] = HashMap()
  private var ipPredMap: Map[ControlPoint, Set[ControlPoint]] = HashMap()
  def getAllIPSucc: Map[ControlPoint, Map[ControlPoint, (OldAddrSet, DecEnvRecord)]] = ipSuccMap
  def getAllIPPred: Map[ControlPoint, Set[ControlPoint]] = ipPredMap
  def getInterProcSucc(cp: ControlPoint): Option[Map[ControlPoint, (OldAddrSet, DecEnvRecord)]] = ipSuccMap.get(cp)
  def getInterProcPred(cp: ControlPoint): Option[Set[ControlPoint]] = ipPredMap.get(cp)

  // Adds inter-procedural call edge from call-node cp1 to entry-node cp2.
  // Edge label ctx records callee context, which is joined if the edge existed already.
  def addCallEdge(cp1: ControlPoint, cp2: ControlPoint, old: OldAddrSet, env: DecEnvRecord): Unit = {
    val updatedSuccMap = ipSuccMap.get(cp1) match {
      case None => HashMap(cp2 -> (old, env))
      case Some(map2) =>
        map2.get(cp2) match {
          case None =>
            map2 + (cp2 -> (old, env))
          case Some((oldOld, oldEnv)) =>
            map2 + (cp2 -> (oldOld + old, oldEnv + env))
        }
    }
    ipSuccMap += (cp1 -> updatedSuccMap)

    val updatedPredSet = ipPredMap.get(cp2) match {
      case None => HashSet(cp1)
      case Some(cpSet) => cpSet + cp1
    }
    ipPredMap += (cp2 -> updatedPredSet)
  }

  // Adds inter-procedural return edge from exit or exit-exc node cp1 to after-call node cp2.
  // Edge label ctx records caller context, which is joined if the edge existed already.
  // If change occurs, cp1 is added to worklist as side-effect.
  def addReturnEdge(cp1: ControlPoint, cp2: ControlPoint, old: OldAddrSet, env: DecEnvRecord): Unit = {
    val updatedSuccMap = ipSuccMap.get(cp1) match {
      case None => {
        worklist.add(cp1)
        HashMap(cp2 -> (old, env))
      }
      case Some(map2) =>
        map2.get(cp2) match {
          case None => {
            worklist.add(cp1)
            map2 + (cp2 -> (old, env))
          }
          case Some((oldOld, oldEnv)) =>
            val oldChanged = !(old <= oldOld)
            val newOld =
              if (oldChanged) oldOld + old
              else oldOld
            val envChanged = !(env <= oldEnv)
            val newEnv =
              if (envChanged) oldEnv + env
              else oldEnv
            if (oldChanged || envChanged) {
              worklist.add(cp1)
              map2 + (cp2 -> (newOld, newEnv))
            } else {
              map2
            }
        }
    }
    ipSuccMap += (cp1 -> updatedSuccMap)

    val updatedPredSet = ipPredMap.get(cp2) match {
      case None => HashSet(cp1)
      case Some(cpSet) => cpSet + cp1
    }
    ipPredMap += (cp2 -> updatedPredSet)
  }

  def E(cp1: ControlPoint, cp2: ControlPoint, old: OldAddrSet, env: DecEnvRecord, st: State): State = {
    (cp1.node, cp2.node) match {
      case (_, Entry(f)) => st.context match {
        case ExecContext.Bot => State.Bot
        case ctx1: ExecContext => {
          val objEnv = env("@scope") match {
            case Some(bind) => DecEnvRecord.newDeclEnvRecord(bind.value)
            case None => DecEnvRecord.newDeclEnvRecord(ValueUtil.Bot)
          }
          val env2 = env - "@scope"
          val ctx2 = ctx1.subsPureLocal(env2)
          val ctx3 = env2("@env") match {
            case Some(bind) =>
              bind.value.locset.foldLeft(ExecContext.Bot)((hi, locEnv) => {
                hi + ctx2.update(locEnv, objEnv)
              })
            case None => ExecContext.Bot
          }
          State(st.heap, ctx3.setOldAddrSet(old))
        }
      }
      case (Exit(_), _) if st.context.isBottom => State.Bot
      case (Exit(f1), AfterCall(f2, retVar, call)) =>
        val (ctx1, old1) = (st.context, st.context.old)
        val (old2, env1) = old.fixOldify(env, old1.mayOld, old1.mustOld)
        if (old2.isBottom) State.Bot
        else {
          val localEnv = ctx1.pureLocal
          val returnV = localEnv.getOrElse("@return")(ValueUtil.Bot) { _.value }
          val ctx2 = ctx1.subsPureLocal(env1)
          val newSt = State(st.heap, ctx2.setOldAddrSet(old2))
          newSt.varStore(retVar, returnV)
        }
      case (Exit(f), _) =>
        val old1 = st.context.old
        val (old2, env1) = old.fixOldify(env, old1.mayOld, old1.mustOld)
        if (old2.isBottom) State.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          State.Bot
        }
      case (ExitExc(_), _) if st.context.isBottom => State.Bot
      case (ExitExc(_), _) if st.context.old.isBottom => State.Bot
      case (ExitExc(_), AfterCatch(_, _)) =>
        val (ctx1, c1) = (st.context, st.context.old)
        val (c2, env1) = old.fixOldify(env, c1.mayOld, c1.mustOld)
        if (c2.isBottom) State.Bot
        else {
          val localEnv = ctx1.pureLocal
          val excValue = localEnv.getOrElse("@exception")(ValueUtil.Bot) { _.value }
          val excBind = BindingUtil(excValue)
          val oldExcAllValue = env1.getOrElse("@exception_all")(ValueUtil.Bot) { _.value }
          val newExcAllBind = BindingUtil(excValue + oldExcAllValue)
          val ctx2 = ctx1.subsPureLocal(env1
            .update("@exception", excBind)
            .update("@exception_all", newExcAllBind))
          State(st.heap, ctx2.setOldAddrSet(c2))
        }
      case (ExitExc(f), _) =>
        val old1 = st.context.old
        val (old2, env1) = old.fixOldify(env, old1.mayOld, old1.mustOld)
        if (old2.isBottom) State.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          State.Bot
        }
      case _ => st
    }
  }

  def C(cp: ControlPoint, st: State): (State, State) = {
    if (st.isBottom) (State.Bot, State.Bot)
    else {
      val h = st.heap
      val ctx = st.context
      val old = ctx.old
      cp.node match {
        case Entry(_) => {
          val fun = cp.node.func
          val xArgVars = fun.argVars
          val xLocalVars = fun.localVars
          val localEnv = ctx.pureLocal
          val locSetArg = localEnv.getOrElse(fun.argumentsName)(AbsLoc.Bot) { _.value.locset }
          val (nSt, _) = xArgVars.foldLeft((st, 0))((res, x) => {
            val (iSt, i) = res
            val vi = locSetArg.foldLeft(ValueUtil.Bot)((vk, lArg) => {
              vk + iSt.heap.proto(lArg, AbsString.alpha(i.toString))
            })
            (iSt.createMutableBinding(x, vi), i + 1)
          })
          val newSt = xLocalVars.foldLeft(nSt)((jSt, x) => {
            val undefV = ValueUtil.alpha(Undef)
            jSt.createMutableBinding(x, undefV)
          })
          (newSt, State.Bot)
        }
        case Exit(_) => (st, State.Bot)
        case ExitExc(_) => (st, State.Bot)
        case call: Call => CI(cp, call.callInst, st, State.Bot)
        case afterCall: AfterCall => (st, State.Bot)
        case afterCatch: AfterCatch => (st, State.Bot)
        case block: NormalBlock =>
          block.getInsts.foldRight((st, State.Bot))((inst, states) => {
            val (oldSt, oldExcSt) = states
            I(inst, oldSt, oldExcSt)
          })
        case ModelBlock(_, sem) => sem(this, st)
      }
    }
  }

  def I(i: CFGNormalInst, st: State, excSt: State): (State, State) = {
    i match {
      case _ if st.isBottom => (State.Bot, excSt)
      case CFGAlloc(_, _, x, e, newAddr) => {
        val objProtoSingleton = AbsLoc.alpha(BuiltinObjectProto.loc)
        // Recency Abstraction
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)
        val (vLocSet, excSet) = e match {
          case None => (objProtoSingleton, ExceptionSetEmpty)
          case Some(proto) => {
            val (v, es) = V(proto, st1)
            if (!v.pvalue.isBottom)
              (v.locset + BuiltinObjectProto.loc, es)
            else
              (v.locset, es)
          }
        }
        val h2 = st1.heap.update(locR, AbsObjectUtil.newObject(vLocSet))
        val newSt = State(h2, st1.context).varStore(x, ValueUtil(locR))
        val newExcSt = st.raiseException(excSet)
        val s1 = excSt + newExcSt
        (newSt, s1)
      }
      case CFGAllocArray(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)
        val np = AbsNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, AbsObjectUtil.newArrayObject(np))
        val newSt = State(h2, st1.context).varStore(x, ValueUtil(locR))
        (newSt, excSt)
      }
      case CFGAllocArg(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)
        val absN = AbsNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, AbsObjectUtil.newArgObject(absN))
        val newSt = State(h2, st1.context).varStore(x, ValueUtil(locR))
        (newSt, excSt)
      }
      case CFGExprStmt(_, _, x, e) => {
        val (v, excSet) = V(e, st)
        val st1 =
          if (!v.isBottom) st.varStore(x, v)
          else State.Bot
        val newExcSt = st.raiseException(excSet)
        (st1, excSt + newExcSt)
      }
      case CFGDelete(_, _, x1, CFGVarRef(_, x2)) => {
        val baseLocSet = st.lookupBase(x2)
        val (st1, b) =
          if (baseLocSet.isBottom) {
            (st, AT)
          } else {
            baseLocSet.foldLeft[(State, AbsBool)](State.Bot, AB)((res, baseLoc) => {
              val (tmpState, tmpB) = res
              val (delState, delB) = st.delete(baseLoc, x2.text)
              (tmpState + delState, tmpB + delB)
            })
          }
        val bVal = ValueUtil(b)
        val st2 = st1.varStore(x1, bVal)
        (st2, excSt)
      }
      case CFGDelete(_, _, x1, expr) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) {
            val trueVal = ValueUtil(AT)
            st.varStore(x1, trueVal)
          } else State.Bot
        val newExcSt = st.raiseException(excSet)
        (st1, excSt + newExcSt)
      }
      case CFGDeleteProp(_, _, lhs, obj, index) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset
        val (v, excSet) = V(index, st)
        val absStrSet =
          if (v.isBottom) HashSet[AbsString]()
          else TypeConversionHelper.ToPrimitive(v, st.heap).toStringSet
        val (h1, b) = locSet.foldLeft[(Heap, AbsBool)](Heap.Bot, AB)((res1, l) => {
          val (tmpHeap1, tmpB1) = res1
          absStrSet.foldLeft((tmpHeap1, tmpB1))((res2, s) => {
            val (tmpHeap2, tmpB2) = res2
            val (delHeap, delB) = st.heap.delete(l, s)
            (tmpHeap2 + delHeap, tmpB2 + delB)
          })
        })
        val st1 = State(h1, st.context)
        val st2 =
          if (st1.isBottom) State.Bot
          else {
            val boolPV = AbsPValue(b)
            st1.varStore(lhs, ValueUtil(boolPV))
          }
        val newExcSt = st.raiseException(excSet)
        (st2, excSt + newExcSt)
      }
      case CFGStore(_, block, obj, index, rhs) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset

        val (idxV, excSetIdx) = V(index, st)
        val (vRhs, esRhs) = V(rhs, st)

        val (heap1, excSet1) =
          (idxV, vRhs) match {
            case (v, _) if v.isBottom => (Heap.Bot, excSetIdx)
            case (_, v) if v.isBottom => (Heap.Bot, excSetIdx ++ esRhs)
            case _ =>
              // iterate over set of strings for index
              val absStrSet = TypeConversionHelper.ToPrimitive(idxV, st.heap).toStringSet
              absStrSet.foldLeft((Heap.Bot, excSetIdx ++ esRhs))((res1, absStr) => {
                val (tmpHeap1, tmpExcSet1) = res1
                val (tmpHeap2, tmpExcSet2) = Helper.storeHelp(locSet, absStr, vRhs, st.heap)
                (tmpHeap1 + tmpHeap2, tmpExcSet1 ++ tmpExcSet2)
              })
          }

        val newExcSt = st.raiseException(excSet1)
        (State(heap1, st.context), excSt + newExcSt)
      }
      case CFGStoreStringIdx(_, block, obj, strIdx, rhs) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset
        val (vRhs, esRhs) = V(rhs, st)

        val (heap1, excSet1) =
          (strIdx, vRhs) match {
            case (_, v) if v.isBottom => (Heap.Bot, esRhs)
            case (EJSString(str), v) =>
              val absStr = AbsString.alpha(str)
              val (tmpHeap2, tmpExcSet2) = Helper.storeHelp(locSet, absStr, vRhs, st.heap)
              (tmpHeap2, tmpExcSet2 ++ esRhs)
          }

        val newExcSt = st.raiseException(excSet1)
        (State(heap1, st.context), excSt + newExcSt)
      }
      case CFGFunExpr(_, block, lhs, None, f, aNew1, aNew2, None) => {
        //Recency Abstraction
        val locR1 = Loc(aNew1, Recent)
        val locR2 = Loc(aNew2, Recent)
        val st1 = st.oldify(aNew1)
        val st2 = st1.oldify(aNew2)
        val oNew = AbsObjectUtil.newObject(BuiltinObjectProto.loc)

        val n = AbsNumber.alpha(f.argVars.length)
        val localEnv = st2.context.pureLocal
        val scope = localEnv.getOrElse("@env")(ValueUtil.Bot) { _.value }
        val h3 = st2.heap.update(locR1, AbsObjectUtil.newFunctionObject(f.id, scope, locR2, n))

        val fVal = ValueUtil(locR1)
        val fPropV = PropValue(DataPropertyUtil(fVal)(AT, AF, AT))
        val h4 = h3.update(locR2, oNew.update("constructor", fPropV, exist = true))

        val newSt = State(h4, st2.context).varStore(lhs, fVal)
        (newSt, excSt)
      }
      case CFGFunExpr(_, block, lhs, Some(name), f, aNew1, aNew2, Some(aNew3)) => {
        // Recency Abstraction
        val locR1 = Loc(aNew1, Recent)
        val locR2 = Loc(aNew2, Recent)
        val locR3 = Loc(aNew3, Recent)
        val st1 = st.oldify(aNew1)
        val st2 = st1.oldify(aNew2)
        val st3 = st2.oldify(aNew3)

        val oNew = AbsObjectUtil.newObject(BuiltinObjectProto.loc)
        val n = AbsNumber.alpha(f.argVars.length)
        val fObjValue = ValueUtil(locR3)
        val h4 = st3.heap.update(locR1, AbsObjectUtil.newFunctionObject(f.id, fObjValue, locR2, n))

        val fVal = ValueUtil(locR1)
        val fPropV = PropValue(DataPropertyUtil(fVal)(AT, AF, AT))
        val h5 = h4.update(locR2, oNew.update("constructor", fPropV, exist = true))

        val localEnv = st3.context.pureLocal
        val scope = localEnv.getOrElse("@env")(ValueUtil.Bot) { _.value }
        val oEnv = DecEnvRecord.newDeclEnvRecord(scope)
        val fBind = BindingUtil(fVal, mutable = AF)
        val newCtx = st3.context.update(locR3, oEnv.update(name.text, fBind))
        val newSt = State(h5, newCtx).varStore(lhs, fVal)
        (newSt, excSt)
      }
      case CFGAssert(_, _, expr, _) => B(expr, st, excSt, i, cfg)
      case CFGCatch(_, _, x) => {
        val localEnv = st.context.pureLocal
        val excSetBind = localEnv.get("@exception_all")
        val excV = localEnv.getOrElse("@exception")(ValueUtil.Bot) { _.value }
        val st1 = st.createMutableBinding(x, excV)
        val newEnv = st1.context.pureLocal.update("@exception", excSetBind)
        val newCtx = st1.context.subsPureLocal(newEnv)
        val newSt = State(st1.heap, newCtx)
        (newSt, State.Bot)
      }
      case CFGReturn(_, _, Some(expr)) => {
        val (v, excSet) = V(expr, st)
        val ctx1 =
          if (!v.isBottom) {
            val localEnv = st.context.pureLocal
            val retValBind = BindingUtil(v)
            st.context.subsPureLocal(localEnv.update("@return", retValBind))
          } else ExecContext.Bot
        val newExcSt = st.raiseException(excSet)
        (State(st.heap, ctx1), excSt + newExcSt)
      }
      case CFGReturn(_, _, None) => {
        val localEnv = st.context.pureLocal
        val retValBind = BindingUtil(ValueUtil.alpha(Undef))
        val ctx1 = st.context.subsPureLocal(localEnv.update("@return", retValBind))
        val newSt = State(st.heap, ctx1)
        (newSt, excSt)
      }
      case CFGThrow(_, _, expr) => {
        val (v, excSet) = V(expr, st)
        val localEnv = st.context.pureLocal
        val excSetV = localEnv.getOrElse("@exception_all")(ValueUtil.Bot) { _.value }
        val newExcBind = BindingUtil(v)
        val newExcSetBind = BindingUtil(v + excSetV)
        val retValBind = BindingUtil(ValueUtil.alpha(Undef))
        val newEnv =
          localEnv.
            update("@exception", newExcBind).
            update("@exception_all", newExcSetBind).
            update("@return", retValBind)
        val ctx1 = st.context.subsPureLocal(newEnv)
        val newExcSt = st.raiseException(excSet)

        (State.Bot, excSt + State(st.heap, ctx1) + newExcSt)
      }
      case CFGInternalCall(ir, info, lhs, fun, arguments, loc) =>
        (fun.toString, arguments, loc) match {
          case ("<>Global<>toObject", List(expr), Some(aNew)) => {
            val (v, excSet1) = V(expr, st)
            val (newSt, newExcSet) =
              if (v.isBottom) {
                (State.Bot, excSet1)
              } else {
                val (v1, st1, excSet2) = TypeConversionHelper.ToObject(v, st, aNew)
                val st2 =
                  if (!v1.isBottom) st1.varStore(lhs, v1)
                  else State.Bot
                (st2, excSet1 ++ excSet2)
              }
            val newExcSt = st.raiseException(newExcSet)
            (newSt, excSt + newExcSt)
          }
          case ("<>Global<>isObject", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val st1 =
              if (!v.isBottom) {
                val b1 =
                  if (!v.locset.isBottom) AT
                  else AB
                val b2 =
                  if (!v.pvalue.isBottom) AF
                  else AB
                val boolVal = ValueUtil(AbsPValue(b1 + b2))
                st.varStore(lhs, boolVal)
              } else {
                State.Bot
              }
            val newExcSt = st.raiseException(excSet)
            (st1, excSt + newExcSt)
          }
          case ("<>Global<>toNumber", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val st1 =
              if (!v.isBottom) st.varStore(lhs, ValueUtil(TypeConversionHelper.ToNumber(v, st.heap)))
              else State.Bot

            val newExcSt = st.raiseException(excSet)
            (st1, excSt + newExcSt)
          }
          case ("<>Global<>toBoolean", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val st1 =
              if (!v.isBottom) st.varStore(lhs, ValueUtil(TypeConversionHelper.ToBoolean(v)))
              else State.Bot

            val newExcSt = st.raiseException(excSet)
            (st1, excSt + newExcSt)
          }
          case ("<>Global<>getBase", List(CFGVarRef(_, x2)), None) => {
            val locSetBase = st.lookupBase(x2)
            val st1 = st.varStore(lhs, ValueUtil(locSetBase))
            (st1, excSt)
          }
          case ("<>Global<>iteratorInit", List(expr), Some(aNew)) => (st, excSt)
          case ("<>Global<>iteratorHasNext", List(expr2, expr3), None) =>
            val boolPV = AbsPValue(AbsBool.Top)
            val st1 = st.varStore(lhs, ValueUtil(boolPV))
            (st1, excSt)
          case ("<>Global<>iteratorNext", List(expr2, expr3), None) =>
            val strPV = AbsPValue(AbsString.Top)
            val st1 = st.varStore(lhs, ValueUtil(strPV))
            (st1, excSt)
          case _ =>
            excLog.signal(SemanticsNotYetImplementedError(ir))
            (State.Bot, State.Bot)
        }
      case CFGNoOp(_, _, _) => (st, excSt)
    }
  }

  def CI(cp: ControlPoint, i: CFGCallInst, st: State, excSt: State): (State, State) = {
    // cons, thisArg and arguments must not be bottom
    val locR = Loc(i.addr1, Recent)
    val st1 = st.oldify(i.addr1)
    val (funVal, funExcSet) = V(i.fun, st1)
    val funLocSet = i match {
      case (_: CFGConstruct) => funVal.locset.filter(l => AT <= st1.heap.hasConstruct(l)(AbsBool))
      case (_: CFGCall) => funVal.locset.filter(l => AT <= TypeConversionHelper.IsCallable(l, st1.heap))
    }
    val (thisVal, _) = V(i.thisArg, st1)
    val thisLocSet = thisVal.getThis(st1.heap)
    val (argVal, _) = V(i.arguments, st1)

    // XXX: stop if thisArg or arguments is LocSetBot(ValueBot)
    if (thisLocSet.isBottom || argVal.isBottom) {
      (st, excSt)
    } else {
      val oldLocalEnv = st1.context.pureLocal
      val callerCallCtx = cp.callContext
      val nCall = cp.node match {
        case callBlock: Call => callBlock
        case _ =>
          excLog.signal(NoAfterCallAfterCatchError(i.ir))
          i.block
      }
      val cpAfterCall = ControlPoint(nCall.afterCall, callerCallCtx)
      val cpAfterCatch = ControlPoint(nCall.afterCatch, callerCallCtx)

      // Draw call/return edges
      funLocSet.foreach((fLoc) => {
        val funObj = st1.heap.getOrElse(fLoc, AbsObjectUtil.Bot)
        val fidSet = i match {
          case _: CFGConstruct =>
            funObj.getOrElse[Set[FunctionId]](IConstruct)(HashSet[FunctionId]()) { _.fidset }
          case _: CFGCall =>
            funObj.getOrElse[Set[FunctionId]](ICall)(HashSet[FunctionId]()) { _.fidset }
        }
        fidSet.foreach((fid) => {
          val newPureLocal = DecEnvRecord.newPureLocal(ValueUtil(locR), thisLocSet)
          val callerCtxSet = callerCallCtx.newCallContext(st1.heap, cfg, fid, locR, thisLocSet, newPureLocal, Some(i.addr1))
          callerCtxSet.foreach {
            case (newCallCtx, newEnv) => {
              val argBind = BindingUtil(argVal)
              cfg.getFunc(fid) match {
                case Some(funCFG) => {
                  val scopeValue = funObj.getOrElse(IScope)(ValueUtil.Bot) { _.value }
                  val scopeBind = BindingUtil(scopeValue)
                  val newEnv2 = newEnv.update(funCFG.argumentsName, argBind)
                    .update("@scope", scopeBind)
                  val entryCP = ControlPoint(funCFG.entry, newCallCtx)
                  val exitCP = ControlPoint(funCFG.exit, newCallCtx)
                  val exitExcCP = ControlPoint(funCFG.exitExc, newCallCtx)
                  addCallEdge(cp, entryCP, OldAddrSet.Empty, newEnv2)
                  addReturnEdge(exitCP, cpAfterCall, st1.context.old, oldLocalEnv)
                  addReturnEdge(exitExcCP, cpAfterCatch, st1.context.old, oldLocalEnv)
                }
                case None => excLog.signal(UndefinedFunctionCallError(i.ir))
              }
            }
          }
        })
      })

      val h2 = argVal.locset.foldLeft(Heap.Bot)((tmpHeap, l) => {
        val funPropV = PropValue(DataPropertyUtil(funLocSet)(AT, AF, AT))
        val argObj = st1.heap.getOrElse(l, AbsObjectUtil.Bot)
        tmpHeap + st1.heap.update(l, argObj.update("callee", funPropV))
      })

      // exception handling
      val typeExcSet1 = i match {
        case _: CFGConstruct if funVal.locset.exists(l => AF <= st1.heap.hasConstruct(l)(AbsBool)) => HashSet(TypeError)
        case _: CFGCall if funVal.locset.exists(l => AF <= TypeConversionHelper.IsCallable(l, st1.heap)) => HashSet(TypeError)
        case _ => ExceptionSetEmpty
      }
      val typeExcSet2 =
        if (!funVal.pvalue.isBottom) HashSet(TypeError)
        else ExceptionSetEmpty

      val totalExcSet = funExcSet ++ typeExcSet1 ++ typeExcSet2
      val newExcSt = st1.raiseException(totalExcSet)

      val h3 =
        if (!funLocSet.isBottom) h2
        else Heap.Bot

      val newSt = State(h3, st1.context)
      (newSt, excSt + newExcSt)
    }
  }

  def V(expr: CFGExpr, st: State): (Value, Set[Exception]) = {
    expr match {
      case CFGVarRef(ir, id) => st.lookup(id)
      case CFGLoad(ir, obj, index) => {
        val (objV, _) = V(obj, st)
        val (idxV, idxExcSet) = V(index, st)
        val absStrSet =
          if (!idxV.isBottom) TypeConversionHelper.ToPrimitive(idxV, st.heap).toStringSet
          else HashSet[AbsString]()
        val v1 = CFGLoadHelper(objV, absStrSet, st.heap)
        (v1, idxExcSet)
      }
      case CFGThis(ir) =>
        val localEnv = st.context.pureLocal
        val thisLocSet = localEnv.getOrElse("@this")(AbsLoc.Bot) { _.value.locset }
        (ValueUtil(thisLocSet), ExceptionSetEmpty)
      case CFGBin(ir, expr1, op, expr2) => {
        val (v1, excSet1) = V(expr1, st)
        val (v2, excSet2) = V(expr2, st)
        (v1, v2) match {
          case _ if v1.isBottom => (ValueUtil.Bot, excSet1)
          case _ if v2.isBottom => (ValueUtil.Bot, excSet1 ++ excSet2)
          case _ =>
            op.name match {
              case "|" => (Helper.bopBitOr(v1, v2), excSet1 ++ excSet2)
              case "&" => (Helper.bopBitAnd(v1, v2), excSet1 ++ excSet2)
              case "^" => (Helper.bopBitXor(v1, v2), excSet1 ++ excSet2)
              case "<<" => (Helper.bopLShift(v1, v2), excSet1 ++ excSet2)
              case ">>" => (Helper.bopRShift(v1, v2), excSet1 ++ excSet2)
              case ">>>" => (Helper.bopURShift(v1, v2), excSet1 ++ excSet2)
              case "+" => (Helper.bopPlus(v1, v2), excSet1 ++ excSet2)
              case "-" => (Helper.bopMinus(v1, v2), excSet1 ++ excSet2)
              case "*" => (Helper.bopMul(v1, v2), excSet1 ++ excSet2)
              case "/" => (Helper.bopDiv(v1, v2), excSet1 ++ excSet2)
              case "%" => (Helper.bopMod(v1, v2), excSet1 ++ excSet2)
              case "==" => (Helper.bopEqBetter(st.heap, v1, v2), excSet1 ++ excSet2)
              case "!=" => (Helper.bopNeq(v1, v2), excSet1 ++ excSet2)
              case "===" => (Helper.bopSEq(v1, v2), excSet1 ++ excSet2)
              case "!==" => (Helper.bopSNeq(v1, v2), excSet1 ++ excSet2)
              case "<" => (Helper.bopLess(v1, v2), excSet1 ++ excSet2)
              case ">" => (Helper.bopGreater(v1, v2), excSet1 ++ excSet2)
              case "<=" => (Helper.bopLessEq(v1, v2), excSet1 ++ excSet2)
              case ">=" => (Helper.bopGreaterEq(v1, v2), excSet1 ++ excSet2)
              case "instanceof" =>
                val locSet1 = v1.locset
                val locSet2 = v2.locset
                val locSet3 = locSet2.filter((l) => AT <= st.heap.hasInstance(l)(AbsBool))
                val protoVal = locSet3.foldLeft(ValueUtil.Bot)((v, l) => {
                  v + st.heap.proto(l, AbsString.alpha("prototype"))
                })
                val locSet4 = protoVal.locset
                val locSet5 = locSet2.filter((l) => AF <= st.heap.hasInstance(l)(AbsBool))
                val b1 = locSet1.foldLeft[Value](ValueUtil.Bot)((tmpVal1, loc1) => {
                  locSet4.foldLeft[Value](tmpVal1)((tmpVal2, loc2) =>
                    tmpVal2 + Helper.inherit(st.heap, loc1, loc2))
                })
                val pv2 =
                  if (!v2.pvalue.isBottom && !locSet4.isBottom) AbsPValue(AF)
                  else AbsPValue.Bot
                val b2 = ValueUtil(pv2)
                val excSet3 =
                  if (!v2.pvalue.isBottom || !locSet5.isBottom || !protoVal.pvalue.isBottom) HashSet(TypeError)
                  else ExceptionSetEmpty
                val b = b1 + b2
                val excSet = excSet1 ++ excSet2 ++ excSet3
                (b, excSet)
              case "in" => {
                val str = TypeConversionHelper.ToString(v1, st.heap)
                val absB = v2.locset.foldLeft(AB)((tmpAbsB, loc) => {
                  tmpAbsB + st.heap.hasProperty(loc, str)
                })
                val b = ValueUtil(AbsPValue(absB))
                val excSet3 =
                  if (!v2.pvalue.isBottom) HashSet(TypeError)
                  else ExceptionSetEmpty
                val excSet = excSet1 ++ excSet2 ++ excSet3
                (b, excSet)
              }
            }
        }
      }
      case CFGUn(ir, op, expr) => {
        val (v, excSet) = V(expr, st)
        op.name match {
          case "void" => (Helper.uVoid(v), excSet)
          case "+" => (Helper.uopPlus(v), excSet)
          case "-" => (Helper.uopMinusBetter(st.heap, v), excSet)
          case "~" => (Helper.uopBitNeg(v), excSet)
          case "!" => (Helper.uopNeg(v), excSet)
          case "typeof" =>
            expr match {
              case CFGVarRef(_, x) =>
                val absStr1 = TypeConversionHelper.typeTag(v, st.heap)
                val absStr2 =
                  if (excSet.contains(ReferenceError)) AbsString.alpha("undefined")
                  else AbsString.Bot
                val absStrPV = AbsPValue(absStr1 + absStr2)
                (ValueUtil(absStrPV), ExceptionSetEmpty)
              case _ =>
                val absStrPV = AbsPValue(TypeConversionHelper.typeTag(v, st.heap))
                (ValueUtil(absStrPV), excSet)
            }
        }
      }
      case CFGVal(ejsVal) =>
        val pvalue: AbsPValue = ejsVal match {
          case EJSNumber(_, num) => AbsPValue.alpha(num)
          case EJSString(str) => AbsPValue.alpha(str)
          case EJSBool(bool) => AbsPValue.alpha(bool)
          case EJSNull => AbsPValue.alpha(Null)
          case EJSUndef => AbsPValue.alpha()
        }
        (ValueUtil(pvalue), ExceptionSetEmpty)
    }
  }

  def B(expr: CFGExpr, st: State, excSt: State, inst: CFGInst, cfg: CFG): (State, State) = {
    val st1 = st //TODO should be the pruned state

    val (v, excSet) = V(expr, st)
    val newExcSt = st.raiseException(excSet)
    val st2 =
      if (AbsBool.alpha(true) <= TypeConversionHelper.ToBoolean(v)) st1
      else State.Bot

    (st2, excSt + newExcSt)
  }

  def CFGLoadHelper(objV: Value, absStrSet: Set[AbsString], h: Heap): Value = {
    val objLocSet = objV.locset
    val v1 = objLocSet.foldLeft(ValueUtil.Bot)((tmpVal1, loc) => {
      absStrSet.foldLeft(tmpVal1)((tmpVal2, absStr) => {
        tmpVal2 + h.proto(loc, absStr)
      })
    })
    v1
  }
}
