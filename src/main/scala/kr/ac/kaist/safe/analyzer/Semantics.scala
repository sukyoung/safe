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
  val operator: Operator = Operator(helper)

  // Interprocedural edges
  private var ipSuccMap: Map[ControlPoint, Map[ControlPoint, (Context, Obj)]] = HashMap[ControlPoint, Map[ControlPoint, (Context, Obj)]]()
  private var ipPredMap: Map[ControlPoint, Set[ControlPoint]] = HashMap[ControlPoint, Set[ControlPoint]]()
  def getAllIPSucc: Map[ControlPoint, Map[ControlPoint, (Context, Obj)]] = ipSuccMap
  def getAllIPPred: Map[ControlPoint, Set[ControlPoint]] = ipPredMap
  def getInterProcSucc(cp: ControlPoint): Option[Map[ControlPoint, (Context, Obj)]] = ipSuccMap.get(cp)
  def getInterProcPred(cp: ControlPoint): Option[Set[ControlPoint]] = ipPredMap.get(cp)

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
        case State.Bot => State.Bot
        case h1: Heap => {
          val objEnv = obj("@scope") match {
            case Some(propV) => Obj.newDeclEnvRecordObj(propV.objval.value)(utils)
            case None => Obj.newDeclEnvRecordObj(Value.Bot(utils))(utils)
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
          val localObj = h1.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
          val returnV = localObj.getOrElse("@return", PropValue.Bot(utils)).objval.value
          val h2 = h1.update(PredefLoc.SINGLE_PURE_LOCAL, obj1)
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
          val localObj = h1.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
          val excValue = localObj.getOrElse("@exception", PropValue.Bot(utils)).objval.value
          val excObjV = ObjectValue(excValue, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
          val oldExcAllValue = obj1.getOrElse("@exception_all", PropValue.Bot(utils)).objval.value
          val newExcAllObjV = ObjectValue(excValue + oldExcAllValue, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
          val h2 = h1.update(
            PredefLoc.SINGLE_PURE_LOCAL,
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
            val localObj = h.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
            val locSetArg = localObj.getOrElse(fun.argumentsName, PropValue.Bot(utils)).objval.value.locset
            val (nHeap, _) = xArgVars.foldLeft((h, 0))((res, x) => {
              val (iHeap, i) = res
              val vi = locSetArg.foldLeft(Value.Bot(utils))((vk, lArg) => {
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
          case block: NormalBlock =>
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
          val lenPropV = heap.getOrElse(l, utils.ObjBot).getOrElse("length", PropValue.Bot(utils))
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
          val lenPropV = heap.getOrElse(l, utils.ObjBot).getOrElse("length", PropValue.Bot(utils))
          val nOldLen = lenPropV.objval.value.pvalue.numval
          val idxPV = PValue(idxAbsStr)(utils)
          val numPV = PValue(idxPV.toAbsNumber(utils.absNumber))(utils)
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
              val idxVal = Value(PValue(nIndex)(utils))
              val absNum1PV = PValue(utils.absNumber.alpha(1))(utils)
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
        val objProtoSingleton = HashSet(BuiltinObject.PROTO_LOC)
        // Recency Abstraction
        val locR = Loc(newAddr, Recent)
        val st1 = helper.oldify(st, newAddr)
        val (vLocSet, excSet) = e match {
          case None => (objProtoSingleton, ExceptionSetEmpty)
          case Some(proto) => {
            val (v, es) = V(proto, st1)
            if (!v.pvalue.isBottom)
              (v.locset ++ HashSet(BuiltinObject.PROTO_LOC), es)
            else
              (v.locset, es)
          }
        }
        val h2 = helper.allocObject(st1.heap, vLocSet, locR)
        val h3 = helper.varStore(h2, x, Value(PValue.Bot(utils), HashSet(locR)))
        val newExcSt = helper.raiseException(st, excSet)
        val s1 = excSt + newExcSt
        (State(h3, st1.context), s1)
      }
      case CFGAllocArray(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = helper.oldify(st, newAddr)
        val np = utils.absNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, Obj.newArrayObject(np)(utils))
        val h3 = helper.varStore(h2, x, Value(PValue.Bot(utils), HashSet(locR)))
        (State(h3, st1.context), excSt)
      }
      case CFGAllocArg(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = helper.oldify(st, newAddr)
        val absN = utils.absNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, Obj.newArgObject(absN)(utils))
        val h3 = helper.varStore(h2, x, Value(PValue.Bot(utils), HashSet(locR)))
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
        val bVal = Value(PValue(b)(utils))
        val h2 = helper.varStore(h1, x1, bVal)
        (State(h2, st.context), excSt)
      }
      case CFGDelete(_, _, x1, expr) => {
        val (v, excSet) = V(expr, st)
        val (h1, ctx1) =
          if (!v.isBottom) {
            val trueVal = Value(PValue(utils.absBool.True)(utils))
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
            val boolPV = PValue(b)(utils)
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
        val locR1 = Loc(aNew1, Recent)
        val locR2 = Loc(aNew2, Recent)
        val st1 = helper.oldify(st, aNew1)
        val st2 = helper.oldify(st1, aNew2)
        val oNew = Obj.newObject(BuiltinObject.PROTO_LOC)(utils)

        val n = utils.absNumber.alpha(f.argVars.length)
        val localObj = st2.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
        val scope = localObj.getOrElse("@env", PropValue.Bot(utils)).objval.value
        val h3 = st2.heap.update(locR1, Obj.newFunctionObject(f.id, scope, locR2, n)(utils))

        val fVal = Value(PValue.Bot(utils), HashSet(locR1))
        val fPropV = PropValue(ObjectValue(fVal, utils.absBool.True, utils.absBool.False, utils.absBool.True))
        val h4 = h3.update(locR2, oNew.update("constructor", fPropV, exist = true))

        val h5 = helper.varStore(h4, lhs, fVal)
        (State(h5, st2.context), excSt)
      }
      case CFGFunExpr(_, block, lhs, Some(name), f, aNew1, aNew2, Some(aNew3)) => {
        // Recency Abstraction
        val locR1 = Loc(aNew1, Recent)
        val locR2 = Loc(aNew2, Recent)
        val locR3 = Loc(aNew3, Recent)
        val st1 = helper.oldify(st, aNew1)
        val st2 = helper.oldify(st1, aNew2)
        val st3 = helper.oldify(st2, aNew3)

        val oNew = Obj.newObject(BuiltinObject.PROTO_LOC)(utils)
        val n = utils.absNumber.alpha(f.argVars.length)
        val fObjValue = Value(PValue.Bot(utils), HashSet(locR3))
        val h4 = st3.heap.update(locR1, Obj.newFunctionObject(f.id, fObjValue, locR2, n)(utils))

        val fVal = Value(PValue.Bot(utils), HashSet(locR1))
        val fPropV = PropValue(ObjectValue(fVal, utils.absBool.True, utils.absBool.False, utils.absBool.True))
        val h5 = h4.update(locR2, oNew.update("constructor", fPropV, exist = true))

        val localObj = st3.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
        val scope = localObj.getOrElse("@env", PropValue.Bot(utils)).objval.value
        val oEnv = Obj.newDeclEnvRecordObj(scope)(utils)
        val fPropV2 = PropValue(ObjectValue(fVal, utils.absBool.False, utils.absBool.Bot, utils.absBool.False))
        val h6 = h5.update(locR3, oEnv.update(name.text, fPropV2))
        val h7 = helper.varStore(h6, lhs, fVal)
        (State(h7, st3.context), excSt)
      }
      case CFGConstruct(ir, block, consExpr, thisArg, arguments, aNew, bNew) => {
        // cons, thisArg and arguments must not be bottom
        val locR = Loc(aNew, Recent)
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
          val oldLocalObj = st1.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
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
            val fidSet = consObj.getOrElse("@construct", PropValue.Bot(utils)).funid
            fidSet.foreach((fid) => {
              val newPureLocal = Obj.newPureLocalObj(Value(PValue.Bot(utils), HashSet(locR)), thisLocSet)(utils)
              val callerCtxSet = callerCallCtx.newCallContext(st1.heap, cfg, fid, locR, thisLocSet, newPureLocal, Some(aNew))
              callerCtxSet.foreach {
                case (newCallCtx, newObj) => {
                  val argPropV = PropValue(ObjectValue(argVal, absTrue, absFalse, absFalse))
                  cfg.getFunc(fid) match {
                    case Some(funCFG) => {
                      val scopeObj = consObj.getOrElse("@scope", PropValue.Bot(utils))
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
            val consPropV = PropValue(ObjectValue(Value(PValue.Bot(utils), consLocSet), absTrue, absFalse, absTrue))
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
        val locR = Loc(aNew, Recent)
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
          val oldLocalObj = st1.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
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
            val fidSet = funObj.getOrElse("@function", PropValue.Bot(utils)).funid
            fidSet.foreach((fid) => {
              val newPureLocal = Obj.newPureLocalObj(Value(PValue.Bot(utils), HashSet(locR)), thisLocSet)(utils)
              val callCtxSet = callerCallCtx.newCallContext(st.heap, cfg, fid, locR, thisLocSet, newPureLocal, Some(aNew))
              callCtxSet.foreach {
                case (newCallCtx, newObj) => {
                  val value = PropValue(ObjectValue(argVal, absTrue, absFalse, absFalse))
                  cfg.getFunc(fid) match {
                    case Some(funCFG) => {
                      val oNew2 =
                        newObj.update(funCFG.argumentsName, value, exist = true)
                          .update("@scope", funObj.getOrElse("@scope", PropValue.Bot(utils)))
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
            val calleePropV = PropValue(ObjectValue(Value(PValue.Bot(utils), funLocSet), absTrue, absFalse, absTrue))
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
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
        val excSetPropV = localObj.getOrElse("@exception_all", PropValue.Bot(utils))
        val excV = localObj.getOrElse("@exception", PropValue.Bot(utils)).objval.value
        val h1 = helper.createMutableBinding(st.heap, x, excV)
        val newObj = h1.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot).update("@exception", excSetPropV)
        val h2 = h1.update(PredefLoc.SINGLE_PURE_LOCAL, newObj)
        (State(h2, st.context), State(Heap.Bot, Context.Bot))
      }
      case CFGReturn(_, _, Some(expr)) => {
        val (v, excSet) = V(expr, st)
        val (h1, ctx1) =
          if (!v.isBottom) {
            val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
            val retValPropV = PropValue(ObjectValue(v)(utils))
            (st.heap.update(PredefLoc.SINGLE_PURE_LOCAL, localObj.update("@return", retValPropV)), st.context)
          } else (Heap.Bot, Context.Bot)
        val newExcSt = helper.raiseException(st, excSet)
        (State(h1, ctx1), excSt + newExcSt)
      }
      case CFGReturn(_, _, None) => {
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
        val retValPropV = PropValue(utils.absUndef.Top)(utils)
        val h1 = st.heap.update(PredefLoc.SINGLE_PURE_LOCAL, localObj.update("@return", retValPropV))
        (State(h1, st.context), excSt)
      }
      case CFGThrow(_, _, expr) => {
        val (v, excSet) = V(expr, st)
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
        val excSetV = localObj.getOrElse("@exception_all", PropValue.Bot(utils)).objval.value
        val newExcPropV = PropValue(ObjectValue(v)(utils))
        val newExcSetPropV = PropValue(ObjectValue(v + excSetV)(utils))
        val retValPropV = PropValue(utils.absUndef.Top)(utils)
        val newObj =
          localObj.
            update("@exception", newExcPropV).
            update("@exception_all", newExcSetPropV).
            update("@return", retValPropV)
        val h1 = st.heap.update(PredefLoc.SINGLE_PURE_LOCAL, newObj)
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
                val boolVal = Value(PValue(b1 + b2)(utils))
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
                val numPV2 = PValue(numPV.toAbsNumber(utils.absNumber))(utils)
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
                val boolPV = PValue(v.toAbsBoolean(utils.absBool))(utils)
                (helper.varStore(st.heap, lhs, Value(boolPV)), st.context)
              } else {
                (Heap.Bot, Context.Bot)
              }
            val newExcSt = helper.raiseException(st, excSet)
            (State(h1, ctx1), excSt + newExcSt)
          }
          case ("<>Global<>getBase", List(CFGVarRef(_, x2)), None) => {
            val locSetBase = helper.lookupBase(st.heap, x2)
            val h1 = helper.varStore(st.heap, lhs, Value(PValue.Bot(utils), locSetBase))
            (State(h1, st.context), excSt)
          }
          case ("<>Global<>iteratorInit", List(expr), Some(aNew)) => (st, excSt)
          case ("<>Global<>iteratorHasNext", List(expr2, expr3), None) =>
            val boolPV = PValue(utils.absBool.Top)(utils)
            val h1 = helper.varStore(st.heap, lhs, Value(boolPV))
            (State(h1, st.context), excSt)
          case ("<>Global<>iteratorNext", List(expr2, expr3), None) =>
            val strPV = PValue(utils.absString.Top)(utils)
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
        val v1 = objLocSet.foldLeft(Value.Bot(utils))((tmpVal1, loc) => {
          absStrSet.foldLeft(tmpVal1)((tmpVal2, absStr) => {
            tmpVal2 + helper.proto(st.heap, loc, absStr)
          })
        })
        (v1, idxExcSet)
      }
      case CFGThis(ir) =>
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, utils.ObjBot)
        val thisLocSet = localObj.getOrElse("@this", PropValue.Bot(utils)).objval.value.locset
        (Value(PValue.Bot(utils), thisLocSet), ExceptionSetEmpty)
      case CFGBin(ir, expr1, op, expr2) => {
        val (v1, excSet1) = V(expr1, st)
        val (v2, excSet2) = V(expr2, st)
        (v1, v2) match {
          case _ if v1.isBottom => (Value.Bot(utils), excSet1)
          case _ if v2.isBottom => (Value.Bot(utils), excSet1 ++ excSet2)
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
                val protoVal = locSet3.foldLeft(Value.Bot(utils))((v, l) => {
                  v + helper.proto(st.heap, l, utils.absString.alpha("prototype"))
                })
                val locSet4 = protoVal.locset
                val locSet5 = locSet2.filter((l) => utils.absBool.False <= helper.hasInstance(st.heap, l))
                val b1 = locSet1.foldLeft[Value](Value.Bot(utils))((tmpVal1, loc1) => {
                  locSet4.foldLeft[Value](tmpVal1)((tmpVal2, loc2) =>
                    tmpVal2 + helper.inherit(st.heap, loc1, loc2, operator.bopSEq))
                })
                val pv2 =
                  if (!v2.pvalue.isBottom && !locSet4.isEmpty) PValue(utils.absBool.False)(utils)
                  else PValue.Bot(utils)
                val b2 = Value(pv2)
                val excSet3 =
                  if (!v2.pvalue.isBottom || !locSet5.isEmpty || !protoVal.pvalue.isBottom) HashSet(TypeError)
                  else ExceptionSetEmpty
                val b = b1 + b2
                val excSet = excSet1 ++ excSet2 ++ excSet3
                (b, excSet)
              case "in" => {
                val str = helper.toPrimitiveBetter(st.heap, v1).toAbsString(utils.absString)
                val absB = v2.locset.foldLeft(utils.absBool.Bot)((tmpAbsB, loc) => {
                  tmpAbsB + helper.hasProperty(st.heap, loc, str)
                })
                val b = Value(PValue(absB)(utils))
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
                val absStrPV = PValue(absStr1 + absStr2)(utils)
                (Value(absStrPV), ExceptionSetEmpty)
              case _ =>
                val absStrPV = PValue(helper.typeTag(st.heap, v))(utils)
                (Value(absStrPV), excSet)
            }
        }
      }
      case CFGVal(ejsVal) =>
        val pvalue: Utils => PValue = ejsVal match {
          case EJSNumber(_, num) => PValue(utils.absNumber.alpha(num))
          case EJSString(str) => PValue(utils.absString.alpha(str))
          case EJSBool(bool) => PValue(utils.absBool.alpha(bool))
          case EJSNull => PValue(utils.absNull.Top)
          case EJSUndef => PValue(utils.absUndef.Top)
        }
        (Value(pvalue(utils)), ExceptionSetEmpty)
    }
  }

  def B(expr: CFGExpr, st: State, excSt: State, inst: CFGInst, cfg: CFG, cp: ControlPoint): (State, State) = {
    val h1 = st.heap //TODO should be the pruned heap

    val (v, excSet) = V(expr, st)
    val newExcSt = helper.raiseException(st, excSet)
    val h2 =
      if (utils.absBool.alpha(true) <= v.toAbsBoolean(utils.absBool)) h1
      else Heap.Bot

    (State(h2, st.context), excSt + newExcSt)
  }
}
