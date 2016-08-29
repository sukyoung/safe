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
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._

import scala.collection.immutable.{ HashMap, HashSet }

class Semantics(
    cfg: CFG,
    worklist: Worklist,
    helper: Helper
) {
  lazy val excLog: ExcLog = new ExcLog
  val utils: Utils = helper.utils
  val pvalueU = utils.pvalue
  val valueU = utils.value
  val dataPropU = utils.dataProp

  private val AF = utils.absBool.False
  private val AT = utils.absBool.True
  private val AB = utils.absBool.Bot

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
            case Some(propV) => DecEnvRecord.newDeclEnvRecord(propV.objval.value)(utils)
            case None => DecEnvRecord.newDeclEnvRecord(valueU.Bot)(utils)
          }
          val env2 = env - "@scope"
          val ctx2 = ctx1.subsPureLocal(env2)
          val ctx3 = env2("@env") match {
            case Some(propV) =>
              propV.objval.value.locset.foldLeft(ExecContext.Bot)((hi, locEnv) => {
                hi + ctx2.update(locEnv, objEnv)
              })
            case None => ExecContext.Bot
          }
          State(st.heap, ExecContext(ctx3.map, old))
        }
      }
      case (Exit(_), _) if st.context.isBottom => State.Bot
      case (Exit(_), _) if st.context.old.isBottom => State.Bot
      case (Exit(f1), AfterCall(f2, retVar, call)) =>
        val (ctx1, old1) = (st.context, st.context.old)
        val (old2, env1) = old.fixOldify(env, old1.mayOld, old1.mustOld)(utils)
        if (old2.isBottom) State.Bot
        else {
          val localEnv = ctx1.pureLocal
          val returnV = localEnv.getOrElse("@return")(valueU.Bot) { _.objval.value }
          val ctx2 = ctx1.subsPureLocal(env1)
          val newSt = State(st.heap, ExecContext(ctx2.map, old2))
          newSt.varStore(retVar, returnV)(utils)
        }
      case (Exit(f), _) =>
        val old1 = st.context.old
        val (old2, env1) = old.fixOldify(env, old1.mayOld, old1.mustOld)(utils)
        if (old2.isBottom) State.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          State.Bot
        }
      case (ExitExc(_), _) if st.context.isBottom => State.Bot
      case (ExitExc(_), _) if st.context.old.isBottom => State.Bot
      case (ExitExc(_), AfterCatch(_, _)) =>
        val (ctx1, c1) = (st.context, st.context.old)
        val (c2, env1) = old.fixOldify(env, c1.mayOld, c1.mustOld)(utils)
        if (c2.isBottom) State.Bot
        else {
          val localEnv = ctx1.pureLocal
          val excValue = localEnv.getOrElse("@exception")(valueU.Bot) { _.objval.value }
          val excObjV = dataPropU(excValue)
          val oldExcAllValue = env1.getOrElse("@exception_all")(valueU.Bot) { _.objval.value }
          val newExcAllObjV = dataPropU(excValue + oldExcAllValue)
          val ctx2 = ctx1.subsPureLocal(env1
            .update("@exception", PropValue(excObjV))
            .update("@exception_all", PropValue(newExcAllObjV)))
          State(st.heap, ExecContext(ctx2.map, c2))
        }
      case (ExitExc(f), _) =>
        val old1 = st.context.old
        val (old2, env1) = old.fixOldify(env, old1.mayOld, old1.mustOld)(utils)
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
          val locSetArg = localEnv.getOrElse(fun.argumentsName)(LocSetEmpty) { _.objval.value.locset }
          val (nSt, _) = xArgVars.foldLeft((st, 0))((res, x) => {
            val (iSt, i) = res
            val vi = locSetArg.foldLeft(valueU.Bot)((vk, lArg) => {
              vk + iSt.heap.proto(lArg, utils.absString.alpha(i.toString))(utils)
            })
            (iSt.createMutableBinding(x, vi)(utils), i + 1)
          })
          val newSt = xLocalVars.foldLeft(nSt)((jSt, x) => {
            val undefV = valueU.alpha()
            jSt.createMutableBinding(x, undefV)(utils)
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
        val objProtoSingleton = HashSet(BuiltinObjectProto.loc)
        // Recency Abstraction
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)(utils)
        val (vLocSet, excSet) = e match {
          case None => (objProtoSingleton, ExceptionSetEmpty)
          case Some(proto) => {
            val (v, es) = V(proto, st1)
            if (!v.pvalue.isBottom)
              (v.locset ++ HashSet(BuiltinObjectProto.loc), es)
            else
              (v.locset, es)
          }
        }
        val h2 = st1.heap.update(locR, Obj.newObject(vLocSet)(utils))
        val newSt = State(h2, st1.context).varStore(x, valueU(HashSet(locR)))(utils)
        val newExcSt = st.raiseException(excSet)(utils)
        val s1 = excSt + newExcSt
        (newSt, s1)
      }
      case CFGAllocArray(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)(utils)
        val np = utils.absNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, Obj.newArrayObject(np)(utils))
        val newSt = State(h2, st1.context).varStore(x, valueU(HashSet(locR)))(utils)
        (newSt, excSt)
      }
      case CFGAllocArg(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)(utils)
        val absN = utils.absNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, Obj.newArgObject(absN)(utils))
        val newSt = State(h2, st1.context).varStore(x, valueU(HashSet(locR)))(utils)
        (newSt, excSt)
      }
      case CFGExprStmt(_, _, x, e) => {
        val (v, excSet) = V(e, st)
        val st1 =
          if (!v.isBottom) st.varStore(x, v)(utils)
          else State.Bot
        val newExcSt = st.raiseException(excSet)(utils)
        (st1, excSt + newExcSt)
      }
      case CFGDelete(_, _, x1, CFGVarRef(_, x2)) => {
        val baseLocSet = st.lookupBase(x2)(utils)
        val (st1, b) =
          if (baseLocSet.isEmpty) {
            (st, AT)
          } else {
            val x2Abs = utils.absString.alpha(x2.toString)
            baseLocSet.foldLeft[(State, AbsBool)](State.Bot, AB)((res, baseLoc) => {
              val (tmpState, tmpB) = res
              val (delState, delB) = st.delete(baseLoc, x2Abs)(utils)
              (tmpState + delState, tmpB + delB)
            })
          }
        val bVal = valueU(b)
        val st2 = st1.varStore(x1, bVal)(utils)
        (st2, excSt)
      }
      case CFGDelete(_, _, x1, expr) => {
        val (v, excSet) = V(expr, st)
        val st1 =
          if (!v.isBottom) {
            val trueVal = valueU(AT)
            st.varStore(x1, trueVal)(utils)
          } else State.Bot
        val newExcSt = st.raiseException(excSet)(utils)
        (st1, excSt + newExcSt)
      }
      case CFGDeleteProp(_, _, lhs, obj, index) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset
        val (v, excSet) = V(index, st)
        val absStrSet =
          if (v.isBottom) HashSet[AbsString]()
          else v.toPrimitiveBetter(st.heap)(utils).toStringSet(utils.absString)
        val (st1, b) = locSet.foldLeft[(State, AbsBool)](State.Bot, AB)((res1, l) => {
          val (tmpState1, tmpB1) = res1
          absStrSet.foldLeft((tmpState1, tmpB1))((res2, s) => {
            val (tmpState2, tmpB2) = res2
            val (delState, delB) = st.delete(l, s)(utils)
            (tmpState2 + delState, tmpB2 + delB)
          })
        })
        val st2 =
          if (st1.isBottom) State.Bot
          else {
            val boolPV = pvalueU(b)
            st1.varStore(lhs, valueU(boolPV))(utils)
          }
        val newExcSt = st.raiseException(excSet)(utils)
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
              val absStrSet = idxV.toPrimitiveBetter(st.heap)(utils).toStringSet(utils.absString)
              absStrSet.foldLeft((Heap.Bot, excSetIdx ++ esRhs))((res1, absStr) => {
                val (tmpHeap1, tmpExcSet1) = res1
                val (tmpHeap2, tmpExcSet2) = helper.storeHelp(locSet, absStr, vRhs, st.heap)
                (tmpHeap1 + tmpHeap2, tmpExcSet1 ++ tmpExcSet2)
              })
          }

        val newExcSt = st.raiseException(excSet1)(utils)
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
              val absStr = utils.absString.alpha(str)
              val (tmpHeap2, tmpExcSet2) = helper.storeHelp(locSet, absStr, vRhs, st.heap)
              (tmpHeap2, tmpExcSet2 ++ esRhs)
          }

        val newExcSt = st.raiseException(excSet1)(utils)
        (State(heap1, st.context), excSt + newExcSt)
      }
      case CFGFunExpr(_, block, lhs, None, f, aNew1, aNew2, None) => {
        //Recency Abstraction
        val locR1 = Loc(aNew1, Recent)
        val locR2 = Loc(aNew2, Recent)
        val st1 = st.oldify(aNew1)(utils)
        val st2 = st1.oldify(aNew2)(utils)
        val oNew = Obj.newObject(BuiltinObjectProto.loc)(utils)

        val n = utils.absNumber.alpha(f.argVars.length)
        val localEnv = st2.context.pureLocal
        val scope = localEnv.getOrElse("@env")(valueU.Bot) { _.objval.value }
        val h3 = st2.heap.update(locR1, Obj.newFunctionObject(f.id, scope, locR2, n)(utils))

        val fVal = valueU(HashSet(locR1))
        val fPropV = PropValue(dataPropU(fVal)(AT, AF, AT))
        val h4 = h3.update(locR2, oNew.update("constructor", fPropV, exist = true))

        val newSt = State(h4, st2.context).varStore(lhs, fVal)(utils)
        (newSt, excSt)
      }
      case CFGFunExpr(_, block, lhs, Some(name), f, aNew1, aNew2, Some(aNew3)) => {
        // Recency Abstraction
        val locR1 = Loc(aNew1, Recent)
        val locR2 = Loc(aNew2, Recent)
        val locR3 = Loc(aNew3, Recent)
        val st1 = st.oldify(aNew1)(utils)
        val st2 = st1.oldify(aNew2)(utils)
        val st3 = st2.oldify(aNew3)(utils)

        val oNew = Obj.newObject(BuiltinObjectProto.loc)(utils)
        val n = utils.absNumber.alpha(f.argVars.length)
        val fObjValue = valueU(HashSet(locR3))
        val h4 = st3.heap.update(locR1, Obj.newFunctionObject(f.id, fObjValue, locR2, n)(utils))

        val fVal = valueU(HashSet(locR1))
        val fPropV = PropValue(dataPropU(fVal)(AT, AF, AT))
        val h5 = h4.update(locR2, oNew.update("constructor", fPropV, exist = true))

        val localEnv = st3.context.pureLocal
        val scope = localEnv.getOrElse("@env")(valueU.Bot) { _.objval.value }
        val oEnv = DecEnvRecord.newDeclEnvRecord(scope)(utils)
        val fPropV2 = PropValue(dataPropU(fVal)(AF, AB, AF))
        val newCtx = st3.context.update(locR3, oEnv.update(name.text, fPropV2))
        val newSt = State(h5, newCtx).varStore(lhs, fVal)(utils)
        (newSt, excSt)
      }
      case CFGAssert(_, _, expr, _) => B(expr, st, excSt, i, cfg)
      case CFGCatch(_, _, x) => {
        val localEnv = st.context.pureLocal
        val excSetPropV = localEnv.get("@exception_all")(utils)
        val excV = localEnv.getOrElse("@exception")(valueU.Bot) { _.objval.value }
        val st1 = st.createMutableBinding(x, excV)(utils)
        val newEnv = st1.context.pureLocal.update("@exception", excSetPropV)
        val newCtx = st1.context.subsPureLocal(newEnv)
        val newSt = State(st1.heap, newCtx)
        (newSt, State.Bot)
      }
      case CFGReturn(_, _, Some(expr)) => {
        val (v, excSet) = V(expr, st)
        val ctx1 =
          if (!v.isBottom) {
            val localEnv = st.context.pureLocal
            val retValPropV = PropValue(dataPropU(v))
            st.context.subsPureLocal(localEnv.update("@return", retValPropV))
          } else ExecContext.Bot
        val newExcSt = st.raiseException(excSet)(utils)
        (State(st.heap, ctx1), excSt + newExcSt)
      }
      case CFGReturn(_, _, None) => {
        val localEnv = st.context.pureLocal
        val retValPropV = PropValue(utils.absUndef.Top)(utils)
        val ctx1 = st.context.subsPureLocal(localEnv.update("@return", retValPropV))
        val newSt = State(st.heap, ctx1)
        (newSt, excSt)
      }
      case CFGThrow(_, _, expr) => {
        val (v, excSet) = V(expr, st)
        val localEnv = st.context.pureLocal
        val excSetV = localEnv.getOrElse("@exception_all")(valueU.Bot) { _.objval.value }
        val newExcPropV = PropValue(dataPropU(v))
        val newExcSetPropV = PropValue(dataPropU(v + excSetV))
        val retValPropV = PropValue(utils.absUndef.Top)(utils)
        val newEnv =
          localEnv.
            update("@exception", newExcPropV).
            update("@exception_all", newExcSetPropV).
            update("@return", retValPropV)
        val ctx1 = st.context.subsPureLocal(newEnv)
        val newExcSt = st.raiseException(excSet)(utils)

        (State.Bot, excSt + State(st.heap, ctx1) + newExcSt)
      }
      case CFGInternalCall(ir, info, lhs, fun, arguments, loc) =>
        (fun.toString, arguments, loc) match {
          case ("<>Global<>toObject", List(expr), Some(aNew)) => {
            val (v, excSet1) = V(expr, st)
            val (v1, st1, excSet2) = helper.toObject(st, v, aNew)
            val st2 =
              if (!v1.isBottom)
                st1.varStore(lhs, v1)(utils)
              else
                State.Bot
            val (st3, excSet3) =
              if (!v.isBottom)
                (st2, excSet1 ++ excSet2)
              else
                (State.Bot, excSet1)
            val newExcSt = st.raiseException(excSet3)(utils)
            (st3, excSt + newExcSt)
          }
          case ("<>Global<>isObject", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val st1 =
              if (!v.isBottom) {
                val b1 =
                  if (!v.locset.isEmpty) AT
                  else AB
                val b2 =
                  if (!v.pvalue.isBottom) AF
                  else AB
                val boolVal = valueU(pvalueU(b1 + b2))
                st.varStore(lhs, boolVal)(utils)
              } else {
                State.Bot
              }
            val newExcSt = st.raiseException(excSet)(utils)
            (st1, excSt + newExcSt)
          }
          case ("<>Global<>toNumber", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val st1 =
              if (!v.isBottom) {
                val numPV = v.toPrimitiveBetter(st.heap)(utils)
                val numPV2 = pvalueU(numPV.toAbsNumber(utils.absNumber))
                st.varStore(lhs, valueU(numPV2))(utils)
              } else {
                State.Bot
              }
            val newExcSt = st.raiseException(excSet)(utils)
            (st1, excSt + newExcSt)
          }
          case ("<>Global<>toBoolean", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val st1 =
              if (!v.isBottom) {
                val boolPV = pvalueU(v.toAbsBoolean(utils.absBool))
                st.varStore(lhs, valueU(boolPV))(utils)
              } else {
                State.Bot
              }
            val newExcSt = st.raiseException(excSet)(utils)
            (st1, excSt + newExcSt)
          }
          case ("<>Global<>getBase", List(CFGVarRef(_, x2)), None) => {
            val locSetBase = st.lookupBase(x2)(utils)
            val st1 = st.varStore(lhs, valueU(locSetBase))(utils)
            (st1, excSt)
          }
          case ("<>Global<>iteratorInit", List(expr), Some(aNew)) => (st, excSt)
          case ("<>Global<>iteratorHasNext", List(expr2, expr3), None) =>
            val boolPV = pvalueU(utils.absBool.Top)
            val st1 = st.varStore(lhs, valueU(boolPV))(utils)
            (st1, excSt)
          case ("<>Global<>iteratorNext", List(expr2, expr3), None) =>
            val strPV = pvalueU(utils.absString.Top)
            val st1 = st.varStore(lhs, valueU(strPV))(utils)
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
    val st1 = st.oldify(i.addr1)(utils)
    val (funVal, funExcSet) = V(i.fun, st1)
    val funLocSet = i match {
      case (_: CFGConstruct) => funVal.locset.filter(l => AT <= st1.heap.hasConstruct(l)(utils.absBool))
      case (_: CFGCall) => funVal.locset.filter(l => AT <= st1.heap.isCallable(l)(utils.absBool))
    }
    val (thisVal, _) = V(i.thisArg, st1)
    val thisLocSet = thisVal.getThis(st1.heap)(utils)
    val (argVal, _) = V(i.arguments, st1)

    // XXX: stop if thisArg or arguments is LocSetBot(ValueBot)
    if (thisLocSet.isEmpty || argVal.isBottom) {
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
        val funObj = st1.heap.getOrElse(fLoc, Obj.Bot(utils))
        val fidSet = i match {
          case _: CFGConstruct =>
            funObj.getOrElse[Set[FunctionId]]("@construct")(HashSet[FunctionId]()) { _.funid }
          case _: CFGCall =>
            funObj.getOrElse[Set[FunctionId]]("@function")(HashSet[FunctionId]()) { _.funid }
        }
        fidSet.foreach((fid) => {
          val newPureLocal = DecEnvRecord.newPureLocal(valueU(locR), thisLocSet)(utils)
          val callerCtxSet = callerCallCtx.newCallContext(st1.heap, cfg, fid, locR, thisLocSet, newPureLocal, Some(i.addr1))
          callerCtxSet.foreach {
            case (newCallCtx, newEnv) => {
              val argPropV = PropValue(dataPropU(argVal)(AT, AF, AF))
              cfg.getFunc(fid) match {
                case Some(funCFG) => {
                  val scopeObj = funObj.get("@scope")(utils)
                  val newEnv2 = newEnv.update(funCFG.argumentsName, argPropV, exist = true)
                    .update("@scope", scopeObj)
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
        val funPropV = PropValue(dataPropU(funLocSet)(AT, AF, AT))
        val argObj = st1.heap.getOrElse(l, Obj.Bot(utils))
        tmpHeap + st1.heap.update(l, argObj.update("callee", funPropV))
      })

      // exception handling
      val typeExcSet1 = i match {
        case _: CFGConstruct if funVal.locset.exists(l => AF <= st1.heap.hasConstruct(l)(utils.absBool)) => HashSet(TypeError)
        case _: CFGCall if funVal.locset.exists(l => AF <= st1.heap.isCallable(l)(utils.absBool)) => HashSet(TypeError)
        case _ => ExceptionSetEmpty
      }
      val typeExcSet2 =
        if (!funVal.pvalue.isBottom) HashSet(TypeError)
        else ExceptionSetEmpty

      val totalExcSet = funExcSet ++ typeExcSet1 ++ typeExcSet2
      val newExcSt = st1.raiseException(totalExcSet)(utils)

      val h3 =
        if (!funLocSet.isEmpty) h2
        else Heap.Bot

      val newSt = State(h3, st1.context)
      (newSt, excSt + newExcSt)
    }
  }

  def V(expr: CFGExpr, st: State): (Value, Set[Exception]) = {
    expr match {
      case CFGVarRef(ir, id) => st.lookup(id)(utils)
      case CFGLoad(ir, obj, index) => {
        val (objV, _) = V(obj, st)
        val (idxV, idxExcSet) = V(index, st)
        val absStrSet =
          if (!idxV.isBottom) idxV.toPrimitiveBetter(st.heap)(utils).toStringSet(utils.absString)
          else HashSet[AbsString]()
        val v1 = CFGLoadHelper(objV, absStrSet, st.heap)
        (v1, idxExcSet)
      }
      case CFGThis(ir) =>
        val localEnv = st.context.pureLocal
        val thisLocSet = localEnv.getOrElse("@this")(LocSetEmpty) { _.objval.value.locset }
        (valueU(thisLocSet), ExceptionSetEmpty)
      case CFGBin(ir, expr1, op, expr2) => {
        val (v1, excSet1) = V(expr1, st)
        val (v2, excSet2) = V(expr2, st)
        (v1, v2) match {
          case _ if v1.isBottom => (valueU.Bot, excSet1)
          case _ if v2.isBottom => (valueU.Bot, excSet1 ++ excSet2)
          case _ =>
            op.name match {
              case "|" => (helper.bopBitOr(v1, v2), excSet1 ++ excSet2)
              case "&" => (helper.bopBitAnd(v1, v2), excSet1 ++ excSet2)
              case "^" => (helper.bopBitXor(v1, v2), excSet1 ++ excSet2)
              case "<<" => (helper.bopLShift(v1, v2), excSet1 ++ excSet2)
              case ">>" => (helper.bopRShift(v1, v2), excSet1 ++ excSet2)
              case ">>>" => (helper.bopURShift(v1, v2), excSet1 ++ excSet2)
              case "+" => (helper.bopPlus(v1, v2), excSet1 ++ excSet2)
              case "-" => (helper.bopMinus(v1, v2), excSet1 ++ excSet2)
              case "*" => (helper.bopMul(v1, v2), excSet1 ++ excSet2)
              case "/" => (helper.bopDiv(v1, v2), excSet1 ++ excSet2)
              case "%" => (helper.bopMod(v1, v2), excSet1 ++ excSet2)
              case "==" => (helper.bopEqBetter(st.heap, v1, v2), excSet1 ++ excSet2)
              case "!=" => (helper.bopNeq(v1, v2), excSet1 ++ excSet2)
              case "===" => (helper.bopSEq(v1, v2), excSet1 ++ excSet2)
              case "!==" => (helper.bopSNeq(v1, v2), excSet1 ++ excSet2)
              case "<" => (helper.bopLess(v1, v2), excSet1 ++ excSet2)
              case ">" => (helper.bopGreater(v1, v2), excSet1 ++ excSet2)
              case "<=" => (helper.bopLessEq(v1, v2), excSet1 ++ excSet2)
              case ">=" => (helper.bopGreaterEq(v1, v2), excSet1 ++ excSet2)
              case "instanceof" =>
                val locSet1 = v1.locset
                val locSet2 = v2.locset
                val locSet3 = locSet2.filter((l) => AT <= st.heap.hasInstance(l)(utils.absBool))
                val protoVal = locSet3.foldLeft(valueU.Bot)((v, l) => {
                  v + st.heap.proto(l, utils.absString.alpha("prototype"))(utils)
                })
                val locSet4 = protoVal.locset
                val locSet5 = locSet2.filter((l) => AF <= st.heap.hasInstance(l)(utils.absBool))
                val b1 = locSet1.foldLeft[Value](valueU.Bot)((tmpVal1, loc1) => {
                  locSet4.foldLeft[Value](tmpVal1)((tmpVal2, loc2) =>
                    tmpVal2 + helper.inherit(st.heap, loc1, loc2))
                })
                val pv2 =
                  if (!v2.pvalue.isBottom && !locSet4.isEmpty) pvalueU(AF)
                  else pvalueU.Bot
                val b2 = valueU(pv2)
                val excSet3 =
                  if (!v2.pvalue.isBottom || !locSet5.isEmpty || !protoVal.pvalue.isBottom) HashSet(TypeError)
                  else ExceptionSetEmpty
                val b = b1 + b2
                val excSet = excSet1 ++ excSet2 ++ excSet3
                (b, excSet)
              case "in" => {
                val str = v1.toPrimitiveBetter(st.heap)(utils).toAbsString(utils.absString)
                val absB = v2.locset.foldLeft(AB)((tmpAbsB, loc) => {
                  tmpAbsB + st.heap.hasProperty(loc, str)(utils)
                })
                val b = valueU(pvalueU(absB))
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
          case "void" => (helper.uVoid(v), excSet)
          case "+" => (helper.uopPlus(v), excSet)
          case "-" => (helper.uopMinusBetter(st.heap, v), excSet)
          case "~" => (helper.uopBitNeg(v), excSet)
          case "!" => (helper.uopNeg(v), excSet)
          case "typeof" =>
            expr match {
              case CFGVarRef(_, x) =>
                val absStr1 = v.typeTag(st.heap)(utils)
                val absStr2 =
                  if (excSet.contains(ReferenceError)) utils.absString.alpha("undefined")
                  else utils.absString.Bot
                val absStrPV = pvalueU(absStr1 + absStr2)
                (valueU(absStrPV), ExceptionSetEmpty)
              case _ =>
                val absStrPV = pvalueU(v.typeTag(st.heap)(utils))
                (valueU(absStrPV), excSet)
            }
        }
      }
      case CFGVal(ejsVal) =>
        val pvalue: PValue = ejsVal match {
          case EJSNumber(_, num) => pvalueU.alpha(num)
          case EJSString(str) => pvalueU.alpha(str)
          case EJSBool(bool) => pvalueU.alpha(bool)
          case EJSNull => pvalueU.alpha(null)
          case EJSUndef => pvalueU.alpha()
        }
        (valueU(pvalue), ExceptionSetEmpty)
    }
  }

  def B(expr: CFGExpr, st: State, excSt: State, inst: CFGInst, cfg: CFG): (State, State) = {
    val st1 = st //TODO should be the pruned state

    val (v, excSet) = V(expr, st)
    val newExcSt = st.raiseException(excSet)(utils)
    val st2 =
      if (utils.absBool.alpha(true) <= v.toAbsBoolean(utils.absBool)) st1
      else State.Bot

    (st2, excSt + newExcSt)
  }

  def CFGLoadHelper(objV: Value, absStrSet: Set[AbsString], h: Heap): Value = {
    val objLocSet = objV.locset
    val v1 = objLocSet.foldLeft(valueU.Bot)((tmpVal1, loc) => {
      absStrSet.foldLeft(tmpVal1)((tmpVal2, absStr) => {
        tmpVal2 + h.proto(loc, absStr)(utils)
      })
    })
    v1
  }
}
