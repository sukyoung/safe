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
import kr.ac.kaist.safe.cfg_builder._
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.util._

import scala.collection.immutable.{ HashMap, HashSet }

class Semantics(cfg: CFG, worklist: Worklist, utils: Utils, addressManager: AddressManager) {
  lazy val excLog: ExcLog = new ExcLog
  val predefLoc: PredefLoc = PredefLoc(addressManager)
  val helper: Helper = Helper(utils, addressManager, predefLoc)
  val operator: Operator = Operator(helper)

  // Interprocedural edges
  private var ipSuccMap: Map[ControlPoint, Map[ControlPoint, (Context, Obj)]] = HashMap[ControlPoint, Map[ControlPoint, (Context, Obj)]]()
  private var ipPredMap: Map[ControlPoint, Set[ControlPoint]] = HashMap[ControlPoint, Set[ControlPoint]]()
  def getInterProcSucc(cp: ControlPoint): Option[Map[ControlPoint, (Context, Obj)]] = ipSuccMap.get(cp)
  def getInterProcPred(cp: ControlPoint): Option[Set[ControlPoint]] = ipPredMap.get(cp)

  def initState: State = {
    val globalPureLocalObj = helper.newPureLocal(Value(utils.PValueBot.copyWith(utils.absNull.Top)), HashSet(predefLoc.GLOBAL_LOC)) - "@return"
    val initHeap = Heap.Bot
      .update(predefLoc.SINGLE_PURE_LOCAL_LOC, globalPureLocalObj)
      .update(predefLoc.GLOBAL_LOC, utils.ObjEmpty)
      .update(predefLoc.COLLAPSED_LOC, utils.ObjEmpty)
    State(initHeap, Context.Empty)
  }

  // Adds inter-procedural call edge from call-node cp1 to entry-node cp2.
  // Edge label ctx records callee context, which is joined if the edge existed already.
  def addCallEdge(cp1: ControlPoint, cp2: ControlPoint, ctx: Context, obj: Obj): Unit = {
    val updatedSuccMap = ipSuccMap.get(cp1) match {
      case None => HashMap(cp2 -> (ctx, obj))
      case Some(map2) =>
        map2.get(cp2) match {
          case None =>
            map2 + (cp2 -> (ctx, obj))
          case Some((oldCtx, oldObj)) =>
            map2 + (cp2 -> (oldCtx + ctx, oldObj + obj))
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
  def addReturnEdge(cp1: ControlPoint, cp2: ControlPoint, ctx: Context, obj: Obj): Unit = {
    val updatedSuccMap = ipSuccMap.get(cp1) match {
      case None => {
        worklist.add(cp1)
        HashMap(cp2 -> (ctx, obj))
      }
      case Some(map2) =>
        map2.get(cp2) match {
          case None => {
            worklist.add(cp1)
            map2 + (cp2 -> (ctx, obj))
          }
          case Some((oldCtx, oldObj)) =>
            val ctxChanged = !(ctx <= oldCtx)
            val newCtx =
              if (ctxChanged) oldCtx + ctx
              else oldCtx
            val objChanged = !(obj <= oldObj)
            val newObj =
              if (objChanged) oldObj + obj
              else oldObj
            if (ctxChanged || objChanged) {
              worklist.add(cp1)
              map2 + (cp2 -> (newCtx, newObj))
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

  def E(cp1: ControlPoint, cp2: ControlPoint, ctx: Context, obj: Obj, st: State): State = {
    (cp1.node, cp2.node) match {
      case (_, Entry(f)) => st.heap match {
        case Heap.Bot => State.Bot
        case h1: Heap => {
          val objEnv = obj("@scope") match {
            case Some(propV) => helper.newDeclEnvRecord(propV.objval.value)
            case None => helper.newDeclEnvRecord(utils.ValueBot)
          }
          val obj2 = obj - "@scope"
          val h2 = h1.remove(predefLoc.SINGLE_PURE_LOCAL_LOC).update(predefLoc.SINGLE_PURE_LOCAL_LOC, obj2)
          val h3 = obj2("@env") match {
            case Some(propV) =>
              propV.objval.value.locset.foldLeft(Heap.Bot)((hi, locEnv) => {
                hi + h2.update(locEnv, objEnv)
              })
            case None => Heap.Bot
          }
          State(h3, ctx)
        }
      }
      case (Exit(_), _) if st.heap.isBottom => State.Bot
      case (Exit(_), _) if st.context.isBottom => State.Bot
      case (Exit(f1), AfterCall(f2, retVar, call)) =>
        val (h1, c1) = (st.heap, st.context)
        val (c2, obj1) = helper.fixOldify(ctx, obj, c1.mayOld, c1.mustOld)
        if (c2.isBottom) State.Bot
        else {
          val localObj = h1.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
          val returnV = localObj.getOrElse("@return", utils.PropValueBot).objval.value
          val h2 = h1.update(predefLoc.SINGLE_PURE_LOCAL_LOC, obj1)
          val h3 = helper.varStore(h2, retVar, returnV)
          State(h3, c2)
        }
      case (Exit(f), _) =>
        val c1 = st.context
        val (c2, obj1) = helper.fixOldify(ctx, obj, c1.mayOld, c1.mustOld)
        if (c2.isBottom) State.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          State.Bot
        }
      case (ExitExc(_), _) if st.heap.isBottom => State.Bot
      case (ExitExc(_), _) if st.context.isBottom => State.Bot
      case (ExitExc(_), AfterCatch(_, _)) =>
        val (h1, c1) = (st.heap, st.context)
        val (c2, obj1) = helper.fixOldify(ctx, obj, c1.mayOld, c1.mustOld)
        if (c2.isBottom) State.Bot
        else {
          val localObj = h1.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
          val excValue = localObj.getOrElse("@exception", utils.PropValueBot).objval.value
          val excObjV = ObjectValue(excValue, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
          val oldExcAllValue = obj1.getOrElse("@exception_all", utils.PropValueBot).objval.value
          val newExcAllObjV = ObjectValue(excValue + oldExcAllValue, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
          val h2 = h1.update(
            predefLoc.SINGLE_PURE_LOCAL_LOC,
            obj1.update("@exception", PropValue(excObjV))
              .update("@exception_all", PropValue(newExcAllObjV))
          )
          State(h2, c2)
        }
      case (ExitExc(f), _) =>
        val c1 = st.context
        val (c2, obj1) = helper.fixOldify(ctx, obj, c1.mayOld, c1.mustOld)
        if (c2.isBottom) State.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          State.Bot
        }
      case _ => st
    }
  }

  def C(cp: ControlPoint, st: State): (State, State) = {
    (st.heap, st.context) match {
      case (Heap.Bot, Context.Bot) => (State.Bot, State.Bot)
      case (h: Heap, ctx: Context) =>
        cp.node match {
          case Entry(_) => {
            val fun = cp.node.func
            val xArgVars = fun.argVars
            val xLocalVars = fun.localVars
            val localObj = h.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
            val locSetArg = localObj.getOrElse(fun.argumentsName, utils.PropValueBot).objval.value.locset
            val (nHeap, _) = xArgVars.foldLeft((h, 0))((res, x) => {
              val (iHeap, i) = res
              val vi = locSetArg.foldLeft(utils.ValueBot)((vk, lArg) => {
                vk + helper.proto(iHeap, lArg, utils.absString.alpha(i.toString))
              })
              (helper.createMutableBinding(iHeap, x, vi), i + 1)
            })
            val hm = xLocalVars.foldLeft(nHeap)((hj, x) => {
              val undefPV = PValue(utils.absUndef.Top, utils.absNull.Bot, utils.absBool.Bot, utils.absNumber.Bot, utils.absString.Bot)
              helper.createMutableBinding(hj, x, Value(undefPV))
            })
            (State(hm, ctx), State.Bot)
          }
          case Exit(_) => (st, State.Bot)
          case ExitExc(_) => (st, State.Bot)
          case call: Call => I(cp, call.callInst, st, State.Bot)
          case afterCall: AfterCall => (st, State.Bot)
          case afterCatch: AfterCatch => (st, State.Bot)
          case block: CFGNormalBlock =>
            block.getInsts.foldRight((st, State.Bot))((inst, states) => {
              val (oldSt, oldExcSt) = states
              I(cp, inst, oldSt, oldExcSt)
            })
        }
    }
  }

  private def storeInstrHelp(objLocSet: Set[Loc], idxAbsStr: AbsString, storeV: Value, heap: Heap): (Heap, Set[Exception]) = {
    val absFalse = utils.absBool.False
    val absTrue = utils.absBool.True

    // non-array objects
    val locSetNArr = objLocSet.filter(l =>
      (absFalse <= helper.isArray(heap, l)) && absTrue <= helper.canPut(heap, l, idxAbsStr))
    // array objects
    val locSetArr = objLocSet.filter(l =>
      (absTrue <= helper.isArray(heap, l)) && absTrue <= helper.canPut(heap, l, idxAbsStr))

    // can not store
    val cantPutHeap =
      if (objLocSet.exists((l) => absFalse <= helper.canPut(heap, l, idxAbsStr))) heap
      else Heap.Bot

    // store for non-array object
    val nArrHeap = locSetNArr.foldLeft(Heap.Bot)((iHeap, l) => {
      iHeap + helper.propStore(heap, l, idxAbsStr, storeV)
    })

    // 15.4.5.1 [[DefineOwnProperty]] of Array
    val (arrHeap, arrExcSet) = locSetArr.foldLeft((Heap.Bot, ExceptionSetEmpty))((res2, l) => {
      // 3. s is length
      val (lengthHeap, lengthExcSet) =
        if (utils.absString.alpha("length") <= idxAbsStr) {
          val lenPropV = heap.getOrElse(l, utils.ObjBot).getOrElse("length", utils.PropValueBot)
          val nOldLen = lenPropV.objval.value.pvalue.numval
          val nNewLen = operator.toUInt32(storeV)
          val numberPV = helper.objToPrimitive(storeV.locset, "Number")
          val nValue = storeV.pvalue.toAbsNumber(utils.absNumber) + numberPV.toAbsNumber(utils.absNumber)
          val bCanPut = helper.canPut(heap, l, utils.absString.alpha("length"))

          val arrLengthHeap2 =
            if ((absTrue <= (nOldLen < nNewLen)(utils.absBool)
              || absTrue <= (nOldLen === nNewLen)(utils.absBool))
              && (absTrue <= bCanPut))
              helper.propStore(heap, l, utils.absString.alpha("length"), storeV)
            else
              Heap.Bot

          val arrLengthHeap3 =
            if (absFalse <= bCanPut) heap
            else Heap.Bot

          val arrLengthHeap4 =
            if ((absTrue <= (nNewLen < nOldLen)(utils.absBool)) && (absTrue <= bCanPut)) {
              val hi = helper.propStore(heap, l, utils.absString.alpha("length"), storeV)
              (nNewLen.gammaSingle, nOldLen.gammaSingle) match {
                case (ConSingleCon(n1), ConSingleCon(n2)) =>
                  (n1.toInt until n2.toInt).foldLeft(hi)((hj, i) => {
                    val (tmpHeap, _) = helper.delete(hj, l, utils.absString.alpha(i.toString))
                    tmpHeap
                  })
                case (ConSingleBot(), _) | (_, ConSingleBot()) => Heap.Bot
                case _ =>
                  val (tmpHeap, _) = helper.delete(hi, l, utils.absString.NumStr)
                  tmpHeap
              }
            } else {
              Heap.Bot
            }

          val arrLengthHeap1 =
            if (absTrue <= (nValue === nNewLen)(utils.absBool))
              arrLengthHeap2 + arrLengthHeap3 + arrLengthHeap4
            else
              Heap.Bot

          val lenExcSet1 =
            if (absFalse <= (nValue === nNewLen)(utils.absBool)) HashSet[Exception](RangeError)
            else ExceptionSetEmpty
          (arrLengthHeap1, lenExcSet1)
        } else {
          (Heap.Bot, ExceptionSetEmpty)
        }
      // 4. s is array index
      val arrIndexHeap =
        if (absTrue <= helper.isArrayIndex(idxAbsStr)) {
          val lenPropV = heap.getOrElse(l, utils.ObjBot).getOrElse("length", utils.PropValueBot)
          val nOldLen = lenPropV.objval.value.pvalue.numval
          val idxPV = utils.PValueBot.copyWith(idxAbsStr)
          val numPV = utils.PValueBot.copyWith(idxPV.toAbsNumber(utils.absNumber))
          val nIndex = operator.toUInt32(Value(numPV))
          val bGtEq = absTrue <= (nOldLen < nIndex)(utils.absBool) ||
            absTrue <= (nOldLen === nIndex)(utils.absBool)
          val bCanPutLen = helper.canPut(heap, l, utils.absString.alpha("length"))
          // 4.b
          val arrIndexHeap1 =
            if (bGtEq && absFalse <= bCanPutLen) heap
            else Heap.Bot
          // 4.c
          val arrIndexHeap2 =
            if (absTrue <= (nIndex < nOldLen)(utils.absBool))
              helper.propStore(heap, l, idxAbsStr, storeV)
            else Heap.Bot
          // 4.e
          val arrIndexHeap3 =
            if (bGtEq && absTrue <= bCanPutLen) {
              val hi = helper.propStore(heap, l, idxAbsStr, storeV)
              val idxVal = Value(utils.PValueBot.copyWith(nIndex))
              val absNum1PV = utils.PValueBot.copyWith(utils.absNumber.alpha(1))
              val vNewIndex = operator.bopPlus(idxVal, Value(absNum1PV))
              helper.propStore(hi, l, utils.absString.alpha("length"), vNewIndex)
            } else Heap.Bot
          arrIndexHeap1 + arrIndexHeap2 + arrIndexHeap3
        } else
          Heap.Bot
      // 5. other
      val otherHeap =
        if (idxAbsStr != utils.absString.alpha("length") && absFalse <= helper.isArrayIndex(idxAbsStr))
          helper.propStore(heap, l, idxAbsStr, storeV)
        else
          Heap.Bot
      val (tmpHeap2, tmpExcSet2) = res2
      (tmpHeap2 + lengthHeap + arrIndexHeap + otherHeap, tmpExcSet2 ++ lengthExcSet)
    })

    (cantPutHeap + nArrHeap + arrHeap, arrExcSet)
  }

  def I(cp: ControlPoint, i: CFGInst, st: State, excSt: State): (State, State) = {
    val absTrue = utils.absBool.True
    val absFalse = utils.absBool.False
    i match {
      case _ if st.heap.isBottom => (State.Bot, excSt)
      case CFGAlloc(_, _, x, e, newAddr) => {
        val objProtoSingleton = HashSet(predefLoc.OBJ_PROTO_LOC)
        // Recency Abstraction
        val locR = addressManager.addrToLoc(newAddr, Recent)
        val st1 = helper.oldify(st, newAddr)
        val (vLocSet, excSet) = e match {
          case None => (objProtoSingleton, ExceptionSetEmpty)
          case Some(proto) => {
            val (v, es) = V(proto, st1)
            if (!v.pvalue.isBottom)
              (v.locset ++ HashSet(predefLoc.OBJ_PROTO_LOC), es)
            else
              (v.locset, es)
          }
        }
        val h2 = helper.allocObject(st1.heap, vLocSet, locR)
        val h3 = helper.varStore(h2, x, Value(utils.PValueBot, HashSet(locR)))
        val newExcSt = helper.raiseException(st, excSet)
        val s1 = excSt + newExcSt
        (State(h3, st1.context), s1)
      }
      case CFGAllocArray(_, _, x, n, newAddr) => {
        val locR = addressManager.addrToLoc(newAddr, Recent)
        val st1 = helper.oldify(st, newAddr)
        val np = utils.absNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, helper.newArrayObject(np))
        val h3 = helper.varStore(h2, x, Value(utils.PValueBot, HashSet(locR)))
        (State(h3, st1.context), excSt)
      }
      case CFGAllocArg(_, _, x, n, newAddr) => {
        val locR = addressManager.addrToLoc(newAddr, Recent)
        val st1 = helper.oldify(st, newAddr)
        val absN = utils.absNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, helper.newArgObject(absN))
        val h3 = helper.varStore(h2, x, Value(utils.PValueBot, HashSet(locR)))
        (State(h3, st1.context), excSt)
      }
      case CFGExprStmt(_, _, x, e) => {
        val (v, excSet) = V(e, st)
        val (h1, ctx1) =
          if (!v.isBottom) (helper.varStore(st.heap, x, v), st.context)
          else (Heap.Bot, Context.Bot)
        val newExcSt = helper.raiseException(st, excSet)
        (State(h1, ctx1), excSt + newExcSt)
      }
      case CFGDelete(_, _, x1, CFGVarRef(_, x2)) => {
        val baseLocSet = helper.lookupBase(st.heap, x2)
        val (h1, b) =
          if (baseLocSet.isEmpty) {
            (st.heap, utils.absBool.True)
          } else {
            val x2Abs = utils.absString.alpha(x2.toString)
            baseLocSet.foldLeft[(Heap, AbsBool)](Heap.Bot, utils.absBool.Bot)((res, baseLoc) => {
              val (tmpHeap, tmpB) = res
              val (delHeap, delB) = helper.delete(st.heap, baseLoc, x2Abs)
              (tmpHeap + delHeap, tmpB + delB)
            })
          }
        val bVal = Value(utils.PValueBot.copyWith(b))
        val h2 = helper.varStore(h1, x1, bVal)
        (State(h2, st.context), excSt)
      }
      case CFGDelete(_, _, x1, expr) => {
        val (v, excSet) = V(expr, st)
        val (h1, ctx1) =
          if (!v.isBottom) {
            val trueVal = Value(utils.PValueBot.copyWith(utils.absBool.True))
            (helper.varStore(st.heap, x1, trueVal), st.context)
          } else (Heap.Bot, Context.Bot)
        val newExcSt = helper.raiseException(st, excSet)
        (State(h1, ctx1), excSt + newExcSt)
      }
      case CFGDeleteProp(_, _, lhs, obj, index) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset
        val (v, excSet) = V(index, st)
        val absStrSet =
          if (v.isBottom) HashSet[AbsString]()
          else helper.toStringSet(helper.toPrimitiveBetter(st.heap, v))
        val (h1: Heap, b: AbsBool) = locSet.foldLeft[(Heap, AbsBool)](Heap.Bot, utils.absBool.Bot)((res1, l) => {
          val (tmpHeap1, tmpB1) = res1
          absStrSet.foldLeft((tmpHeap1, tmpB1))((res2, s) => {
            val (tmpHeap2, tmpB2) = res2
            val (delHeap, delB) = helper.delete(st.heap, l, s)
            (tmpHeap2 + delHeap, tmpB2 + delB)
          })
        })
        val (h2, ctx2) =
          if (h1.isBottom) (Heap.Bot, Context.Bot)
          else {
            val boolPV = utils.PValueBot.copyWith(b)
            (helper.varStore(h1, lhs, Value(boolPV)), st.context)
          }
        val newExcSt = helper.raiseException(st, excSet)
        (State(h2, ctx2), excSt + newExcSt)
      }
      case CFGStore(_, block, obj, index, rhs) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset

        val (vIdx, excSetIdx) = V(index, st)
        val (vRhs, esRhs) = V(rhs, st)

        val (heap1, excSet1) =
          (vIdx, vRhs) match {
            case (v, _) if v.isBottom => (Heap.Bot, excSetIdx)
            case (_, v) if v.isBottom => (Heap.Bot, excSetIdx ++ esRhs)
            case _ =>
              // iterate over set of strings for index
              val absStrSet = helper.toStringSet(helper.toPrimitiveBetter(st.heap, vIdx))
              absStrSet.foldLeft((Heap.Bot, excSetIdx ++ esRhs))((res1, absStr) => {
                val (tmpHeap1, tmpExcSet1) = res1
                val (tmpHeap2, tmpExcSet2) = storeInstrHelp(locSet, absStr, vRhs, st.heap)
                (tmpHeap1 + tmpHeap2, tmpExcSet1 ++ tmpExcSet2)
              })
          }

        val newExcSt = helper.raiseException(st, excSet1)
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
              val (tmpHeap2, tmpExcSet2) = storeInstrHelp(locSet, absStr, vRhs, st.heap)
              (tmpHeap2, tmpExcSet2 ++ esRhs)
          }

        val newExcSt = helper.raiseException(st, excSet1)
        (State(heap1, st.context), excSt + newExcSt)
      }
      case CFGFunExpr(_, block, lhs, None, f, aNew1, aNew2, None) => {
        //Recency Abstraction
        val locR1 = addressManager.addrToLoc(aNew1, Recent)
        val locR2 = addressManager.addrToLoc(aNew2, Recent)
        val st1 = helper.oldify(st, aNew1)
        val st2 = helper.oldify(st1, aNew2)
        val oNew = helper.newObject(predefLoc.OBJ_PROTO_LOC)

        val n = utils.absNumber.alpha(block.func.argVars.length)
        val localObj = st2.heap.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
        val scope = localObj.getOrElse("@env", utils.PropValueBot).objval.value
        val h3 = st2.heap.update(locR1, helper.newFunctionObject(f.id, scope, locR2, n))

        val fVal = Value(utils.PValueBot, HashSet(locR1))
        val fPropV = PropValue(ObjectValue(fVal, utils.absBool.True, utils.absBool.False, utils.absBool.True))
        val h4 = h3.update(locR2, oNew.update("constructor", fPropV, exist = true))

        val h5 = helper.varStore(h4, lhs, fVal)
        (State(h5, st2.context), excSt)
      }
      case CFGFunExpr(_, block, lhs, Some(name), f, aNew1, aNew2, Some(aNew3)) => {
        // Recency Abstraction
        val locR1 = addressManager.addrToLoc(aNew1, Recent)
        val locR2 = addressManager.addrToLoc(aNew2, Recent)
        val locR3 = addressManager.addrToLoc(aNew3, Recent)
        val st1 = helper.oldify(st, aNew1)
        val st2 = helper.oldify(st1, aNew2)
        val st3 = helper.oldify(st2, aNew3)

        val oNew = helper.newObject(predefLoc.OBJ_PROTO_LOC)
        val n = utils.absNumber.alpha(block.func.argVars.length)
        val fObjValue = Value(utils.PValueBot, HashSet(locR3))
        val h4 = st3.heap.update(locR1, helper.newFunctionObject(f.id, fObjValue, locR2, n))

        val fVal = Value(utils.PValueBot, HashSet(locR1))
        val fPropV = PropValue(ObjectValue(fVal, utils.absBool.True, utils.absBool.False, utils.absBool.True))
        val h5 = h4.update(locR2, oNew.update("constructor", fPropV, exist = true))

        val localObj = st3.heap.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
        val scope = localObj.getOrElse("@env", utils.PropValueBot).objval.value
        val oEnv = helper.newDeclEnvRecord(scope)
        val fPropV2 = PropValue(ObjectValue(fVal, utils.absBool.False, utils.absBool.Bot, utils.absBool.False))
        val h6 = h5.update(locR3, oEnv.update(name.text, fPropV2))
        val h7 = helper.varStore(h6, lhs, fVal)
        (State(h7, st3.context), excSt)
      }
      case CFGConstruct(ir, block, consExpr, thisArg, arguments, aNew, bNew) => {
        // cons, thisArg and arguments must not be bottom
        val locR = addressManager.addrToLoc(aNew, Recent)
        val st1 = helper.oldify(st, aNew)
        val (consVal, consExcSet) = V(consExpr, st1)
        val consLocSet = consVal.locset.filter(l => absTrue <= helper.hasConstruct(st1.heap, l))
        val (thisVal, _) = V(thisArg, st1)
        val thisLocSet = helper.getThis(st1.heap, thisVal)
        val (argVal, _) = V(arguments, st1)

        // XXX: stop if thisArg or arguments is LocSetBot(ValueBot)
        if (thisLocSet.isEmpty || argVal.isBottom) {
          (st, excSt)
        } else {
          val oldLocalObj = st1.heap.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
          val callerCallCtx = cp.callContext
          val nCall =
            cp.node match {
              case callBlock: Call => callBlock
              case _ =>
                excLog.signal(NoAfterCallAfterCatchError(ir))
                block
            }
          val cpAfterCall = ControlPoint(nCall.afterCall, callerCallCtx)
          val cpAfterCatch = ControlPoint(nCall.afterCatch, callerCallCtx)

          // Draw call/return edges
          consLocSet.foreach((consLoc) => {
            val consObj = st1.heap.getOrElse(consLoc, utils.ObjBot)
            val fidSet = consObj.getOrElse("@construct", utils.PropValueBot).funid
            fidSet.foreach((fid) => {
              val newPureLocal = helper.newPureLocal(Value(utils.PValueBot, HashSet(locR)), thisLocSet)
              val calleCtxSet = callerCallCtx.newCallContext(st1.heap, cfg, fid, locR, thisLocSet, newPureLocal, Some(aNew))
              calleCtxSet.foreach {
                case (newCallCtx, newObj) => {
                  val argPropV = PropValue(ObjectValue(argVal, absTrue, absFalse, absFalse))
                  cfg.funMap.get(fid) match {
                    case Some(funCFG) => {
                      val scopeObj = consObj.getOrElse("@scope", utils.PropValueBot)
                      val newObj2 =
                        newObj.update(funCFG.argumentsName, argPropV, exist = true)
                          .update("@scope", scopeObj)
                      val entryCP = ControlPoint(funCFG.entry, newCallCtx)
                      val exitCP = ControlPoint(funCFG.exit, newCallCtx)
                      val exitExcCP = ControlPoint(funCFG.exitExc, newCallCtx)
                      addCallEdge(cp, entryCP, Context.Empty, newObj2)
                      addReturnEdge(exitCP, cpAfterCall, st1.context, oldLocalObj)
                      addReturnEdge(exitExcCP, cpAfterCatch, st1.context, oldLocalObj)
                    }
                    case None => excLog.signal(UndefinedFunctionCallError(ir))
                  }
                }
              }
            })
          })

          val h2 = argVal.locset.foldLeft(Heap.Bot)((tmpHeap, l) => {
            val consPropV = PropValue(ObjectValue(Value(utils.PValueBot, consLocSet), absTrue, absFalse, absTrue))
            val argObj = st1.heap.getOrElse(l, utils.ObjBot)
            tmpHeap + st1.heap.update(l, argObj.update("callee", consPropV))
          })

          // exception handling
          val typeExcSet1 =
            if (consVal.locset.exists(l => absFalse <= helper.hasConstruct(st1.heap, l))) Set(TypeError)
            else ExceptionSetEmpty
          val typeExcSet2 =
            if (!consVal.pvalue.isBottom) Set(TypeError)
            else ExceptionSetEmpty

          val totalExcSet = consExcSet ++ typeExcSet1 ++ typeExcSet2
          val newExcSt = helper.raiseException(st1, totalExcSet)

          val h3 =
            if (!consLocSet.isEmpty) h2
            else Heap.Bot
          (State(h3, st1.context), excSt + newExcSt)
        }
      }
      case CFGCall(ir, block, funExpr, thisArg, arguments, aNew, bNew) => {
        // cons, thisArg and arguments must not be bottom
        val locR = addressManager.addrToLoc(aNew, Recent)
        val st1 = helper.oldify(st, aNew)
        val (funVal, funExcSet) = V(funExpr, st1)
        val funLocSet = funVal.locset.filter(l => utils.absBool.True <= helper.isCallable(st1.heap, l))
        val (thisV, thisExcSet) = V(thisArg, st1)
        val thisLocSet = helper.getThis(st1.heap, thisV)
        val (argVal, _) = V(arguments, st1)
        // XXX: stop if thisArg or arguments is LocSetBot(ValueBot)
        if (thisLocSet.isEmpty || argVal.isBottom) {
          (st, excSt)
        } else {
          val oldLocalObj = st1.heap.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
          val callerCallCtx = cp.callContext
          val callBlock =
            cp.node match {
              case callBlock: Call => callBlock
              case _ =>
                excLog.signal(NoAfterCallAfterCatchError(ir))
                block
            }
          val afterCallCP = ControlPoint(callBlock.afterCall, callerCallCtx)
          val afterCatchCP = ControlPoint(callBlock.afterCatch, callerCallCtx)

          funLocSet.foreach((funLoc) => {
            val funObj = st1.heap.getOrElse(funLoc, utils.ObjBot)
            val fidSet = funObj.getOrElse("@function", utils.PropValueBot).funid
            fidSet.foreach((fid) => {
              val newPureLocal = helper.newPureLocal(Value(utils.PValueBot, HashSet(locR)), thisLocSet)
              val callCtxSet = callerCallCtx.newCallContext(st.heap, cfg, fid, locR, thisLocSet, newPureLocal, Some(aNew))
              callCtxSet.foreach {
                case (newCallCtx, newObj) => {
                  val value = PropValue(ObjectValue(argVal, absTrue, absFalse, absFalse))
                  cfg.funMap.get(fid) match {
                    case Some(funCFG) => {
                      val oNew2 =
                        newObj.update(funCFG.argumentsName, value, exist = true)
                          .update("@scope", funObj.getOrElse("@scope", utils.PropValueBot))
                      val entryCP = ControlPoint(funCFG.entry, newCallCtx)
                      val exitCP = ControlPoint(funCFG.exit, newCallCtx)
                      val exitExcCP = ControlPoint(funCFG.exitExc, newCallCtx)
                      addCallEdge(cp, entryCP, Context.Empty, oNew2)
                      addReturnEdge(exitCP, afterCallCP, st1.context, oldLocalObj)
                      addReturnEdge(exitExcCP, afterCatchCP, st1.context, oldLocalObj)
                    }
                    case None => excLog.signal(UndefinedFunctionCallError(ir))
                  }
                }
              }
            })
          })

          val h2 = argVal.locset.foldLeft(Heap.Bot)((tmpHeap, argLoc) => {
            val calleePropV = PropValue(ObjectValue(Value(utils.PValueBot, funLocSet), absTrue, absFalse, absTrue))
            val argObj = st1.heap.getOrElse(argLoc, utils.ObjBot)
            tmpHeap + st1.heap.update(argLoc, argObj.update("callee", calleePropV))
          })

          // exception handling
          val typeExcSet1 =
            if (funVal.locset.exists(l => absFalse <= helper.isCallable(st1.heap, l))) Set(TypeError)
            else ExceptionSetEmpty
          val typeExcSet2 =
            if (!funVal.pvalue.isBottom) Set(TypeError)
            else ExceptionSetEmpty

          val totalExcSet = funExcSet ++ typeExcSet1 ++ typeExcSet2
          val newExcSt = helper.raiseException(st1, totalExcSet)

          val h3 =
            if (!funLocSet.isEmpty) h2
            else Heap.Bot
          (State(h3, st1.context), excSt + newExcSt)
        }
      }
      case CFGAssert(_, _, expr, _) => B(expr, st, excSt, i, cfg, cp)
      case CFGCatch(_, _, x) => {
        val localObj = st.heap.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
        val excSetPropV = localObj.getOrElse("@exception_all", utils.PropValueBot)
        val excV = localObj.getOrElse("@exception", utils.PropValueBot).objval.value
        val h1 = helper.createMutableBinding(st.heap, x, excV)
        val newObj = h1.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot).update("@exception", excSetPropV)
        val h2 = h1.update(predefLoc.SINGLE_PURE_LOCAL_LOC, newObj)
        (State(h2, st.context), State(Heap.Bot, Context.Bot))
      }
      case CFGReturn(_, _, Some(expr)) => {
        val (v, excSet) = V(expr, st)
        val (h1, ctx1) =
          if (!v.isBottom) {
            val localObj = st.heap.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
            val retValPropV = PropValue(utils.ObjectValueBot.copyWith(v))
            (st.heap.update(predefLoc.SINGLE_PURE_LOCAL_LOC, localObj.update("@return", retValPropV)), st.context)
          } else (Heap.Bot, Context.Bot)
        val newExcSt = helper.raiseException(st, excSet)
        (State(h1, ctx1), excSt + newExcSt)
      }
      case CFGReturn(_, _, None) => {
        val localObj = st.heap.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
        val undefV = Value(utils.PValueBot.copyWith(utils.absUndef.Top))
        val retValPropV = PropValue(utils.ObjectValueBot.copyWith(undefV))
        val h1 = st.heap.update(predefLoc.SINGLE_PURE_LOCAL_LOC, localObj.update("@return", retValPropV))
        (State(h1, st.context), excSt)
      }
      case CFGThrow(_, _, expr) => {
        val (v, excSet) = V(expr, st)
        val localObj = st.heap.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
        val excSetV = localObj.getOrElse("@exception_all", utils.PropValueBot).objval.value
        val newExcPropV = PropValue(utils.ObjectValueBot.copyWith(v))
        val newExcSetPropV = PropValue(utils.ObjectValueBot.copyWith(v + excSetV))
        val undefV = Value(utils.PValueBot.copyWith(utils.absUndef.Top))
        val retValPropV = PropValue(utils.ObjectValueBot.copyWith(undefV))
        val newObj =
          localObj.
            update("@exception", newExcPropV).
            update("@exception_all", newExcSetPropV).
            update("@return", retValPropV)
        val h1 = st.heap.update(predefLoc.SINGLE_PURE_LOCAL_LOC, newObj)
        val newExcSt = helper.raiseException(st, excSet)

        (State.Bot, excSt + State(h1, st.context) + newExcSt)
      }
      case CFGInternalCall(ir, info, lhs, fun, arguments, loc) =>
        (fun.toString, arguments, loc) match {
          case ("<>Global<>toObject", List(expr), Some(aNew)) => {
            val (v, excSet1) = V(expr, st)
            val (v1, st1, excSet2) = helper.toObject(st, v, aNew)
            val (h2, ctx2) =
              if (!v1.isBottom)
                (helper.varStore(st1.heap, lhs, v1), st1.context)
              else
                (Heap.Bot, Context.Bot)
            val (st3, excSet3) =
              if (!v.isBottom)
                (State(h2, ctx2), excSet1 ++ excSet2)
              else
                (State.Bot, excSet1)
            val newExcSt = helper.raiseException(st, excSet3)
            (st3, excSt + newExcSt)
          }
          case ("<>Global<>isObject", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val (h1, ctx1) =
              if (!v.isBottom) {
                val b1 =
                  if (!v.locset.isEmpty) utils.absBool.True
                  else utils.absBool.Bot
                val b2 =
                  if (!v.pvalue.isBottom) utils.absBool.False
                  else utils.absBool.Bot
                val boolVal = Value(utils.PValueBot.copyWith(b1 + b2))
                (helper.varStore(st.heap, lhs, boolVal), st.context)
              } else {
                (Heap.Bot, Context.Bot)
              }
            val newExcSt = helper.raiseException(st, excSet)
            (State(h1, ctx1), excSt + newExcSt)
          }
          case ("<>Global<>toNumber", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val (h1, ctx1) =
              if (!v.isBottom) {
                val numPV = helper.toPrimitiveBetter(st.heap, v)
                val numPV2 = utils.PValueBot.copyWith(numPV.toAbsNumber(utils.absNumber))
                (helper.varStore(st.heap, lhs, Value(numPV2)), st.context)
              } else {
                (Heap.Bot, Context.Bot)
              }
            val newExcSt = helper.raiseException(st, excSet)
            (State(h1, ctx1), excSt + newExcSt)
          }
          case ("<>Global<>toBoolean", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val (h1, ctx1) =
              if (!v.isBottom) {
                val boolPV = utils.PValueBot.copyWith(v.toAbsBoolean(utils.absBool))
                (helper.varStore(st.heap, lhs, Value(boolPV)), st.context)
              } else {
                (Heap.Bot, Context.Bot)
              }
            val newExcSt = helper.raiseException(st, excSet)
            (State(h1, ctx1), excSt + newExcSt)
          }
          case ("<>Global<>getBase", List(CFGVarRef(_, x2)), None) => {
            val locSetBase = helper.lookupBase(st.heap, x2)
            val h1 = helper.varStore(st.heap, lhs, Value(utils.PValueBot, locSetBase))
            (State(h1, st.context), excSt)
          }
          case ("<>Global<>iteratorInit", List(expr), Some(aNew)) => (st, excSt)
          case ("<>Global<>iteratorHasNext", List(expr2, expr3), None) =>
            val boolPV = utils.PValueBot.copyWith(utils.absBool.Top)
            val h1 = helper.varStore(st.heap, lhs, Value(boolPV))
            (State(h1, st.context), excSt)
          case ("<>Global<>iteratorNext", List(expr2, expr3), None) =>
            val strPV = utils.PValueBot.copyWith(utils.absString.Top)
            val h1 = helper.varStore(st.heap, lhs, Value(strPV))
            (State(h1, st.context), excSt)
          case _ =>
            excLog.signal(SemanticsNotYetImplementedError(ir))
            (State.Bot, State.Bot)
        }
      case CFGNoOp(_, _, _) => (st, excSt)
    }
  }

  def V(expr: CFGExpr, st: State): (Value, Set[Exception]) = {
    expr match {
      case CFGVarRef(ir, id) => helper.lookup(st.heap, id)
      case CFGLoad(ir, obj, index) => {
        val (objV, _) = V(obj, st)
        val objLocSet = objV.locset
        val (idxV, idxExcSet) = V(index, st)
        val absStrSet =
          if (!idxV.isBottom) helper.toStringSet(helper.toPrimitiveBetter(st.heap, idxV))
          else HashSet[AbsString]()
        val v1 = objLocSet.foldLeft(utils.ValueBot)((tmpVal1, loc) => {
          absStrSet.foldLeft(tmpVal1)((tmpVal2, absStr) => {
            tmpVal2 + helper.proto(st.heap, loc, absStr)
          })
        })
        (v1, idxExcSet)
      }
      case CFGThis(ir) =>
        val localObj = st.heap.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
        val thisLocSet = localObj.getOrElse("@this", utils.PropValueBot).objval.value.locset
        (Value(utils.PValueBot, thisLocSet), ExceptionSetEmpty)
      case CFGBin(ir, expr1, op, expr2) => {
        val (v1, excSet1) = V(expr1, st)
        val (v2, excSet2) = V(expr2, st)
        (v1, v2) match {
          case _ if v1.isBottom => (utils.ValueBot, excSet1)
          case _ if v2.isBottom => (utils.ValueBot, excSet1 ++ excSet2)
          case _ =>
            op.name match {
              case "|" => (operator.bopBitOr(v1, v2), excSet1 ++ excSet2)
              case "&" => (operator.bopBitAnd(v1, v2), excSet1 ++ excSet2)
              case "^" => (operator.bopBitXor(v1, v2), excSet1 ++ excSet2)
              case "<<" => (operator.bopLShift(v1, v2), excSet1 ++ excSet2)
              case ">>" => (operator.bopRShift(v1, v2), excSet1 ++ excSet2)
              case ">>>" => (operator.bopURShift(v1, v2), excSet1 ++ excSet2)
              case "+" => (operator.bopPlus(v1, v2), excSet1 ++ excSet2)
              case "-" => (operator.bopMinus(v1, v2), excSet1 ++ excSet2)
              case "*" => (operator.bopMul(v1, v2), excSet1 ++ excSet2)
              case "/" => (operator.bopDiv(v1, v2), excSet1 ++ excSet2)
              case "%" => (operator.bopMod(v1, v2), excSet1 ++ excSet2)
              case "==" => (operator.bopEqBetter(st.heap, v1, v2), excSet1 ++ excSet2)
              case "!=" => (operator.bopNeq(v1, v2), excSet1 ++ excSet2)
              case "===" => (operator.bopSEq(v1, v2), excSet1 ++ excSet2)
              case "!==" => (operator.bopSNeq(v1, v2), excSet1 ++ excSet2)
              case "<" => (operator.bopLess(v1, v2), excSet1 ++ excSet2)
              case ">" => (operator.bopGreater(v1, v2), excSet1 ++ excSet2)
              case "<=" => (operator.bopLessEq(v1, v2), excSet1 ++ excSet2)
              case ">=" => (operator.bopGreaterEq(v1, v2), excSet1 ++ excSet2)
              case "instanceof" =>
                val locSet1 = v1.locset
                val locSet2 = v2.locset
                val locSet3 = locSet2.filter((l) => utils.absBool.True <= helper.hasInstance(st.heap, l))
                val protoVal = locSet3.foldLeft(utils.ValueBot)((v, l) => {
                  v + helper.proto(st.heap, l, utils.absString.alpha("prototype"))
                })
                val locSet4 = protoVal.locset
                val locSet5 = locSet2.filter((l) => utils.absBool.False <= helper.hasInstance(st.heap, l))
                val b1 = locSet1.foldLeft[Value](utils.ValueBot)((tmpVal1, loc1) => {
                  locSet4.foldLeft[Value](tmpVal1)((tmpVal2, loc2) =>
                    tmpVal2 + helper.inherit(st.heap, loc1, loc2, operator.bopSEq))
                })
                val pv2 =
                  if (!v1.pvalue.isBottom && !locSet4.isEmpty) utils.PValueBot.copyWith(utils.absBool.False)
                  else utils.PValueBot.copyWith(utils.absBool.Bot)
                val b2 = Value(pv2)
                val excSet3 =
                  if (!v2.pvalue.isBottom || !locSet5.isEmpty || !protoVal.pvalue.isBottom)
                    HashSet(TypeError)
                  else
                    ExceptionSetEmpty
                val b = b1 + b2
                val excSet = excSet1 ++ excSet2 ++ excSet3
                (b, excSet)
              case "in" => {
                val str = helper.toPrimitiveBetter(st.heap, v1).toAbsString(utils.absString)
                val absB = v2.locset.foldLeft(utils.absBool.Bot)((tmpAbsB, loc) => {
                  tmpAbsB + helper.hasProperty(st.heap, loc, str)
                })
                val b = Value(utils.PValueBot.copyWith(absB))
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
          case "void" => (operator.uVoid(v), excSet)
          case "+" => (operator.uopPlus(v), excSet)
          case "-" => (operator.uopMinusBetter(st.heap, v), excSet)
          case "~" => (operator.uopBitNeg(v), excSet)
          case "!" => (operator.uopNeg(v), excSet)
          case "typeof" =>
            expr match {
              case CFGVarRef(_, x) =>
                val absStr1 = helper.typeTag(st.heap, v)
                val absStr2 =
                  if (excSet.contains(ReferenceError)) utils.absString.alpha("undefined")
                  else utils.absString.Bot
                val absStrPV = utils.PValueBot.copyWith(absStr1 + absStr2)
                (Value(absStrPV), ExceptionSetEmpty)
              case _ =>
                val absStrPV = utils.PValueBot.copyWith(helper.typeTag(st.heap, v))
                (Value(absStrPV), excSet)
            }
        }
      }
      case CFGVal(ejsVal) =>
        val pv = ejsVal match {
          case EJSNumber(_, num) => utils.PValueBot.copyWith(utils.absNumber.alpha(num))
          case EJSString(str) => utils.PValueBot.copyWith(utils.absString.alpha(str))
          case EJSBool(bool) => utils.PValueBot.copyWith(utils.absBool.alpha(bool))
          case EJSNull => utils.PValueBot.copyWith(utils.absNull.Top)
          case EJSUndef => utils.PValueBot.copyWith(utils.absUndef.Top)
        }
        (Value(pv), ExceptionSetEmpty)
    }
  }

  def B(expr: CFGExpr, s: State, se: State, inst: CFGInst, cfg: CFG, cp: ControlPoint): (State, State) = {
    (State.Bot, State.Bot)
  }
}
