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
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.ir._
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
  case class EdgeData(old: OldAddrSet, env: AbsLexEnv, thisBinding: AbsValue) {
    def +(other: EdgeData): EdgeData = EdgeData(
      this.old + other.old,
      this.env + other.env,
      this.thisBinding + other.thisBinding
    )
  }
  type IPSucc = Map[ControlPoint, EdgeData]
  type IPPred = Set[ControlPoint]
  type IPSuccMap = Map[ControlPoint, IPSucc]
  type IPPredMap = Map[ControlPoint, IPPred]
  private var ipSuccMap: IPSuccMap = HashMap()
  private var ipPredMap: IPPredMap = HashMap()
  def getAllIPSucc: IPSuccMap = ipSuccMap
  def getAllIPPred: IPPredMap = ipPredMap
  def getInterProcSucc(cp: ControlPoint): Option[IPSucc] = ipSuccMap.get(cp)
  def getInterProcPred(cp: ControlPoint): Option[IPPred] = ipPredMap.get(cp)

  // Adds inter-procedural call edge from call-node cp1 to entry-node cp2.
  // Edge label ctx records callee context, which is joined if the edge existed already.
  def addCallEdge(cp1: ControlPoint, cp2: ControlPoint, data: EdgeData): Unit = {
    val updatedSuccMap = ipSuccMap.get(cp1) match {
      case None => HashMap(cp2 -> data)
      case Some(map2) =>
        map2.get(cp2) match {
          case None =>
            map2 + (cp2 -> data)
          case Some(oldData) =>
            map2 + (cp2 -> (data + oldData))
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
  def addReturnEdge(cp1: ControlPoint, cp2: ControlPoint, data: EdgeData): Unit = {
    val updatedSuccMap = ipSuccMap.get(cp1) match {
      case None => {
        worklist.add(cp1)
        HashMap(cp2 -> data)
      }
      case Some(map2) =>
        map2.get(cp2) match {
          case None => {
            worklist.add(cp1)
            map2 + (cp2 -> data)
          }
          case Some(oldData) =>
            val oldChanged = !(data.old <= oldData.old)
            val newOld =
              if (oldChanged) data.old + oldData.old
              else oldData.old
            val envChanged = !(data.env <= oldData.env)
            val newEnv =
              if (envChanged) data.env + oldData.env
              else oldData.env
            val thisChanged = !(data.thisBinding <= oldData.thisBinding)
            val newThis =
              if (thisChanged) data.thisBinding + oldData.thisBinding
              else oldData.thisBinding
            if (oldChanged || envChanged || thisChanged) {
              worklist.add(cp1)
              map2 + (cp2 -> EdgeData(newOld, newEnv, newThis))
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

  def E(cp1: ControlPoint, cp2: ControlPoint, data: EdgeData, st: AbsState): AbsState = {
    (cp1.node, cp2.node) match {
      case (_, Entry(f)) => st.context match {
        case _ if st.context.isBottom => AbsState.Bot
        case ctx1: AbsContext => {
          val objEnv = data.env.record.decEnvRec.GetBindingValue("@scope") match {
            case (value, _) => AbsLexEnv.NewDeclarativeEnvironment(value.locset)
          }
          val (envRec, _) = data.env.record.decEnvRec.DeleteBinding("@scope")
          val ctx2 = ctx1.subsPureLocal(data.env.copyWith(record = envRec))
          val ctx3 = data.env.outer.foldLeft(AbsContext.Bot)((hi, locEnv) => {
            hi + ctx2.update(locEnv, objEnv)
          })
          AbsState(st.heap, ctx3
            .setOldAddrSet(data.old)
            .setThisBinding(data.thisBinding))
        }
      }
      case (Exit(_), _) if st.context.isBottom => AbsState.Bot
      case (Exit(f1), AfterCall(f2, retVar, call)) =>
        val (ctx1, old1) = (st.context, st.context.old)
        val (old2, env1) = data.old.fixOldify(data.env, old1.mayOld, old1.mustOld)
        if (old2.isBottom) AbsState.Bot
        else {
          val localEnv = ctx1.pureLocal
          val (returnV, _) = localEnv.record.decEnvRec.GetBindingValue("@return")
          val ctx2 = ctx1.subsPureLocal(env1)
          val newSt = AbsState(st.heap, ctx2
            .setOldAddrSet(old2)
            .setThisBinding(data.thisBinding))
          newSt.varStore(retVar, returnV)
        }
      case (Exit(f), _) =>
        val old1 = st.context.old
        val (old2, env1) = data.old.fixOldify(data.env, old1.mayOld, old1.mustOld)
        if (old2.isBottom) AbsState.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          AbsState.Bot
        }
      case (ExitExc(_), _) if st.context.isBottom => AbsState.Bot
      case (ExitExc(_), _) if st.context.old.isBottom => AbsState.Bot
      case (ExitExc(_), AfterCatch(_, _)) =>
        val (ctx1, c1) = (st.context, st.context.old)
        val (c2, envL) = data.old.fixOldify(data.env, c1.mayOld, c1.mustOld)
        val env1 = envL.record.decEnvRec
        if (c2.isBottom) AbsState.Bot
        else {
          val localEnv = ctx1.pureLocal
          val (excValue, _) = localEnv.record.decEnvRec.GetBindingValue("@exception")
          val (oldExcAllValue, _) = env1.GetBindingValue("@exception_all")
          val (env2, _) = env1.SetMutableBinding("@exception", excValue)
          val (env3, _) = env2.SetMutableBinding("@exception_all", excValue + oldExcAllValue)
          val ctx2 = ctx1.subsPureLocal(envL.copyWith(record = env3))
          AbsState(st.heap, ctx2
            .setOldAddrSet(c2)
            .setThisBinding(data.thisBinding))
        }
      case (ExitExc(f), _) =>
        val old1 = st.context.old
        val (old2, env1) = data.old.fixOldify(data.env, old1.mayOld, old1.mustOld)
        if (old2.isBottom) AbsState.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          AbsState.Bot
        }
      case _ => st
    }
  }

  def C(cp: ControlPoint, st: AbsState): (AbsState, AbsState) = {
    if (st.isBottom) (AbsState.Bot, AbsState.Bot)
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
          val (argV, _) = localEnv.record.decEnvRec.GetBindingValue(fun.argumentsName)
          val (nSt, _) = xArgVars.foldLeft((st, 0))((res, x) => {
            val (iSt, i) = res
            val vi = argV.locset.foldLeft(AbsValue.Bot)((vk, lArg) => {
              vk + iSt.heap.get(lArg).Get(i.toString, iSt.heap)
            })
            (iSt.createMutableBinding(x, vi), i + 1)
          })
          val newSt = xLocalVars.foldLeft(nSt)((jSt, x) => {
            val undefV = AbsValue(Undef)
            jSt.createMutableBinding(x, undefV)
          })
          (newSt, AbsState.Bot)
        }
        case Exit(_) => (st, AbsState.Bot)
        case ExitExc(_) => (st, AbsState.Bot)
        case call: Call => CI(cp, call.callInst, st, AbsState.Bot)
        case afterCall: AfterCall => (st, AbsState.Bot)
        case afterCatch: AfterCatch => (st, AbsState.Bot)
        case block: NormalBlock =>
          block.getInsts.foldRight((st, AbsState.Bot))((inst, states) => {
            val (oldSt, oldExcSt) = states
            I(inst, oldSt, oldExcSt)
          })
        case ModelBlock(_, sem) => sem(st)
      }
    }
  }

  def I(i: CFGNormalInst, st: AbsState, excSt: AbsState): (AbsState, AbsState) = {
    i match {
      case _ if st.isBottom => (AbsState.Bot, excSt)
      case CFGAlloc(_, _, x, e, newAddr) => {
        val objProtoSingleton = AbsLoc(BuiltinObjectProto.loc)
        // Recency Abstraction
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)
        val (vLocSet, excSet) = e match {
          case None => (objProtoSingleton, ExcSetEmpty)
          case Some(proto) => {
            val (v, es) = V(proto, st1)
            if (!v.pvalue.isBottom)
              (v.locset + BuiltinObjectProto.loc, es)
            else
              (v.locset, es)
          }
        }
        val h2 = st1.heap.update(locR, AbsObject.newObject(vLocSet))
        val newSt = AbsState(h2, st1.context).varStore(x, AbsValue(locR))
        val newExcSt = st.raiseException(excSet)
        val s1 = excSt + newExcSt
        (newSt, s1)
      }
      case CFGAllocArray(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)
        val np = AbsNumber(n.toInt)
        val h2 = st1.heap.update(locR, AbsObject.newArrayObject(np))
        val newSt = AbsState(h2, st1.context).varStore(x, AbsValue(locR))
        (newSt, excSt)
      }
      case CFGAllocArg(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)
        val absN = AbsNumber(n.toInt)
        val h2 = st1.heap.update(locR, AbsObject.newArgObject(absN))
        val newSt = AbsState(h2, st1.context).varStore(x, AbsValue(locR))
        (newSt, excSt)
      }
      case CFGEnterCode(_, _, x, e) => {
        val (v, excSet) = V(e, st)
        val thisVal = AbsValue(v.getThis(st.heap))
        val st1 =
          if (!v.isBottom) st.varStore(x, thisVal)
          else AbsState.Bot
        val newExcSt = st.raiseException(excSet)
        (st1, excSt + newExcSt)
      }
      case CFGExprStmt(_, _, x, e) => {
        val (v, excSet) = V(e, st)
        val st1 =
          if (!v.isBottom) st.varStore(x, v)
          else AbsState.Bot
        val newExcSt = st.raiseException(excSet)
        (st1, excSt + newExcSt)
      }
      case CFGDelete(_, _, x1, CFGVarRef(_, x2)) => {
        val baseV = st.lookupBase(x2)
        val undefB = baseV.pvalue.undefval.fold(AB)(_ => AT)
        val (st1, locB) =
          baseV.locset.foldLeft[(AbsState, AbsBool)](AbsState.Bot, AB)((res, baseLoc) => {
            val (tmpState, tmpB) = res
            val (delState, delB) = st.delete(baseLoc, x2.text)
            (tmpState + delState, tmpB + delB)
          })
        val st2 = st1.varStore(x1, locB + undefB)
        (st2, excSt)
      }
      case CFGDelete(_, _, x1, expr) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) {
            val trueVal = AbsValue(AT)
            st.varStore(x1, trueVal)
          } else AbsState.Bot
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
        val (h1, b) = locSet.foldLeft[(AbsHeap, AbsBool)](AbsHeap.Bot, AB)((res1, l) => {
          val (tmpHeap1, tmpB1) = res1
          absStrSet.foldLeft((tmpHeap1, tmpB1))((res2, s) => {
            val (tmpHeap2, tmpB2) = res2
            val (delHeap, delB) = st.heap.delete(l, s)
            (tmpHeap2 + delHeap, tmpB2 + delB)
          })
        })
        val st1 = AbsState(h1, st.context)
        val st2 =
          if (st1.isBottom) AbsState.Bot
          else {
            st1.varStore(lhs, AbsValue(b))
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
            case (v, _) if v.isBottom => (AbsHeap.Bot, excSetIdx)
            case (_, v) if v.isBottom => (AbsHeap.Bot, excSetIdx ++ esRhs)
            case _ =>
              // iterate over set of strings for index
              val absStrSet = TypeConversionHelper.ToPrimitive(idxV, st.heap).toStringSet
              absStrSet.foldLeft((AbsHeap.Bot, excSetIdx ++ esRhs))((res1, absStr) => {
                val (tmpHeap1, tmpExcSet1) = res1
                val (tmpHeap2, tmpExcSet2) = Helper.storeHelp(locSet, absStr, vRhs, st.heap)
                (tmpHeap1 + tmpHeap2, tmpExcSet1 ++ tmpExcSet2)
              })
          }

        val newExcSt = st.raiseException(excSet1)
        (AbsState(heap1, st.context), excSt + newExcSt)
      }
      case CFGStoreStringIdx(_, block, obj, strIdx, rhs) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset
        val (vRhs, esRhs) = V(rhs, st)

        val (heap1, excSet1) =
          (strIdx, vRhs) match {
            case (_, v) if v.isBottom => (AbsHeap.Bot, esRhs)
            case (EJSString(str), v) =>
              val absStr = AbsString(str)
              val (tmpHeap2, tmpExcSet2) = Helper.storeHelp(locSet, absStr, vRhs, st.heap)
              (tmpHeap2, tmpExcSet2 ++ esRhs)
          }

        val newExcSt = st.raiseException(excSet1)
        (AbsState(heap1, st.context), excSt + newExcSt)
      }
      case CFGFunExpr(_, block, lhs, None, f, aNew1, aNew2, None) => {
        //Recency Abstraction
        val locR1 = Loc(aNew1, Recent)
        val locR2 = Loc(aNew2, Recent)
        val st1 = st.oldify(aNew1)
        val st2 = st1.oldify(aNew2)
        val oNew = AbsObject.newObject(BuiltinObjectProto.loc)

        val n = AbsNumber(f.argVars.length)
        val localEnv = st2.context.pureLocal
        val h3 = st2.heap.update(locR1, AbsObject.newFunctionObject(f.id, localEnv.outer, locR2, n))

        val fVal = AbsValue(locR1)
        val h4 = h3.update(locR2, oNew.update("constructor", AbsDataProp(fVal, AT, AF, AT)))

        val newSt = AbsState(h4, st2.context).varStore(lhs, fVal)
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

        val oNew = AbsObject.newObject(BuiltinObjectProto.loc)
        val n = AbsNumber(f.argVars.length)
        val fObjValue = AbsValue(locR3)
        val h4 = st3.heap.update(locR1, AbsObject.newFunctionObject(f.id, fObjValue, locR2, n))

        val fVal = AbsValue(locR1)
        val h5 = h4.update(locR2, oNew.update("constructor", AbsDataProp(fVal, AT, AF, AT)))

        val localEnv = st3.context.pureLocal
        val oEnv = AbsLexEnv.NewDeclarativeEnvironment(localEnv.outer)
        val oEnvRec2 = oEnv.record.decEnvRec
          .CreateImmutableBinding(name.text)
          .InitializeImmutableBinding(name.text, fVal)
        val newCtx = st3.context.update(locR3, oEnv.copyWith(record = oEnvRec2))
        val newSt = AbsState(h5, newCtx).varStore(lhs, fVal)
        (newSt, excSt)
      }
      case CFGAssert(_, _, expr, _) => B(expr, st, excSt)
      case CFGCatch(_, _, x) => {
        val localEnv = st.context.pureLocal
        val (excSetV, _) = localEnv.record.decEnvRec.GetBindingValue("@exception_all")
        val (excV, _) = localEnv.record.decEnvRec.GetBindingValue("@exception")
        val st1 = st.createMutableBinding(x, excV)
        val env = st1.context.pureLocal
        val (newEnv, _) = env.record.decEnvRec.SetMutableBinding("@exception", excSetV)
        val newCtx = st1.context.subsPureLocal(env.copyWith(record = newEnv))
        val newSt = AbsState(st1.heap, newCtx)
        (newSt, AbsState.Bot)
      }
      case CFGReturn(_, _, Some(expr)) => {
        val (v, excSet) = V(expr, st)
        val ctx1 =
          if (!v.isBottom) {
            val localEnv = st.context.pureLocal
            val (localEnv2, _) = localEnv.record.decEnvRec.SetMutableBinding("@return", v)
            st.context.subsPureLocal(localEnv.copyWith(record = localEnv2))
          } else AbsContext.Bot
        val newExcSt = st.raiseException(excSet)
        (AbsState(st.heap, ctx1), excSt + newExcSt)
      }
      case CFGReturn(_, _, None) => {
        val localEnv = st.context.pureLocal
        val (localEnv2, _) = localEnv.record.decEnvRec.SetMutableBinding("@return", AbsUndef.Top)
        val ctx1 = st.context.subsPureLocal(localEnv.copyWith(record = localEnv2))
        val newSt = AbsState(st.heap, ctx1)
        (newSt, excSt)
      }
      case CFGThrow(_, _, expr) => {
        val (v, excSet) = V(expr, st)
        val localEnv = st.context.pureLocal
        val (excSetV, _) = localEnv.record.decEnvRec.GetBindingValue("@exception_all")
        val (newEnv, _) = localEnv.record.decEnvRec.SetMutableBinding("@exception", v)
        val (newEnv2, _) = newEnv.SetMutableBinding("@exception_all", v + excSetV)
        val (newEnv3, _) = newEnv2
          .CreateMutableBinding("@return").fold(newEnv2)((e: AbsDecEnvRec) => e)
          .SetMutableBinding("@return", AbsUndef.Top)
        val ctx1 = st.context.subsPureLocal(localEnv.copyWith(record = newEnv3))
        val newExcSt = st.raiseException(excSet)

        (AbsState.Bot, excSt + AbsState(st.heap, ctx1) + newExcSt)
      }
      case CFGInternalCall(ir, _, lhs, fun, arguments, loc) =>
        IC(ir, lhs, fun, arguments, loc, st, excSt)
      case CFGNoOp(_, _, _) => (st, excSt)
    }
  }

  // internal API value
  lazy val internalValueMap: Map[String, AbsValue] = HashMap(
    NodeUtil.INTERNAL_TOP -> AbsValue.Top,
    NodeUtil.INTERNAL_UINT -> AbsNumber.UInt,
    NodeUtil.INTERNAL_GLOBAL -> AbsValue(BuiltinGlobal.loc),
    NodeUtil.INTERNAL_BOOL_TOP -> AbsBool.Top,
    NodeUtil.INTERNAL_NUM_TOP -> AbsNumber.Top,
    NodeUtil.INTERNAL_STR_TOP -> AbsString.Top,
    NodeUtil.INTERNAL_EVAL_ERR -> AbsValue(BuiltinEvalError.loc),
    NodeUtil.INTERNAL_RANGE_ERR -> AbsValue(BuiltinRangeError.loc),
    NodeUtil.INTERNAL_REF_ERR -> AbsValue(BuiltinRefError.loc),
    NodeUtil.INTERNAL_SYNTAX_ERR -> AbsValue(BuiltinSyntaxError.loc),
    NodeUtil.INTERNAL_TYPE_ERR -> AbsValue(BuiltinTypeError.loc),
    NodeUtil.INTERNAL_URI_ERR -> AbsValue(BuiltinURIError.loc),
    NodeUtil.INTERNAL_EVAL_ERR_PROTO -> AbsValue(BuiltinEvalErrorProto.loc),
    NodeUtil.INTERNAL_RANGE_ERR_PROTO -> AbsValue(BuiltinRangeErrorProto.loc),
    NodeUtil.INTERNAL_REF_ERR_PROTO -> AbsValue(BuiltinRefErrorProto.loc),
    NodeUtil.INTERNAL_SYNTAX_ERR_PROTO -> AbsValue(BuiltinSyntaxErrorProto.loc),
    NodeUtil.INTERNAL_TYPE_ERR_PROTO -> AbsValue(BuiltinTypeErrorProto.loc),
    NodeUtil.INTERNAL_URI_ERR_PROTO -> AbsValue(BuiltinURIErrorProto.loc),
    NodeUtil.INTERNAL_ERR_PROTO -> AbsValue(BuiltinErrorProto.loc),
    NodeUtil.INTERNAL_OBJ_CONST -> AbsValue(BuiltinObject.loc),
    NodeUtil.INTERNAL_ARRAY_CONST -> AbsValue(BuiltinArray.loc)
  )

  // internal API call
  // CFGInternalCall(ir, _, lhs, fun, arguments, loc)
  def IC(ir: IRNode, lhs: CFGId, fun: CFGId, args: List[CFGExpr], loc: Option[Address], st: AbsState, excSt: AbsState): (AbsState, AbsState) = (fun.toString, args, loc) match {
    case (NodeUtil.INTERNAL_TO_NUM, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToNumber(v, st.heap)))
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_TO_OBJ, List(expr), Some(aNew)) => {
      val (v, excSet1) = V(expr, st)
      val (newSt, newExcSet) =
        if (v.isBottom) {
          (AbsState.Bot, excSet1)
        } else {
          val (v1, st1, excSet2) = TypeConversionHelper.ToObject(v, st, aNew)
          val st2 =
            if (!v1.isBottom) st1.varStore(lhs, v1)
            else AbsState.Bot
          (st2, excSet1 ++ excSet2)
        }
      val newExcSt = st.raiseException(newExcSet)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_GET_BASE, List(CFGVarRef(_, x2)), None) => {
      val baseV = st.lookupBase(x2)
      val st1 = st.varStore(lhs, baseV)
      (st1, excSt)
    }
    case (NodeUtil.INTERNAL_IS_OBJ, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val st1 =
        if (!v.isBottom) {
          val b1 =
            if (!v.locset.isBottom) AT
            else AB
          val b2 =
            if (!v.pvalue.isBottom) AF
            else AB
          val boolVal = AbsValue(b1 + b2)
          st.varStore(lhs, boolVal)
        } else {
          AbsState.Bot
        }
      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_ITER_INIT, List(expr), Some(aNew)) => (st, excSt)
    case (NodeUtil.INTERNAL_HAS_NEXT, List(expr2, expr3), None) =>
      val st1 = st.varStore(lhs, AbsBool.Top)
      (st1, excSt)
    case (NodeUtil.INTERNAL_ITER_NEXT, List(expr2, expr3), None) =>
      val st1 = st.varStore(lhs, AbsString.Top)
      (st1, excSt)
    case _ =>
      excLog.signal(SemanticsNotYetImplementedError(ir))
      (AbsState.Bot, AbsState.Bot)
  }

  def CI(cp: ControlPoint, i: CFGCallInst, st: AbsState, excSt: AbsState): (AbsState, AbsState) = {
    // cons, thisArg and arguments must not be bottom
    val locR = Loc(i.addr, Recent)
    val st1 = st.oldify(i.addr)
    val (funVal, funExcSet) = V(i.fun, st1)
    val funLocSet = i match {
      case (_: CFGConstruct) => funVal.locset.filter(l => AT <= st1.heap.hasConstruct(l))
      case (_: CFGCall) => funVal.locset.filter(l => AT <= TypeConversionHelper.IsCallable(l, st1.heap))
    }
    val (thisVal, _) = V(i.thisArg, st1)
    // val thisVal = AbsValue(thisV.getThis(st.heap))
    val (argVal, _) = V(i.arguments, st1)

    // XXX: stop if thisArg or arguments is LocSetBot(ValueBot)
    if (thisVal.isBottom || argVal.isBottom) {
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
        val funObj = st1.heap.get(fLoc)
        val fidSet = i match {
          case _: CFGConstruct =>
            funObj(IConstruct).fidset
          case _: CFGCall =>
            funObj(ICall).fidset
        }
        fidSet.foreach((fid) => {
          val newEnv = AbsLexEnv.newPureLocal(AbsLoc(locR))
          val newCallCtx = callerCallCtx.newCallContext(cfg, fid, locR)
          cfg.getFunc(fid) match {
            case Some(funCFG) => {
              val scopeValue = funObj(IScope).value
              val (newEnv2, _) = newEnv.record.decEnvRec
                .CreateMutableBinding(funCFG.argumentsName)
                .SetMutableBinding(funCFG.argumentsName, argVal)
              val (newEnv3, _) = newEnv2
                .CreateMutableBinding("@scope")
                .SetMutableBinding("@scope", scopeValue)
              val entryCP = ControlPoint(funCFG.entry, newCallCtx)
              val exitCP = ControlPoint(funCFG.exit, newCallCtx)
              val exitExcCP = ControlPoint(funCFG.exitExc, newCallCtx)
              addCallEdge(cp, entryCP, EdgeData(
                OldAddrSet.Empty,
                newEnv.copyWith(record = newEnv3),
                thisVal
              ))
              addReturnEdge(exitCP, cpAfterCall, EdgeData(
                st1.context.old,
                oldLocalEnv,
                st1.context.thisBinding
              ))
              addReturnEdge(exitExcCP, cpAfterCatch, EdgeData(
                st1.context.old,
                oldLocalEnv,
                st1.context.thisBinding
              ))
            }
            case None => excLog.signal(UndefinedFunctionCallError(i.ir))
          }
        })
      })

      val h2 = argVal.locset.foldLeft(AbsHeap.Bot)((tmpHeap, l) => {
        val argObj = st1.heap.get(l)
        tmpHeap + st1.heap.update(l, argObj.update("callee", AbsDataProp(funLocSet, AT, AF, AT)))
      })

      // exception handling
      val typeExcSet1 = i match {
        case _: CFGConstruct if funVal.locset.exists(l => AF <= st1.heap.hasConstruct(l)) => HashSet(TypeError)
        case _: CFGCall if funVal.locset.exists(l => AF <= TypeConversionHelper.IsCallable(l, st1.heap)) => HashSet(TypeError)
        case _ => ExcSetEmpty
      }
      val typeExcSet2 =
        if (!funVal.pvalue.isBottom) HashSet(TypeError)
        else ExcSetEmpty

      val totalExcSet = funExcSet ++ typeExcSet1 ++ typeExcSet2
      val newExcSt = st1.raiseException(totalExcSet)

      val h3 =
        if (!funLocSet.isBottom) h2
        else AbsHeap.Bot

      val newSt = AbsState(h3, st1.context)
      (newSt, excSt + newExcSt)
    }
  }

  def V(expr: CFGExpr, st: AbsState): (AbsValue, Set[Exception]) = expr match {
    case CFGVarRef(ir, id) => st.lookup(id)
    case CFGLoad(ir, obj, index) => {
      val (objV, _) = V(obj, st)
      val (idxV, idxExcSet) = V(index, st)
      val absStrSet =
        if (!idxV.isBottom) TypeConversionHelper.ToPrimitive(idxV, st.heap).toStringSet
        else HashSet[AbsString]()
      val v1 = Helper.propLoad(objV, absStrSet, st.heap)
      (v1, idxExcSet)
    }
    case CFGThis(ir) =>
      (st.context.thisBinding, ExcSetEmpty)
    case CFGBin(ir, expr1, op, expr2) => {
      val (v1, excSet1) = V(expr1, st)
      val (v2, excSet2) = V(expr2, st)
      (v1, v2) match {
        case _ if v1.isBottom => (AbsValue.Bot, excSet1)
        case _ if v2.isBottom => (AbsValue.Bot, excSet1 ++ excSet2)
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
              val locSet3 = locSet2.filter((l) => AT <= st.heap.hasInstance(l))
              val protoVal = locSet3.foldLeft(AbsValue.Bot)((v, l) => {
                v + st.heap.get(l).Get("prototype", st.heap)
              })
              val locSet4 = protoVal.locset
              val locSet5 = locSet2.filter((l) => AF <= st.heap.hasInstance(l))
              val b1 = locSet1.foldLeft[AbsValue](AbsValue.Bot)((tmpVal1, loc1) => {
                locSet4.foldLeft[AbsValue](tmpVal1)((tmpVal2, loc2) =>
                  tmpVal2 + Helper.inherit(st.heap, loc1, loc2))
              })
              val b2 =
                if (!v1.pvalue.isBottom && !locSet4.isBottom) AbsValue(AF)
                else AbsValue.Bot
              val excSet3 =
                if (!v2.pvalue.isBottom || !locSet5.isBottom || !protoVal.pvalue.isBottom) HashSet(TypeError)
                else ExcSetEmpty
              val b = b1 + b2
              val excSet = excSet1 ++ excSet2 ++ excSet3
              (b, excSet)
            case "in" => {
              val str = TypeConversionHelper.ToString(v1, st.heap)
              val absB = v2.locset.foldLeft(AB)((tmpAbsB, loc) => {
                tmpAbsB + st.heap.get(loc).HasProperty(str, st.heap)
              })
              val b = AbsValue(absB)
              val excSet3 =
                if (!v2.pvalue.isBottom) HashSet(TypeError)
                else ExcSetEmpty
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
                if (excSet.contains(ReferenceError)) AbsString("undefined")
                else AbsString.Bot
              val absStr = absStr1 + absStr2
              (AbsValue(absStr), ExcSetEmpty)
            case _ =>
              val absStr = TypeConversionHelper.typeTag(v, st.heap)
              (AbsValue(absStr), excSet)
          }
      }
    }
    case CFGInternalValue(ir, name) => internalValueMap.get(name) match {
      case Some(value) => (value, ExcSetEmpty)
      case None =>
        excLog.signal(SemanticsNotYetImplementedError(ir))
        (AbsValue.Bot, ExcSetEmpty)
    }
    case CFGVal(ejsVal) =>
      val pvalue: AbsPValue = ejsVal match {
        case EJSNumber(_, num) => AbsPValue(num)
        case EJSString(str) => AbsPValue(str)
        case EJSBool(bool) => AbsPValue(bool)
        case EJSNull => AbsPValue(Null)
        case EJSUndef => AbsPValue(Undef)
      }
      (AbsValue(pvalue), ExcSetEmpty)
  }

  def B(expr: CFGExpr, st: AbsState, excSt: AbsState): (AbsState, AbsState) = {
    val st1 = st //TODO should be the pruned state

    val (v, excSet) = V(expr, st)
    val newExcSt = st.raiseException(excSet)
    val st2 =
      if (AbsBool(true) <= TypeConversionHelper.ToBoolean(v)) st1
      else AbsState.Bot

    (st2, excSt + newExcSt)
  }
}
