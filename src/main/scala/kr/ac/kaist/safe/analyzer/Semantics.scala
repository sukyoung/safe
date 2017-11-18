/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
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
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._

import scala.collection.immutable.{ HashMap, HashSet }
import scala.collection.mutable.{ HashMap => MHashMap, Map => MMap }

class Semantics(
    cfg: CFG,
    worklist: Worklist
) {
  lazy val excLog: ExcLog = new ExcLog

  private val AF = AbsBool.False
  private val AT = AbsBool.True
  private val AB = AbsBool.Bot

  // control point maps to state
  protected val cpToState: MMap[CFGBlock, MMap[TracePartition, AbsState]] = MHashMap()
  def getAllState: Map[CFGBlock, Map[TracePartition, AbsState]] =
    cpToState.toMap map { case (block, mmap) => block -> mmap.toMap }
  def getState(block: CFGBlock): Map[TracePartition, AbsState] =
    cpToState.getOrElse(block, {
      val newMap = MHashMap[TracePartition, AbsState]()
      cpToState(block) = newMap
      newMap
    }).toMap
  def getState(cp: ControlPoint): AbsState = {
    val block = cp.block
    val tp = cp.tracePartition
    getState(block).getOrElse(tp, AbsState.Bot)
  }
  def setState(cp: ControlPoint, state: AbsState): Unit = {
    val block = cp.block
    val tp = cp.tracePartition
    val map = cpToState.getOrElse(block, {
      val newMap = MHashMap[TracePartition, AbsState]()
      cpToState(block) = newMap
      newMap
    })
    if (state.isBottom) map -= tp
    else map(tp) = state
  }

  type IPSucc = Map[ControlPoint, EdgeData]
  type IPSuccMap = Map[ControlPoint, IPSucc]
  private var ipSuccMap: IPSuccMap = HashMap()
  def getAllIPSucc: IPSuccMap = ipSuccMap
  def setAllIPSucc(newMap: IPSuccMap): Unit = { ipSuccMap = newMap }
  def getInterProcSucc(cp: ControlPoint): Option[IPSucc] = ipSuccMap.get(cp)

  // Adds inter-procedural call edge from call-block cp1 to entry-block cp2.
  // Edge label ctx records callee context, which is joined if the edge existed already.
  def addIPEdge(cp1: ControlPoint, cp2: ControlPoint, data: EdgeData): Unit = {
    val updatedSuccMap = ipSuccMap.get(cp1) match {
      case None => HashMap(cp2 -> data)
      case Some(map2) => map2.get(cp2) match {
        case None =>
          map2 + (cp2 -> data)
        case Some(oldData) =>
          map2 + (cp2 -> (data + oldData))
      }
    }
    ipSuccMap += (cp1 -> updatedSuccMap)
  }

  def E(cp1: ControlPoint, cp2: ControlPoint, data: EdgeData, st: AbsState): AbsState = {
    (cp1.block, cp2.block) match {
      case (_, Entry(f)) => st.context match {
        case _ if st.context.isBottom => AbsState.Bot
        case ctx1: AbsContext => {
          val objEnv = data.env.record.decEnvRec.GetBindingValue("@scope") match {
            case (value, _) => AbsLexEnv.NewDeclarativeEnvironment(value.locset)
          }
          val (envRec, _) = data.env.record.decEnvRec.DeleteBinding("@scope")
          val ctx2 = ctx1.subsPureLocal(data.env.copyWith(record = envRec))
          val ctx3 = data.env.outer.foldLeft[AbsContext](AbsContext.Bot)((hi, locEnv) => {
            hi + ctx2.update(locEnv, objEnv)
          })
          AbsState(st.heap, ctx3
            .setOldASiteSet(data.old)
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
            .setOldASiteSet(old2)
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
            .setOldASiteSet(c2)
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
      cp.block match {
        case Entry(_) => {
          val fun = cp.block.func
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
        case call: Call => CI(cp, call.callInst, st, AbsState.Bot)
        case block: NormalBlock =>
          block.getInsts.foldRight((st, AbsState.Bot))((inst, states) => {
            val (oldSt, oldExcSt) = states
            I(inst, oldSt, oldExcSt)
          })
        case ModelBlock(_, sem) => sem(st)
        case _ => (st, AbsState.Bot)
      }
    }
  }

  def I(i: CFGNormalInst, st: AbsState, excSt: AbsState): (AbsState, AbsState) = {
    i match {
      case _ if st.isBottom => (AbsState.Bot, excSt)
      case CFGAlloc(_, _, x, e, newASite) => {
        val objProtoSingleton = AbsLoc(BuiltinObjectProto.loc)
        val loc = Loc(newASite)
        val st1 = st.oldify(loc)
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
        val h2 = st1.heap.update(loc, AbsObj.newObject(vLocSet))
        val newSt = AbsState(h2, st1.context).varStore(x, AbsValue(loc))
        val newExcSt = st.raiseException(excSet)
        val s1 = excSt + newExcSt
        (newSt, s1)
      }
      case CFGAllocArray(_, _, x, n, newASite) => {
        val loc = Loc(newASite)
        val st1 = st.oldify(loc)
        val np = AbsNum(n.toInt)
        val h2 = st1.heap.update(loc, AbsObj.newArrayObject(np))
        val newSt = AbsState(h2, st1.context).varStore(x, AbsValue(loc))
        (newSt, excSt)
      }
      case CFGAllocArg(_, _, x, n, newASite) => {
        val loc = Loc(newASite)
        val st1 = st.oldify(loc)
        val absN = AbsNum(n.toInt)
        val h2 = st1.heap.update(loc, AbsObj.newArgObject(absN))
        val newSt = AbsState(h2, st1.context).varStore(x, AbsValue(loc))
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
          if (v.isBottom) HashSet[AbsStr]()
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
              absStrSet.foldLeft[(AbsHeap, Set[Exception])]((AbsHeap.Bot, excSetIdx ++ esRhs))((res1, absStr) => {
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
              val absStr = AbsStr(str)
              val (tmpHeap2, tmpExcSet2) = Helper.storeHelp(locSet, absStr, vRhs, st.heap)
              (tmpHeap2, tmpExcSet2 ++ esRhs)
          }

        val newExcSt = st.raiseException(excSet1)
        (AbsState(heap1, st.context), excSt + newExcSt)
      }
      case CFGFunExpr(_, block, lhs, None, f, aNew1, aNew2, None) => {
        //Recency Abstraction
        val loc1 = Loc(aNew1)
        val loc2 = Loc(aNew2)
        val st1 = st.oldify(loc1)
        val st2 = st1.oldify(loc2)
        val oNew = AbsObj.newObject(BuiltinObjectProto.loc)

        val n = AbsNum(f.argVars.length)
        val localEnv = st2.context.pureLocal
        val h3 = st2.heap.update(loc1, AbsObj.newFunctionObject(f.id, localEnv.outer, loc2, n))

        val fVal = AbsValue(loc1)
        val h4 = h3.update(loc2, oNew.update("constructor", AbsDataProp(fVal, AT, AF, AT)))

        val newSt = AbsState(h4, st2.context).varStore(lhs, fVal)
        (newSt, excSt)
      }
      case CFGFunExpr(_, block, lhs, Some(name), f, aNew1, aNew2, Some(aNew3)) => {
        // Recency Abstraction
        val loc1 = Loc(aNew1)
        val loc2 = Loc(aNew2)
        val loc3 = Loc(aNew3)
        val st1 = st.oldify(loc1)
        val st2 = st1.oldify(loc2)
        val st3 = st2.oldify(loc3)

        val oNew = AbsObj.newObject(BuiltinObjectProto.loc)
        val n = AbsNum(f.argVars.length)
        val fObjValue = AbsValue(loc3)
        val h4 = st3.heap.update(loc1, AbsObj.newFunctionObject(f.id, fObjValue, loc2, n))

        val fVal = AbsValue(loc1)
        val h5 = h4.update(loc2, oNew.update("constructor", AbsDataProp(fVal, AT, AF, AT)))

        val localEnv = st3.context.pureLocal
        val oEnv = AbsLexEnv.NewDeclarativeEnvironment(localEnv.outer)
        val oEnvRec2 = oEnv.record.decEnvRec
          .CreateImmutableBinding(name.text)
          .InitializeImmutableBinding(name.text, fVal)
        val newCtx = st3.context.update(loc3, oEnv.copyWith(record = oEnvRec2))
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
      case CFGInternalCall(ir, _, lhs, name, arguments, loc) =>
        IC(ir, lhs, name, arguments, loc, st, excSt)
      case CFGNoOp(_, _, _) => (st, excSt)
    }
  }

  // internal API value
  def getInternalValue(name: String): Option[AbsValue] = name match {
    case (NodeUtil.INTERNAL_TOP) => Some(AbsValue.Top)
    case (NodeUtil.INTERNAL_UINT) => Some(AbsNum.UInt)
    case (NodeUtil.INTERNAL_NUINT) => Some(AbsNum.NUInt)
    case (NodeUtil.INTERNAL_GLOBAL) => Some(AbsValue(BuiltinGlobal.loc))
    case (NodeUtil.INTERNAL_BOOL_TOP) => Some(AbsBool.Top)
    case (NodeUtil.INTERNAL_NUM_TOP) => Some(AbsNum.Top)
    case (NodeUtil.INTERNAL_STR_TOP) => Some(AbsStr.Top)
    case (NodeUtil.INTERNAL_EVAL_ERR) => Some(AbsValue(BuiltinEvalError.loc))
    case (NodeUtil.INTERNAL_RANGE_ERR) => Some(AbsValue(BuiltinRangeError.loc))
    case (NodeUtil.INTERNAL_REF_ERR) => Some(AbsValue(BuiltinRefError.loc))
    case (NodeUtil.INTERNAL_SYNTAX_ERR) => Some(AbsValue(BuiltinSyntaxError.loc))
    case (NodeUtil.INTERNAL_TYPE_ERR) => Some(AbsValue(BuiltinTypeError.loc))
    case (NodeUtil.INTERNAL_URI_ERR) => Some(AbsValue(BuiltinURIError.loc))
    case (NodeUtil.INTERNAL_EVAL_ERR_PROTO) => Some(AbsValue(BuiltinEvalErrorProto.loc))
    case (NodeUtil.INTERNAL_RANGE_ERR_PROTO) => Some(AbsValue(BuiltinRangeErrorProto.loc))
    case (NodeUtil.INTERNAL_REF_ERR_PROTO) => Some(AbsValue(BuiltinRefErrorProto.loc))
    case (NodeUtil.INTERNAL_SYNTAX_ERR_PROTO) => Some(AbsValue(BuiltinSyntaxErrorProto.loc))
    case (NodeUtil.INTERNAL_TYPE_ERR_PROTO) => Some(AbsValue(BuiltinTypeErrorProto.loc))
    case (NodeUtil.INTERNAL_URI_ERR_PROTO) => Some(AbsValue(BuiltinURIErrorProto.loc))
    case (NodeUtil.INTERNAL_ERR_PROTO) => Some(AbsValue(BuiltinErrorProto.loc))
    case (NodeUtil.INTERNAL_OBJ_CONST) => Some(AbsValue(BuiltinObject.loc))
    case (NodeUtil.INTERNAL_ARRAY_CONST) => Some(AbsValue(BuiltinArray.loc))
    case _ => None
  }

  // internal API call
  // CFGInternalCall(ir, _, lhs, name, arguments, loc)
  def IC(ir: IRNode, lhs: CFGId, name: String, args: List[CFGExpr], loc: Option[AllocSite], st: AbsState, excSt: AbsState): (AbsState, AbsState) = (name, args, loc) match {
    case (NodeUtil.INTERNAL_CLASS, List(exprO, exprP), None) => {
      val (v, excSetO) = V(exprO, st)
      val (p, excSetP) = V(exprP, st)
      val newH = v.locset.foldLeft(st.heap) {
        case (h, loc) => {
          val obj = st.heap.get(loc)
          val newObj = obj.update(IClass, AbsIValue(p))
          h.update(loc, newObj)
        }
      }
      val newSt = AbsState(newH, st.context).varStore(lhs, p)
      val newExcSt = st.raiseException(excSetO ++ excSetP)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_CLASS, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val obj = st.heap.get(v.locset)
      val className = obj(IClass).value
      val st1 =
        if (!v.isBottom) st.varStore(lhs, className)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_PRIM_VAL, List(exprO, exprP), None) => {
      val (v, excSetO) = V(exprO, st)
      val (p, excSetP) = V(exprP, st)
      val newH = v.locset.foldLeft(st.heap) {
        case (h, loc) => {
          val obj = st.heap.get(loc)
          val newObj = obj.update(IPrimitiveValue, AbsIValue(p))
          h.update(loc, newObj)
        }
      }
      val newSt = AbsState(newH, st.context).varStore(lhs, p)
      val newExcSt = st.raiseException(excSetO ++ excSetP)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_PRIM_VAL, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val obj = st.heap.get(v.locset)
      val value = obj(IPrimitiveValue).value
      val st1 =
        if (!v.isBottom) st.varStore(lhs, value)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_PROTO, List(exprO, exprP), None) => {
      val (v, excSetO) = V(exprO, st)
      val (p, excSetP) = V(exprP, st)
      val newH = v.locset.foldLeft(st.heap) {
        case (h, loc) => {
          val obj = st.heap.get(loc)
          val newObj = obj.update(IPrototype, AbsIValue(p))
          h.update(loc, newObj)
        }
      }
      val newSt = AbsState(newH, st.context).varStore(lhs, p)
      val newExcSt = st.raiseException(excSetO ++ excSetP)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_PROTO, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val obj = st.heap.get(v.locset)
      val value = obj(IPrototype).value
      val st1 =
        if (!v.isBottom) st.varStore(lhs, value)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_EXTENSIBLE, List(exprO, exprP), None) => {
      val (v, excSetO) = V(exprO, st)
      val (p, excSetP) = V(exprP, st)
      val newH = v.locset.foldLeft(st.heap) {
        case (h, loc) => {
          val obj = st.heap.get(loc)
          val newObj = obj.update(IExtensible, AbsIValue(p))
          h.update(loc, newObj)
        }
      }
      val newSt = AbsState(newH, st.context).varStore(lhs, p)
      val newExcSt = st.raiseException(excSetO ++ excSetP)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_EXTENSIBLE, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val obj = st.heap.get(v.locset)
      val value = obj(IExtensible).value
      val st1 =
        if (!v.isBottom) st.varStore(lhs, value)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_GET_BASE, List(CFGVarRef(_, x2)), None) => {
      val baseV = st.lookupBase(x2)
      val st1 = st.varStore(lhs, baseV)
      (st1, excSt)
    }
    case (NodeUtil.INTERNAL_GET_OWN_PROP, List(exprO, exprP), Some(aNew)) => {
      val (v, excSetO) = V(exprO, st)
      val (p, excSetP) = V(exprP, st)
      val obj = st.heap.get(v.locset)
      val name = TypeConversionHelper.ToString(p)
      val (desc, undef) = obj.GetOwnProperty(name)
      val (retSt, retV, excSet) = if (!desc.isBottom) {
        val (descObj, excSet) = AbsObj.FromPropertyDescriptor(st.heap, desc)
        val descLoc = Loc(aNew)
        val state = st.oldify(descLoc)
        val retH = state.heap.update(descLoc, descObj.oldify(aNew))
        val retV = AbsValue(undef, AbsLoc(descLoc))
        (AbsState(retH, state.context), retV, excSet)
      } else (st, AbsValue(undef), ExcSetEmpty)
      val newSt = retSt.varStore(lhs, retV)
      val newExcSt = st.raiseException(excSetO ++ excSetP ++ excSet)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_DEF_OWN_PROP, List(exprO, exprP, exprA), None) => {
      val h = st.heap
      val (objV, excSetO) = V(exprO, st)
      val (propV, excSetP) = V(exprP, st)
      val (attrV, excSetA) = V(exprA, st)

      val name = propV.pvalue.strval
      // ToPropertyDescriptor ( Obj )
      // 1. If Type(Obj) is not Object throw a TypeError exception.
      val excSet =
        if (attrV.pvalue.isBottom) ExcSetEmpty
        else HashSet(TypeError)
      val attr = h.get(attrV.locset)
      val desc = AbsDesc.ToPropertyDescriptor(attr, h)
      val (retH, retExcSet) = objV.locset.foldLeft((h, excSet ++ excSetO ++ excSetP ++ excSetA)) {
        case ((heap, e), loc) => {
          val obj = heap.get(loc)
          val (retObj, _, newExcSet) = obj.DefineOwnProperty(h, name, desc, true)
          val retH = heap.update(loc, retObj)
          (retH, e ++ newExcSet)
        }
      }
      val retSt = AbsState(retH, st.context).varStore(lhs, AbsValue(objV.locset))
      val excSt = st.raiseException(retExcSet)
      (retSt, excSt)
    }
    case (NodeUtil.INTERNAL_TO_PRIM, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToPrimitive(v)))
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_TO_BOOL, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToBoolean(v)))
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_TO_NUM, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToNumber(v, st.heap)))
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_TO_INT, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToInteger(v, st.heap)))
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_TO_UINT, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToUInt32(v, st.heap)))
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_TO_STR, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.ToString(v, st.heap)))
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
    case (NodeUtil.INTERNAL_IS_CALLABLE, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, AbsValue(TypeConversionHelper.IsCallable(v, st.heap)))
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_SAME_VALUE, List(left, right), None) => {
      val (l, excSet1) = V(left, st)
      val (r, excSet2) = V(right, st)
      val st1 =
        if (!l.isBottom && !r.isBottom) {
          st.varStore(lhs, AbsValue(TypeConversionHelper.SameValue(st.heap, l, r)))
        } else AbsState.Bot

      val newExcSt = st.raiseException(excSet1 ++ excSet2)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_GET_OWN_PROP_NAMES, List(expr), Some(aNew)) => {
      val h = st.heap
      val (objV, excSet1) = V(expr, st)
      val arrASite = aNew
      val (keyStr, lenSet) = objV.locset.foldLeft((AbsStr.Bot, Set[Option[Int]]())) {
        case ((str, lenSet), loc) => {
          val obj = h.get(loc)
          val (keys, size) = obj.collectKeySet("") match {
            case ConInf() => (AbsStr.Top, None)
            case ConFin(set) => (AbsStr(set), Some(set.size))
          }
          (str + keys, lenSet + size)
        }
      }
      val (maxOpt, len) =
        if (lenSet.isEmpty) (None, AbsNum.Bot)
        else {
          val (opt, num) = lenSet.foldLeft[(Option[Int], AbsNum)]((Some(0), AbsNum.Bot)) {
            case ((None, _), _) | (_, None) => (None, AbsNum.Top)
            case ((Some(k), num), Some(t)) => (Some(math.max(k, t)), num + AbsNum(t))
          }
          (Some(opt), num)
        }

      // 1. If Type(O) is not Object throw a TypeError exception.
      val excSet2: Set[Exception] =
        if (objV.pvalue.isBottom) ExcSetEmpty
        else HashSet(TypeError)
      // 2. Let array be the result of creating a new Array object.
      // (XXX: we assign the length of the Array object as the number of properties)
      val array = AbsObj.newArrayObject(len)
      // 3. For each named own property P of O (with index n started from 0)
      //   a. Let name be the String value that is the name of P.
      val AT = (AbsBool.True, AbsAbsent.Bot)
      val name = AbsValue(AbsPValue(strval = keyStr))
      val desc = AbsDesc((name, AbsAbsent.Bot), AT, AT, AT)
      val (retObj, retExcSet) = maxOpt match {
        case Some(Some(max)) => (0 until max.toInt).foldLeft((array, excSet2)) {
          case ((obj, e), n) => {
            val prop = AbsStr(n.toString)
            // b. Call the [[DefineOwnProperty]] internal method of array with arguments
            //    ToString(n), the PropertyDescriptor {[[Value]]: name, [[Writable]]:
            //    true, [[Enumerable]]: true, [[Configurable]]:true}, and false.
            val (newObj, _, excSet) = obj.DefineOwnProperty(h, prop, desc, false)
            (obj + newObj, e ++ excSet)
          }
        }
        case Some(None) => (AbsObj.Top, excSet2 + TypeError + RangeError)
        case None => (AbsObj.Bot, excSet2)
      }

      val excSt = st.raiseException(excSet1 ++ retExcSet)

      // 5. Return array.
      retObj.isBottom match {
        case true => (AbsState.Bot, excSt)
        case false => {
          val arrLoc = Loc(arrASite)
          val state = st.oldify(arrLoc)
          val retHeap = state.heap.update(arrLoc, retObj.oldify(arrLoc))
          val excSt = state.raiseException(retExcSet)
          val st2 = AbsState(retHeap, state.context)
          val retSt = st2.varStore(lhs, AbsValue(arrLoc))

          (retSt, excSt)
        }
      }
    }
    case (NodeUtil.INTERNAL_STR_OBJ, List(expr), Some(aNew)) => {
      val (v, excSet) = V(expr, st)
      val str = TypeConversionHelper.ToString(v)
      val loc = Loc(aNew)
      val st1 = st.oldify(loc)
      val heap = st1.heap.update(loc, AbsObj.newStringObj(str))
      val st2 = AbsState(heap, st1.context)
      val st3 =
        if (!v.isBottom) st2.varStore(lhs, AbsValue(loc))
        else AbsState.Bot
      val newExcSt = st.raiseException(excSet)
      (st3, newExcSt)
    }
    case (NodeUtil.INTERNAL_BOOL_OBJ, List(expr), Some(aNew)) => {
      val (v, excSet) = V(expr, st)
      val bool = TypeConversionHelper.ToBoolean(v)
      val loc = Loc(aNew)
      val st1 = st.oldify(loc)
      val heap = st1.heap.update(loc, AbsObj.newBooleanObj(bool))
      val st2 = AbsState(heap, st1.context)
      val st3 =
        if (!v.isBottom) st2.varStore(lhs, AbsValue(loc))
        else AbsState.Bot
      val newExcSt = st.raiseException(excSet)
      (st3, newExcSt)
    }
    case (NodeUtil.INTERNAL_NUM_OBJ, List(expr), Some(aNew)) => {
      val (v, excSet) = V(expr, st)
      val num = TypeConversionHelper.ToNumber(v)
      val loc = Loc(aNew)
      val st1 = st.oldify(loc)
      val heap = st1.heap.update(loc, AbsObj.newNumberObj(num))
      val st2 = AbsState(heap, st1.context)
      val st3 =
        if (!v.isBottom) st2.varStore(lhs, AbsValue(loc))
        else AbsState.Bot
      val newExcSt = st.raiseException(excSet)
      (st3, newExcSt)
    }
    case (NodeUtil.INTERNAL_ABS, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).abs)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_ACOS, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).acos)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_ASIN, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).asin)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_ATAN, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).atan)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_ATAN_TWO, List(exprY, exprX), None) => {
      val (y, excSetY) = V(exprY, st)
      val (x, excSetX) = V(exprX, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(y, st.heap)
        .atan2(TypeConversionHelper.ToNumber(x, st.heap)))
      val st1 =
        if (!y.isBottom && !x.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSetX ++ excSetY)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_CEIL, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).ceil)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_COS, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).cos)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_EXP, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).exp)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_FLOOR, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).floor)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_LOG, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).log)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_POW, List(exprX, exprY), None) => {
      val (x, excSetX) = V(exprX, st)
      val (y, excSetY) = V(exprY, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(x, st.heap)
        .pow(TypeConversionHelper.ToNumber(y, st.heap)))
      val st1 =
        if (!x.isBottom && !y.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSetX ++ excSetY)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_ROUND, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).round)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_SIN, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).sin)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_SQRT, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).sqrt)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_TAN, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val resV = AbsValue(TypeConversionHelper.ToNumber(v, st.heap).tan)
      val st1 =
        if (!v.isBottom) st.varStore(lhs, resV)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
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
    case (NodeUtil.INTERNAL_ITER_INIT, List(expr), Some(aNew)) => {
      val (v, excSet1) = V(expr, st)
      val vObj = AbsValue(v.pvalue.copyWith(
        undefval = AbsUndef.Bot,
        nullval = AbsNull.Bot
      ), v.locset)
      val (locset, st1, excSet2) = TypeConversionHelper.ToObject(vObj, st, aNew)
      val (locset2, st2) =
        if (v.pvalue.undefval.isTop || v.pvalue.nullval.isTop) {
          val heap = st.heap
          val newObj = heap.get(locset) + AbsObj.Empty
          val loc = Loc(aNew)
          (locset + loc, AbsState(st1.heap + heap.update(loc, newObj), st.context))
        } else (locset, st1)
      val st3 = st2.varStore(lhs, AbsValue(AbsNum(0), locset2))
      val newExcSt = st.raiseException(excSet1 ++ excSet2)
      (st3, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_ITER_HAS_NEXT, List(_, expr), None) => {
      val heap = st.heap
      val (v, excSet) = V(expr, st)
      val locset = v.locset
      val cur = v.pvalue.numval
      val boolV = cur.gamma match {
        case ConInf() => AbsBool.Top
        case ConFin(idxSet) => idxSet.foldLeft(AbsBool.Bot) {
          case (b, idx) => locset.foldLeft(b) {
            case (b, loc) => {
              val (strList, astr) = heap.get(loc).keySetPair
              if (idx < strList.length) b + AbsBool.True
              else b + astr.fold(AbsBool.False) { _ => AbsBool.Top }
            }
          }
        }
      }
      val st1 = st.varStore(lhs, boolV)
      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_ITER_NEXT, List(_, expr @ CFGVarRef(_, id)), None) => {
      val heap = st.heap
      val (v, excSet) = V(expr, st)
      val locset = v.locset
      val cur = v.pvalue.numval
      val strV = locset.foldLeft(AbsStr.Bot) {
        case (str, loc) => {
          val obj = heap.get(loc)
          val (strList, astr) = heap.get(loc).keySetPair
          cur.gamma match {
            case ConInf() => str + AbsStr(strList.toSet) + astr
            case ConFin(idxSet) => idxSet.foldLeft(str) {
              case (str, Num(idx)) => {
                if (idx < strList.length) str + AbsStr(strList(idx.toInt))
                else str + astr
              }
            }
          }
        }
      }
      val st1 = st.varStore(lhs, strV)
      val next = AbsValue(cur.add(AbsNum(1)), locset)
      val st2 = st1.varStore(id, next)
      val newExcSt = st.raiseException(excSet)
      (st2, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_ADD_EVENT_FUNC, List(exprV), None) => {
      val (v, excSetV) = V(exprV, st)
      val id = NodeUtil.getInternalVarId(NodeUtil.INTERNAL_EVENT_FUNC)
      val (curV, excSetC) = st.lookup(id)
      val newSt = st.varStore(id, curV.locset + v.locset)
      val newExcSt = st.raiseException(excSetV ++ excSetC)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_GET_LOC, List(exprV), None) => {
      val (v, excSetV) = V(exprV, st)
      val locset = v.pvalue.strval.gamma match {
        case ConInf() => AbsLoc.Top
        case ConFin(strset) => AbsLoc(strset.map(str => Loc(str)))
      }
      val newSt = st.varStore(lhs, locset)
      val newExcSt = st.raiseException(excSetV)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_TARGET_FUN, List(exprO, exprP), None) => {
      val (v, excSetO) = V(exprO, st)
      val (p, excSetP) = V(exprP, st)
      val newH = v.locset.foldLeft(st.heap) {
        case (h, loc) => {
          val obj = st.heap.get(loc)
          val newObj = obj.update(ITargetFunction, AbsIValue(p))
          h.update(loc, newObj)
        }
      }
      val newSt = AbsState(newH, st.context).varStore(lhs, p)
      val newExcSt = st.raiseException(excSetO ++ excSetP)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_TARGET_FUN, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val obj = st.heap.get(v.locset)
      val value = obj(ITargetFunction).value
      val st1 =
        if (!v.isBottom) st.varStore(lhs, value)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_BOUND_THIS, List(exprO, exprP), None) => {
      val (v, excSetO) = V(exprO, st)
      val (p, excSetP) = V(exprP, st)
      val newH = v.locset.foldLeft(st.heap) {
        case (h, loc) => {
          val obj = st.heap.get(loc)
          val newObj = obj.update(IBoundThis, AbsIValue(p))
          h.update(loc, newObj)
        }
      }
      val newSt = AbsState(newH, st.context).varStore(lhs, p)
      val newExcSt = st.raiseException(excSetO ++ excSetP)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_BOUND_THIS, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val obj = st.heap.get(v.locset)
      val value = obj(IBoundThis).value
      val st1 =
        if (!v.isBottom) st.varStore(lhs, value)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_BOUND_ARGS, List(exprO, exprP), None) => {
      val (v, excSetO) = V(exprO, st)
      val (p, excSetP) = V(exprP, st)
      val newH = v.locset.foldLeft(st.heap) {
        case (h, loc) => {
          val obj = st.heap.get(loc)
          val newObj = obj.update(IBoundArgs, AbsIValue(p))
          h.update(loc, newObj)
        }
      }
      val newSt = AbsState(newH, st.context).varStore(lhs, p)
      val newExcSt = st.raiseException(excSetO ++ excSetP)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_BOUND_ARGS, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val obj = st.heap.get(v.locset)
      val value = obj(IBoundArgs).value
      val st1 =
        if (!v.isBottom) st.varStore(lhs, value)
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_GET_LOC, List(exprV), None) => {
      val (v, excSetV) = V(exprV, st)
      val locset = v.pvalue.strval.gamma match {
        case ConInf() => AbsLoc.Top
        case ConFin(strset) => AbsLoc(strset.map(str => Loc(str)))
      }
      val newSt = st.varStore(lhs, locset)
      val newExcSt = st.raiseException(excSetV)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_CALL, List(exprO, exprP), None) => {
      val (v, excSetO) = V(exprO, st)
      val (p, excSetP) = V(exprP, st)

      val obj = st.heap.get(p.locset)
      val fidset = obj(ICall).fidset

      val newH = v.locset.foldLeft(st.heap) {
        case (h, loc) => {
          val obj = st.heap.get(loc)
          val newObj = obj.update(ICall, fidset)
          h.update(loc, newObj)
        }
      }

      val newSt = AbsState(newH, st.context).varStore(lhs, p)
      val newExcSt = st.raiseException(excSetO ++ excSetP)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_CONSTRUCT, List(exprO, exprP), None) => {
      val (v, excSetO) = V(exprO, st)
      val (p, excSetP) = V(exprP, st)

      val obj = st.heap.get(p.locset)
      val cidset = obj(IConstruct).fidset

      val newH = v.locset.foldLeft(st.heap) {
        case (h, loc) => {
          val obj = st.heap.get(loc)
          val newObj = obj.update(IConstruct, cidset)
          h.update(loc, newObj)
        }
      }

      val newSt = AbsState(newH, st.context).varStore(lhs, p)
      val newExcSt = st.raiseException(excSetO ++ excSetP)
      (newSt, excSt + newExcSt)
    }
    case (NodeUtil.INTERNAL_HAS_CONST, List(expr), None) => {
      val (v, excSet) = V(expr, st)
      val obj = st.heap.get(v.locset)
      val isDomIn = obj.fold(AbsBool.False) { obj => (obj contains IConstruct) }
      val b1 =
        if (AbsBool.True <= isDomIn) AbsBool.True
        else AbsBool.Bot
      val b2 =
        if (AbsBool.False <= isDomIn) AbsBool.False
        else AbsBool.Bot

      val st1 =
        if (!v.isBottom) st.varStore(lhs, AbsValue(b1 + b2))
        else AbsState.Bot

      val newExcSt = st.raiseException(excSet)
      (st1, excSt + newExcSt)
    }
    case _ =>
      excLog.signal(SemanticsNotYetImplementedError(ir))
      (AbsState.Bot, AbsState.Bot)
  }

  def CI(cp: ControlPoint, i: CFGCallInst, st: AbsState, excSt: AbsState): (AbsState, AbsState) = {
    // cons, thisArg and arguments must not be bottom
    val loc = Loc(i.asite)
    val st1 = st.oldify(loc)
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
      val tp = cp.tracePartition
      val nCall = i.block
      val cpAfterCall = ControlPoint(nCall.afterCall, tp)
      val cpAfterCatch = ControlPoint(nCall.afterCatch, tp)

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
          cfg.getFunc(fid) match {
            case Some(funCFG) => {
              val scopeValue = funObj(IScope).value
              val newEnv = AbsLexEnv.newPureLocal(AbsLoc(loc))
              val (newRec, _) = newEnv.record.decEnvRec
                .CreateMutableBinding(funCFG.argumentsName)
                .SetMutableBinding(funCFG.argumentsName, argVal)
              val (newRec2, _) = newRec
                .CreateMutableBinding("@scope")
                .SetMutableBinding("@scope", scopeValue)
              val entryCP = cp.next(funCFG.entry, CFGEdgeCall)
              val newTP = entryCP.tracePartition
              val exitCP = ControlPoint(funCFG.exit, newTP)
              val exitExcCP = ControlPoint(funCFG.exitExc, newTP)
              addIPEdge(cp, entryCP, EdgeData(
                OldASiteSet.Empty,
                newEnv.copyWith(record = newRec2),
                thisVal
              ))
              addIPEdge(exitCP, cpAfterCall, EdgeData(
                st1.context.old,
                oldLocalEnv,
                st1.context.thisBinding
              ))
              addIPEdge(exitExcCP, cpAfterCatch, EdgeData(
                st1.context.old,
                oldLocalEnv,
                st1.context.thisBinding
              ))
            }
            case None => excLog.signal(UndefinedFunctionCallError(i.ir))
          }
        })
      })

      val h2 = argVal.locset.foldLeft[AbsHeap](AbsHeap.Bot)((tmpHeap, l) => {
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
        else HashSet[AbsStr]()
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
          val h = st.heap
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
            case "==" => (Helper.bopEqBetter(h, v1, v2), excSet1 ++ excSet2)
            case "!=" => (Helper.bopNeq(h, v1, v2), excSet1 ++ excSet2)
            case "===" => (Helper.bopSEq(h, v1, v2), excSet1 ++ excSet2)
            case "!==" => (Helper.bopSNeq(h, v1, v2), excSet1 ++ excSet2)
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
                if (excSet.contains(ReferenceError)) AbsStr("undefined")
                else AbsStr.Bot
              val absStr = absStr1 + absStr2
              (AbsValue(absStr), ExcSetEmpty)
            case _ =>
              val absStr = TypeConversionHelper.typeTag(v, st.heap)
              (AbsValue(absStr), excSet)
          }
      }
    }
    case CFGInternalValue(ir, name) => getInternalValue(name) match {
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

// Interprocedural edges
case class EdgeData(old: OldASiteSet, env: AbsLexEnv, thisBinding: AbsValue) {
  def +(other: EdgeData): EdgeData = EdgeData(
    this.old + other.old,
    this.env + other.env,
    this.thisBinding + other.thisBinding
  )
  def <=(other: EdgeData): Boolean = {
    this.old <= other.old &&
      this.env <= other.env &&
      this.thisBinding <= other.thisBinding
  }
  def </(other: EdgeData): Boolean = !(this <= other)
}
