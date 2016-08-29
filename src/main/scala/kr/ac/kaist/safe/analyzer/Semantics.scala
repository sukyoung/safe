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
  private var ipSuccMap: Map[ControlPoint, Map[ControlPoint, (OldAddrSet, Obj)]] = HashMap()
  private var ipPredMap: Map[ControlPoint, Set[ControlPoint]] = HashMap()
  def getAllIPSucc: Map[ControlPoint, Map[ControlPoint, (OldAddrSet, Obj)]] = ipSuccMap
  def getAllIPPred: Map[ControlPoint, Set[ControlPoint]] = ipPredMap
  def getInterProcSucc(cp: ControlPoint): Option[Map[ControlPoint, (OldAddrSet, Obj)]] = ipSuccMap.get(cp)
  def getInterProcPred(cp: ControlPoint): Option[Set[ControlPoint]] = ipPredMap.get(cp)

  // Adds inter-procedural call edge from call-node cp1 to entry-node cp2.
  // Edge label ctx records callee context, which is joined if the edge existed already.
  def addCallEdge(cp1: ControlPoint, cp2: ControlPoint, old: OldAddrSet, obj: Obj): Unit = {
    val updatedSuccMap = ipSuccMap.get(cp1) match {
      case None => HashMap(cp2 -> (old, obj))
      case Some(map2) =>
        map2.get(cp2) match {
          case None =>
            map2 + (cp2 -> (old, obj))
          case Some((oldOld, oldObj)) =>
            map2 + (cp2 -> (oldOld + old, oldObj + obj))
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
  def addReturnEdge(cp1: ControlPoint, cp2: ControlPoint, old: OldAddrSet, obj: Obj): Unit = {
    val updatedSuccMap = ipSuccMap.get(cp1) match {
      case None => {
        worklist.add(cp1)
        HashMap(cp2 -> (old, obj))
      }
      case Some(map2) =>
        map2.get(cp2) match {
          case None => {
            worklist.add(cp1)
            map2 + (cp2 -> (old, obj))
          }
          case Some((oldOld, oldObj)) =>
            val oldChanged = !(old <= oldOld)
            val newOld =
              if (oldChanged) oldOld + old
              else oldOld
            val objChanged = !(obj <= oldObj)
            val newObj =
              if (objChanged) oldObj + obj
              else oldObj
            if (oldChanged || objChanged) {
              worklist.add(cp1)
              map2 + (cp2 -> (newOld, newObj))
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

  def E(cp1: ControlPoint, cp2: ControlPoint, old: OldAddrSet, obj: Obj, st: State): State = {
    (cp1.node, cp2.node) match {
      case (_, Entry(f)) => st.heap match {
        case State.Bot => State.Bot
        case h1: Heap => {
          val objEnv = obj("@scope") match {
            case Some(propV) => Obj.newDeclEnvRecordObj(propV.objval.value)(utils)
            case None => Obj.newDeclEnvRecordObj(valueU.Bot)(utils)
          }
          val obj2 = obj - "@scope"
          val h2 = h1.remove(PredefLoc.SINGLE_PURE_LOCAL).update(PredefLoc.SINGLE_PURE_LOCAL, obj2)
          val h3 = obj2("@env") match {
            case Some(propV) =>
              propV.objval.value.locset.foldLeft(Heap.Bot)((hi, locEnv) => {
                hi + h2.update(locEnv, objEnv)
              })
            case None => Heap.Bot
          }
          State(Heap(h3.map, old))
        }
      }
      case (Exit(_), _) if st.heap.isBottom => State.Bot
      case (Exit(_), _) if st.heap.old.isBottom => State.Bot
      case (Exit(f1), AfterCall(f2, retVar, call)) =>
        val (h1, c1) = (st.heap, st.heap.old)
        val (c2, obj1) = old.fixOldify(obj, c1.mayOld, c1.mustOld)(utils)
        if (c2.isBottom) State.Bot
        else {
          val localObj = h1.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
          val returnV = localObj.getOrElse("@return")(valueU.Bot) { _.objval.value }
          val h2 = h1.update(PredefLoc.SINGLE_PURE_LOCAL, obj1)
          val h3 = h2.varStore(retVar, returnV)(utils)
          State(Heap(h3.map, c2))
        }
      case (Exit(f), _) =>
        val c1 = st.heap.old
        val (c2, obj1) = old.fixOldify(obj, c1.mayOld, c1.mustOld)(utils)
        if (c2.isBottom) State.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          State.Bot
        }
      case (ExitExc(_), _) if st.heap.isBottom => State.Bot
      case (ExitExc(_), _) if st.heap.old.isBottom => State.Bot
      case (ExitExc(_), AfterCatch(_, _)) =>
        val (h1, c1) = (st.heap, st.heap.old)
        val (c2, obj1) = old.fixOldify(obj, c1.mayOld, c1.mustOld)(utils)
        if (c2.isBottom) State.Bot
        else {
          val localObj = h1.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
          val excValue = localObj.getOrElse("@exception")(valueU.Bot) { _.objval.value }
          val excObjV = dataPropU(excValue)
          val oldExcAllValue = obj1.getOrElse("@exception_all")(valueU.Bot) { _.objval.value }
          val newExcAllObjV = dataPropU(excValue + oldExcAllValue)
          val h2 = h1.update(
            PredefLoc.SINGLE_PURE_LOCAL,
            obj1.update("@exception", PropValue(excObjV))
              .update("@exception_all", PropValue(newExcAllObjV))
          )
          State(Heap(h2.map, c2))
        }
      case (ExitExc(f), _) =>
        val c1 = st.heap.old
        val (c2, obj1) = old.fixOldify(obj, c1.mayOld, c1.mustOld)(utils)
        if (c2.isBottom) State.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          State.Bot
        }
      case _ => st
    }
  }

  def C(cp: ControlPoint, st: State): (State, State) = {
    (st.heap, st.heap.old) match {
      case (Heap.Bot, OldAddrSet.Bot) => (State.Bot, State.Bot)
      case (h: Heap, old: OldAddrSet) =>
        cp.node match {
          case Entry(_) => {
            val fun = cp.node.func
            val xArgVars = fun.argVars
            val xLocalVars = fun.localVars
            val localObj = h.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
            val locSetArg = localObj.getOrElse(fun.argumentsName)(LocSetEmpty) { _.objval.value.locset }
            val (nHeap, _) = xArgVars.foldLeft((h, 0))((res, x) => {
              val (iHeap, i) = res
              val vi = locSetArg.foldLeft(valueU.Bot)((vk, lArg) => {
                vk + iHeap.proto(lArg, utils.absString.alpha(i.toString))(utils)
              })
              (iHeap.createMutableBinding(x, vi)(utils), i + 1)
            })
            val hm = xLocalVars.foldLeft(nHeap)((jHeap, x) => {
              val undefPV = pvalueU.alpha()
              jHeap.createMutableBinding(x, valueU(undefPV))(utils)
            })
            (State(Heap(hm.map, old)), State.Bot)
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
      case _ if st.heap.isBottom => (State.Bot, excSt)
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
        val h2 = st1.heap.allocObject(vLocSet, locR)(utils)
        val h3 = h2.varStore(x, valueU(HashSet(locR)))(utils)
        val newExcSt = st.raiseException(excSet)(utils)
        val s1 = excSt + newExcSt
        (State(Heap(h3.map, st1.heap.old)), s1)
      }
      case CFGAllocArray(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)(utils)
        val np = utils.absNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, Obj.newArrayObject(np)(utils))
        val h3 = h2.varStore(x, valueU(HashSet(locR)))(utils)
        (State(Heap(h3.map, st1.heap.old)), excSt)
      }
      case CFGAllocArg(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)(utils)
        val absN = utils.absNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, Obj.newArgObject(absN)(utils))
        val h3 = h2.varStore(x, valueU(HashSet(locR)))(utils)
        (State(Heap(h3.map, st1.heap.old)), excSt)
      }
      case CFGExprStmt(_, _, x, e) => {
        val (v, excSet) = V(e, st)
        val (h1, old1) =
          if (!v.isBottom) (st.heap.varStore(x, v)(utils), st.heap.old)
          else (Heap.Bot, OldAddrSet.Bot)
        val newExcSt = st.raiseException(excSet)(utils)
        (State(Heap(h1.map, old1)), excSt + newExcSt)
      }
      case CFGDelete(_, _, x1, CFGVarRef(_, x2)) => {
        val baseLocSet = st.heap.lookupBase(x2)(utils)
        val (h1, b) =
          if (baseLocSet.isEmpty) {
            (st.heap, AT)
          } else {
            val x2Abs = utils.absString.alpha(x2.toString)
            baseLocSet.foldLeft[(Heap, AbsBool)](Heap.Bot, AB)((res, baseLoc) => {
              val (tmpHeap, tmpB) = res
              val (delHeap, delB) = st.heap.delete(baseLoc, x2Abs)(utils)
              (tmpHeap + delHeap, tmpB + delB)
            })
          }
        val bVal = valueU(b)
        val h2 = h1.varStore(x1, bVal)(utils)
        (State(Heap(h2.map, st.heap.old)), excSt)
      }
      case CFGDelete(_, _, x1, expr) => {
        val (v, excSet) = V(expr, st)
        val (h1, old1) =
          if (!v.isBottom) {
            val trueVal = valueU(AT)
            (st.heap.varStore(x1, trueVal)(utils), st.heap.old)
          } else (Heap.Bot, OldAddrSet.Bot)
        val newExcSt = st.raiseException(excSet)(utils)
        (State(Heap(h1.map, old1)), excSt + newExcSt)
      }
      case CFGDeleteProp(_, _, lhs, obj, index) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset
        val (v, excSet) = V(index, st)
        val absStrSet =
          if (v.isBottom) HashSet[AbsString]()
          else v.toPrimitiveBetter(st.heap)(utils).toStringSet(utils.absString)
        val (h1: Heap, b: AbsBool) = locSet.foldLeft[(Heap, AbsBool)](Heap.Bot, AB)((res1, l) => {
          val (tmpHeap1, tmpB1) = res1
          absStrSet.foldLeft((tmpHeap1, tmpB1))((res2, s) => {
            val (tmpHeap2, tmpB2) = res2
            val (delHeap, delB) = st.heap.delete(l, s)(utils)
            (tmpHeap2 + delHeap, tmpB2 + delB)
          })
        })
        val (h2, old2) =
          if (h1.isBottom) (Heap.Bot, OldAddrSet.Bot)
          else {
            val boolPV = pvalueU(b)
            (h1.varStore(lhs, valueU(boolPV))(utils), st.heap.old)
          }
        val newExcSt = st.raiseException(excSet)(utils)
        (State(Heap(h2.map, old2)), excSt + newExcSt)
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
        (State(Heap(heap1.map, st.heap.old)), excSt + newExcSt)
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
        (State(Heap(heap1.map, st.heap.old)), excSt + newExcSt)
      }
      case CFGFunExpr(_, block, lhs, None, f, aNew1, aNew2, None) => {
        //Recency Abstraction
        val locR1 = Loc(aNew1, Recent)
        val locR2 = Loc(aNew2, Recent)
        val st1 = st.oldify(aNew1)(utils)
        val st2 = st1.oldify(aNew2)(utils)
        val oNew = Obj.newObject(BuiltinObjectProto.loc)(utils)

        val n = utils.absNumber.alpha(f.argVars.length)
        val localObj = st2.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val scope = localObj.getOrElse("@env")(valueU.Bot) { _.objval.value }
        val h3 = st2.heap.update(locR1, Obj.newFunctionObject(f.id, scope, locR2, n)(utils))

        val fVal = valueU(HashSet(locR1))
        val fPropV = PropValue(dataPropU(fVal)(AT, AF, AT))
        val h4 = h3.update(locR2, oNew.update("constructor", fPropV, exist = true))

        val h5 = h4.varStore(lhs, fVal)(utils)
        (State(Heap(h5.map, st2.heap.old)), excSt)
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

        val localObj = st3.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val scope = localObj.getOrElse("@env")(valueU.Bot) { _.objval.value }
        val oEnv = Obj.newDeclEnvRecordObj(scope)(utils)
        val fPropV2 = PropValue(dataPropU(fVal)(AF, AB, AF))
        val h6 = h5.update(locR3, oEnv.update(name.text, fPropV2))
        val h7 = h6.varStore(lhs, fVal)(utils)
        (State(Heap(h7.map, st3.heap.old)), excSt)
      }
      case CFGAssert(_, _, expr, _) => B(expr, st, excSt, i, cfg)
      case CFGCatch(_, _, x) => {
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val excSetPropV = localObj.get("@exception_all")(utils)
        val excV = localObj.getOrElse("@exception")(valueU.Bot) { _.objval.value }
        val h1 = st.heap.createMutableBinding(x, excV)(utils)
        val newObj = h1.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils)).update("@exception", excSetPropV)
        val h2 = h1.update(PredefLoc.SINGLE_PURE_LOCAL, newObj)
        (State(Heap(h2.map, st.heap.old)), State.Bot)
      }
      case CFGReturn(_, _, Some(expr)) => {
        val (v, excSet) = V(expr, st)
        val (h1, old1) =
          if (!v.isBottom) {
            val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
            val retValPropV = PropValue(dataPropU(v))
            (st.heap.update(PredefLoc.SINGLE_PURE_LOCAL, localObj.update("@return", retValPropV)), st.heap.old)
          } else (Heap.Bot, OldAddrSet.Bot)
        val newExcSt = st.raiseException(excSet)(utils)
        (State(Heap(h1.map, old1)), excSt + newExcSt)
      }
      case CFGReturn(_, _, None) => {
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val retValPropV = PropValue(utils.absUndef.Top)(utils)
        val h1 = st.heap.update(PredefLoc.SINGLE_PURE_LOCAL, localObj.update("@return", retValPropV))
        (State(Heap(h1.map, st.heap.old)), excSt)
      }
      case CFGThrow(_, _, expr) => {
        val (v, excSet) = V(expr, st)
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val excSetV = localObj.getOrElse("@exception_all")(valueU.Bot) { _.objval.value }
        val newExcPropV = PropValue(dataPropU(v))
        val newExcSetPropV = PropValue(dataPropU(v + excSetV))
        val retValPropV = PropValue(utils.absUndef.Top)(utils)
        val newObj =
          localObj.
            update("@exception", newExcPropV).
            update("@exception_all", newExcSetPropV).
            update("@return", retValPropV)
        val h1 = st.heap.update(PredefLoc.SINGLE_PURE_LOCAL, newObj)
        val newExcSt = st.raiseException(excSet)(utils)

        (State.Bot, excSt + State(Heap(h1.map, st.heap.old)) + newExcSt)
      }
      case CFGInternalCall(ir, info, lhs, fun, arguments, loc) =>
        (fun.toString, arguments, loc) match {
          case ("<>Global<>toObject", List(expr), Some(aNew)) => {
            val (v, excSet1) = V(expr, st)
            val (v1, st1, excSet2) = helper.toObject(st, v, aNew)
            val (h2, old2) =
              if (!v1.isBottom)
                (st1.heap.varStore(lhs, v1)(utils), st1.heap.old)
              else
                (Heap.Bot, OldAddrSet.Bot)
            val (st3, excSet3) =
              if (!v.isBottom)
                (State(Heap(h2.map, old2)), excSet1 ++ excSet2)
              else
                (State.Bot, excSet1)
            val newExcSt = st.raiseException(excSet3)(utils)
            (st3, excSt + newExcSt)
          }
          case ("<>Global<>isObject", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val (h1, old1) =
              if (!v.isBottom) {
                val b1 =
                  if (!v.locset.isEmpty) AT
                  else AB
                val b2 =
                  if (!v.pvalue.isBottom) AF
                  else AB
                val boolVal = valueU(pvalueU(b1 + b2))
                (st.heap.varStore(lhs, boolVal)(utils), st.heap.old)
              } else {
                (Heap.Bot, OldAddrSet.Bot)
              }
            val newExcSt = st.raiseException(excSet)(utils)
            (State(Heap(h1.map, old1)), excSt + newExcSt)
          }
          case ("<>Global<>toNumber", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val (h1, old1) =
              if (!v.isBottom) {
                val numPV = v.toPrimitiveBetter(st.heap)(utils)
                val numPV2 = pvalueU(numPV.toAbsNumber(utils.absNumber))
                (st.heap.varStore(lhs, valueU(numPV2))(utils), st.heap.old)
              } else {
                (Heap.Bot, OldAddrSet.Bot)
              }
            val newExcSt = st.raiseException(excSet)(utils)
            (State(Heap(h1.map, old1)), excSt + newExcSt)
          }
          case ("<>Global<>toBoolean", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val (h1, old1) =
              if (!v.isBottom) {
                val boolPV = pvalueU(v.toAbsBoolean(utils.absBool))
                (st.heap.varStore(lhs, valueU(boolPV))(utils), st.heap.old)
              } else {
                (Heap.Bot, OldAddrSet.Bot)
              }
            val newExcSt = st.raiseException(excSet)(utils)
            (State(Heap(h1.map, old1)), excSt + newExcSt)
          }
          case ("<>Global<>getBase", List(CFGVarRef(_, x2)), None) => {
            val locSetBase = st.heap.lookupBase(x2)(utils)
            val h1 = st.heap.varStore(lhs, valueU(locSetBase))(utils)
            (State(Heap(h1.map, st.heap.old)), excSt)
          }
          case ("<>Global<>iteratorInit", List(expr), Some(aNew)) => (st, excSt)
          case ("<>Global<>iteratorHasNext", List(expr2, expr3), None) =>
            val boolPV = pvalueU(utils.absBool.Top)
            val h1 = st.heap.varStore(lhs, valueU(boolPV))(utils)
            (State(Heap(h1.map, st.heap.old)), excSt)
          case ("<>Global<>iteratorNext", List(expr2, expr3), None) =>
            val strPV = pvalueU(utils.absString.Top)
            val h1 = st.heap.varStore(lhs, valueU(strPV))(utils)
            (State(Heap(h1.map, st.heap.old)), excSt)
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
      case _: CFGConstruct => funVal.locset.filter(l => AT <= st1.heap.hasConstruct(l)(utils.absBool))
      case _: CFGCall => funVal.locset.filter(l => AT <= st1.heap.isCallable(l)(utils.absBool))
    }
    val (thisVal, _) = V(i.thisArg, st1)
    val thisLocSet = thisVal.getThis(st1.heap)(utils)
    val (argVal, _) = V(i.arguments, st1)

    // XXX: stop if thisArg or arguments is LocSetBot(ValueBot)
    if (thisLocSet.isEmpty || argVal.isBottom) {
      (st, excSt)
    } else {
      val oldLocalObj = st1.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
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
          val newPureLocal = Obj.newPureLocalObj(valueU(locR), thisLocSet)(utils)
          val callerCtxSet = callerCallCtx.newCallContext(st1.heap, cfg, fid, locR, thisLocSet, newPureLocal, Some(i.addr1))
          callerCtxSet.foreach {
            case (newCallCtx, newObj) => {
              val argPropV = PropValue(dataPropU(argVal)(AT, AF, AF))
              cfg.getFunc(fid) match {
                case Some(funCFG) => {
                  val scopeObj = funObj.get("@scope")(utils)
                  val newObj2 = newObj.update(funCFG.argumentsName, argPropV, exist = true)
                    .update("@scope", scopeObj)
                  val entryCP = ControlPoint(funCFG.entry, newCallCtx)
                  val exitCP = ControlPoint(funCFG.exit, newCallCtx)
                  val exitExcCP = ControlPoint(funCFG.exitExc, newCallCtx)
                  addCallEdge(cp, entryCP, OldAddrSet.Empty, newObj2)
                  addReturnEdge(exitCP, cpAfterCall, st1.heap.old, oldLocalObj)
                  addReturnEdge(exitExcCP, cpAfterCatch, st1.heap.old, oldLocalObj)
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
      (State(Heap(h3.map, st1.heap.old)), excSt + newExcSt)
    }
  }

  def V(expr: CFGExpr, st: State): (Value, Set[Exception]) = {
    expr match {
      case CFGVarRef(ir, id) => st.heap.lookup(id)(utils)
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
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val thisLocSet = localObj.getOrElse("@this")(LocSetEmpty) { _.objval.value.locset }
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
    val h1 = st.heap //TODO should be the pruned heap

    val (v, excSet) = V(expr, st)
    val newExcSt = st.raiseException(excSet)(utils)
    val h2 =
      if (utils.absBool.alpha(true) <= v.toAbsBoolean(utils.absBool)) h1
      else Heap.Bot

    (State(Heap(h2.map, st.heap.old)), excSt + newExcSt)
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
