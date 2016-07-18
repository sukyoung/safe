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

  private val afalse = utils.absBool.False
  private val atrue = utils.absBool.True

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
        val (c2, obj1) = ctx.fixOldify(obj, c1.mayOld, c1.mustOld)(utils)
        if (c2.isBottom) State.Bot
        else {
          val localObj = h1.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
          val returnV = localObj.getOrElse("@return")(Value.Bot(utils)) { _.objval.value }
          val h2 = h1.update(PredefLoc.SINGLE_PURE_LOCAL, obj1)
          val h3 = h2.varStore(retVar, returnV)(utils)
          State(h3, c2)
        }
      case (Exit(f), _) =>
        val c1 = st.context
        val (c2, obj1) = ctx.fixOldify(obj, c1.mayOld, c1.mustOld)(utils)
        if (c2.isBottom) State.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          State.Bot
        }
      case (ExitExc(_), _) if st.heap.isBottom => State.Bot
      case (ExitExc(_), _) if st.context.isBottom => State.Bot
      case (ExitExc(_), AfterCatch(_, _)) =>
        val (h1, c1) = (st.heap, st.context)
        val (c2, obj1) = ctx.fixOldify(obj, c1.mayOld, c1.mustOld)(utils)
        if (c2.isBottom) State.Bot
        else {
          val localObj = h1.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
          val excValue = localObj.getOrElse("@exception")(Value.Bot(utils)) { _.objval.value }
          val excObjV = ObjectValue(excValue, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
          val oldExcAllValue = obj1.getOrElse("@exception_all")(Value.Bot(utils)) { _.objval.value }
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
        val (c2, obj1) = ctx.fixOldify(obj, c1.mayOld, c1.mustOld)(utils)
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
            val localObj = h.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
            val locSetArg = localObj.getOrElse(fun.argumentsName)(LocSetEmpty) { _.objval.value.locset }
            val (nHeap, _) = xArgVars.foldLeft((h, 0))((res, x) => {
              val (iHeap, i) = res
              val vi = locSetArg.foldLeft(Value.Bot(utils))((vk, lArg) => {
                vk + iHeap.proto(lArg, utils.absString.alpha(i.toString))(utils)
              })
              (iHeap.createMutableBinding(x, vi)(utils), i + 1)
            })
            val hm = xLocalVars.foldLeft(nHeap)((jHeap, x) => {
              val undefPV = PValue(utils.absUndef.Top, utils.absNull.Bot, utils.absBool.Bot, utils.absNumber.Bot, utils.absString.Bot)
              jHeap.createMutableBinding(x, Value(undefPV))(utils)
            })
            (State(hm, ctx), State.Bot)
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
          case ModelBlock(_, sem) => (st, State.Bot)
        }
    }
  }

  def I(i: CFGNormalInst, st: State, excSt: State): (State, State) = {
    i match {
      case _ if st.heap.isBottom => (State.Bot, excSt)
      case CFGAlloc(_, _, x, e, newAddr) => {
        val objProtoSingleton = HashSet(BuiltinObject.PROTO_LOC)
        // Recency Abstraction
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)(utils)
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
        val h2 = st1.heap.allocObject(vLocSet, locR)(utils)
        val h3 = h2.varStore(x, Value(PValue.Bot(utils), HashSet(locR)))(utils)
        val newExcSt = st.raiseException(excSet)(utils)
        val s1 = excSt + newExcSt
        (State(h3, st1.context), s1)
      }
      case CFGAllocArray(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)(utils)
        val np = utils.absNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, Obj.newArrayObject(np)(utils))
        val h3 = h2.varStore(x, Value(PValue.Bot(utils), HashSet(locR)))(utils)
        (State(h3, st1.context), excSt)
      }
      case CFGAllocArg(_, _, x, n, newAddr) => {
        val locR = Loc(newAddr, Recent)
        val st1 = st.oldify(newAddr)(utils)
        val absN = utils.absNumber.alpha(n.toInt)
        val h2 = st1.heap.update(locR, Obj.newArgObject(absN)(utils))
        val h3 = h2.varStore(x, Value(PValue.Bot(utils), HashSet(locR)))(utils)
        (State(h3, st1.context), excSt)
      }
      case CFGExprStmt(_, _, x, e) => {
        val (v, excSet) = V(e, st)
        val (h1, ctx1) =
          if (!v.isBottom) (st.heap.varStore(x, v)(utils), st.context)
          else (Heap.Bot, Context.Bot)
        val newExcSt = st.raiseException(excSet)(utils)
        (State(h1, ctx1), excSt + newExcSt)
      }
      case CFGDelete(_, _, x1, CFGVarRef(_, x2)) => {
        val baseLocSet = st.heap.lookupBase(x2)(utils)
        val (h1, b) =
          if (baseLocSet.isEmpty) {
            (st.heap, atrue)
          } else {
            val x2Abs = utils.absString.alpha(x2.toString)
            baseLocSet.foldLeft[(Heap, AbsBool)](Heap.Bot, utils.absBool.Bot)((res, baseLoc) => {
              val (tmpHeap, tmpB) = res
              val (delHeap, delB) = st.heap.delete(baseLoc, x2Abs)(utils)
              (tmpHeap + delHeap, tmpB + delB)
            })
          }
        val bVal = Value(PValue(b)(utils))
        val h2 = h1.varStore(x1, bVal)(utils)
        (State(h2, st.context), excSt)
      }
      case CFGDelete(_, _, x1, expr) => {
        val (v, excSet) = V(expr, st)
        val (h1, ctx1) =
          if (!v.isBottom) {
            val trueVal = Value(PValue(atrue)(utils))
            (st.heap.varStore(x1, trueVal)(utils), st.context)
          } else (Heap.Bot, Context.Bot)
        val newExcSt = st.raiseException(excSet)(utils)
        (State(h1, ctx1), excSt + newExcSt)
      }
      case CFGDeleteProp(_, _, lhs, obj, index) => {
        // locSet must not be empty because obj is coming through <>toObject.
        val (value, _) = V(obj, st)
        val locSet = value.locset
        val (v, excSet) = V(index, st)
        val absStrSet =
          if (v.isBottom) HashSet[AbsString]()
          else v.toPrimitiveBetter(st.heap)(utils).toStringSet(utils.absString)
        val (h1: Heap, b: AbsBool) = locSet.foldLeft[(Heap, AbsBool)](Heap.Bot, utils.absBool.Bot)((res1, l) => {
          val (tmpHeap1, tmpB1) = res1
          absStrSet.foldLeft((tmpHeap1, tmpB1))((res2, s) => {
            val (tmpHeap2, tmpB2) = res2
            val (delHeap, delB) = st.heap.delete(l, s)(utils)
            (tmpHeap2 + delHeap, tmpB2 + delB)
          })
        })
        val (h2, ctx2) =
          if (h1.isBottom) (Heap.Bot, Context.Bot)
          else {
            val boolPV = PValue(b)(utils)
            (h1.varStore(lhs, Value(boolPV))(utils), st.context)
          }
        val newExcSt = st.raiseException(excSet)(utils)
        (State(h2, ctx2), excSt + newExcSt)
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
        val oNew = Obj.newObject(BuiltinObject.PROTO_LOC)(utils)

        val n = utils.absNumber.alpha(f.argVars.length)
        val localObj = st2.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val scope = localObj.getOrElse("@env")(Value.Bot(utils)) { _.objval.value }
        val h3 = st2.heap.update(locR1, Obj.newFunctionObject(f.id, scope, locR2, n)(utils))

        val fVal = Value(PValue.Bot(utils), HashSet(locR1))
        val fPropV = PropValue(ObjectValue(fVal, atrue, afalse, atrue))
        val h4 = h3.update(locR2, oNew.update("constructor", fPropV, exist = true))

        val h5 = h4.varStore(lhs, fVal)(utils)
        (State(h5, st2.context), excSt)
      }
      case CFGFunExpr(_, block, lhs, Some(name), f, aNew1, aNew2, Some(aNew3)) => {
        // Recency Abstraction
        val locR1 = Loc(aNew1, Recent)
        val locR2 = Loc(aNew2, Recent)
        val locR3 = Loc(aNew3, Recent)
        val st1 = st.oldify(aNew1)(utils)
        val st2 = st1.oldify(aNew2)(utils)
        val st3 = st2.oldify(aNew3)(utils)

        val oNew = Obj.newObject(BuiltinObject.PROTO_LOC)(utils)
        val n = utils.absNumber.alpha(f.argVars.length)
        val fObjValue = Value(PValue.Bot(utils), HashSet(locR3))
        val h4 = st3.heap.update(locR1, Obj.newFunctionObject(f.id, fObjValue, locR2, n)(utils))

        val fVal = Value(PValue.Bot(utils), HashSet(locR1))
        val fPropV = PropValue(ObjectValue(fVal, atrue, afalse, atrue))
        val h5 = h4.update(locR2, oNew.update("constructor", fPropV, exist = true))

        val localObj = st3.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val scope = localObj.getOrElse("@env")(Value.Bot(utils)) { _.objval.value }
        val oEnv = Obj.newDeclEnvRecordObj(scope)(utils)
        val fPropV2 = PropValue(ObjectValue(fVal, afalse, utils.absBool.Bot, afalse))
        val h6 = h5.update(locR3, oEnv.update(name.text, fPropV2))
        val h7 = h6.varStore(lhs, fVal)(utils)
        (State(h7, st3.context), excSt)
      }
      case CFGAssert(_, _, expr, _) => B(expr, st, excSt, i, cfg)
      case CFGCatch(_, _, x) => {
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val excSetPropV = localObj.get("@exception_all")(utils)
        val excV = localObj.getOrElse("@exception")(Value.Bot(utils)) { _.objval.value }
        val h1 = st.heap.createMutableBinding(x, excV)(utils)
        val newObj = h1.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils)).update("@exception", excSetPropV)
        val h2 = h1.update(PredefLoc.SINGLE_PURE_LOCAL, newObj)
        (State(h2, st.context), State(Heap.Bot, Context.Bot))
      }
      case CFGReturn(_, _, Some(expr)) => {
        val (v, excSet) = V(expr, st)
        val (h1, ctx1) =
          if (!v.isBottom) {
            val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
            val retValPropV = PropValue(ObjectValue(v)(utils))
            (st.heap.update(PredefLoc.SINGLE_PURE_LOCAL, localObj.update("@return", retValPropV)), st.context)
          } else (Heap.Bot, Context.Bot)
        val newExcSt = st.raiseException(excSet)(utils)
        (State(h1, ctx1), excSt + newExcSt)
      }
      case CFGReturn(_, _, None) => {
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val retValPropV = PropValue(utils.absUndef.Top)(utils)
        val h1 = st.heap.update(PredefLoc.SINGLE_PURE_LOCAL, localObj.update("@return", retValPropV))
        (State(h1, st.context), excSt)
      }
      case CFGThrow(_, _, expr) => {
        val (v, excSet) = V(expr, st)
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val excSetV = localObj.getOrElse("@exception_all")(Value.Bot(utils)) { _.objval.value }
        val newExcPropV = PropValue(ObjectValue(v)(utils))
        val newExcSetPropV = PropValue(ObjectValue(v + excSetV)(utils))
        val retValPropV = PropValue(utils.absUndef.Top)(utils)
        val newObj =
          localObj.
            update("@exception", newExcPropV).
            update("@exception_all", newExcSetPropV).
            update("@return", retValPropV)
        val h1 = st.heap.update(PredefLoc.SINGLE_PURE_LOCAL, newObj)
        val newExcSt = st.raiseException(excSet)(utils)

        (State.Bot, excSt + State(h1, st.context) + newExcSt)
      }
      case CFGInternalCall(ir, info, lhs, fun, arguments, loc) =>
        (fun.toString, arguments, loc) match {
          case ("<>Global<>toObject", List(expr), Some(aNew)) => {
            val (v, excSet1) = V(expr, st)
            val (v1, st1, excSet2) = helper.toObject(st, v, aNew)
            val (h2, ctx2) =
              if (!v1.isBottom)
                (st1.heap.varStore(lhs, v1)(utils), st1.context)
              else
                (Heap.Bot, Context.Bot)
            val (st3, excSet3) =
              if (!v.isBottom)
                (State(h2, ctx2), excSet1 ++ excSet2)
              else
                (State.Bot, excSet1)
            val newExcSt = st.raiseException(excSet3)(utils)
            (st3, excSt + newExcSt)
          }
          case ("<>Global<>isObject", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val (h1, ctx1) =
              if (!v.isBottom) {
                val b1 =
                  if (!v.locset.isEmpty) atrue
                  else utils.absBool.Bot
                val b2 =
                  if (!v.pvalue.isBottom) afalse
                  else utils.absBool.Bot
                val boolVal = Value(PValue(b1 + b2)(utils))
                (st.heap.varStore(lhs, boolVal)(utils), st.context)
              } else {
                (Heap.Bot, Context.Bot)
              }
            val newExcSt = st.raiseException(excSet)(utils)
            (State(h1, ctx1), excSt + newExcSt)
          }
          case ("<>Global<>toNumber", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val (h1, ctx1) =
              if (!v.isBottom) {
                val numPV = v.toPrimitiveBetter(st.heap)(utils)
                val numPV2 = PValue(numPV.toAbsNumber(utils.absNumber))(utils)
                (st.heap.varStore(lhs, Value(numPV2))(utils), st.context)
              } else {
                (Heap.Bot, Context.Bot)
              }
            val newExcSt = st.raiseException(excSet)(utils)
            (State(h1, ctx1), excSt + newExcSt)
          }
          case ("<>Global<>toBoolean", List(expr), None) => {
            val (v, excSet) = V(expr, st)
            val (h1, ctx1) =
              if (!v.isBottom) {
                val boolPV = PValue(v.toAbsBoolean(utils.absBool))(utils)
                (st.heap.varStore(lhs, Value(boolPV))(utils), st.context)
              } else {
                (Heap.Bot, Context.Bot)
              }
            val newExcSt = st.raiseException(excSet)(utils)
            (State(h1, ctx1), excSt + newExcSt)
          }
          case ("<>Global<>getBase", List(CFGVarRef(_, x2)), None) => {
            val locSetBase = st.heap.lookupBase(x2)(utils)
            val h1 = st.heap.varStore(lhs, Value(PValue.Bot(utils), locSetBase))(utils)
            (State(h1, st.context), excSt)
          }
          case ("<>Global<>iteratorInit", List(expr), Some(aNew)) => (st, excSt)
          case ("<>Global<>iteratorHasNext", List(expr2, expr3), None) =>
            val boolPV = PValue(utils.absBool.Top)(utils)
            val h1 = st.heap.varStore(lhs, Value(boolPV))(utils)
            (State(h1, st.context), excSt)
          case ("<>Global<>iteratorNext", List(expr2, expr3), None) =>
            val strPV = PValue(utils.absString.Top)(utils)
            val h1 = st.heap.varStore(lhs, Value(strPV))(utils)
            (State(h1, st.context), excSt)
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
      case _: CFGConstruct => funVal.locset.filter(l => atrue <= st1.heap.hasConstruct(l)(utils.absBool))
      case _: CFGCall => funVal.locset.filter(l => atrue <= st1.heap.isCallable(l)(utils.absBool))
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
          val newPureLocal = Obj.newPureLocalObj(Value(locR)(utils), thisLocSet)(utils)
          val callerCtxSet = callerCallCtx.newCallContext(st1.heap, cfg, fid, locR, thisLocSet, newPureLocal, Some(i.addr1))
          callerCtxSet.foreach {
            case (newCallCtx, newObj) => {
              val argPropV = PropValue(ObjectValue(argVal, atrue, afalse, afalse))
              cfg.getFunc(fid) match {
                case Some(funCFG) => {
                  val scopeObj = funObj.get("@scope")(utils)
                  val newObj2 = newObj.update(funCFG.argumentsName, argPropV, exist = true)
                    .update("@scope", scopeObj)
                  val entryCP = ControlPoint(funCFG.entry, newCallCtx)
                  val exitCP = ControlPoint(funCFG.exit, newCallCtx)
                  val exitExcCP = ControlPoint(funCFG.exitExc, newCallCtx)
                  addCallEdge(cp, entryCP, Context.Empty, newObj2)
                  addReturnEdge(exitCP, cpAfterCall, st1.context, oldLocalObj)
                  addReturnEdge(exitExcCP, cpAfterCatch, st1.context, oldLocalObj)
                }
                case None => excLog.signal(UndefinedFunctionCallError(i.ir))
              }
            }
          }
        })
      })

      val h2 = argVal.locset.foldLeft(Heap.Bot)((tmpHeap, l) => {
        val funPropV = PropValue(ObjectValue(Value(funLocSet)(utils), atrue, afalse, atrue))
        val argObj = st1.heap.getOrElse(l, Obj.Bot(utils))
        tmpHeap + st1.heap.update(l, argObj.update("callee", funPropV))
      })

      // exception handling
      val typeExcSet1 = i match {
        case _: CFGConstruct if funVal.locset.exists(l => afalse <= st1.heap.hasConstruct(l)(utils.absBool)) => HashSet(TypeError)
        case _: CFGCall if funVal.locset.exists(l => afalse <= st1.heap.isCallable(l)(utils.absBool)) => HashSet(TypeError)
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
      (State(h3, st1.context), excSt + newExcSt)
    }
  }

  def V(expr: CFGExpr, st: State): (Value, Set[Exception]) = {
    expr match {
      case CFGVarRef(ir, id) => st.heap.lookup(id)(utils)
      case CFGLoad(ir, obj, index) => {
        val (objV, _) = V(obj, st)
        val objLocSet = objV.locset
        val (idxV, idxExcSet) = V(index, st)
        val absStrSet =
          if (!idxV.isBottom) idxV.toPrimitiveBetter(st.heap)(utils).toStringSet(utils.absString)
          else HashSet[AbsString]()
        val v1 = objLocSet.foldLeft(Value.Bot(utils))((tmpVal1, loc) => {
          absStrSet.foldLeft(tmpVal1)((tmpVal2, absStr) => {
            tmpVal2 + st.heap.proto(loc, absStr)(utils)
          })
        })
        (v1, idxExcSet)
      }
      case CFGThis(ir) =>
        val localObj = st.heap.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val thisLocSet = localObj.getOrElse("@this")(LocSetEmpty) { _.objval.value.locset }
        (Value(PValue.Bot(utils), thisLocSet), ExceptionSetEmpty)
      case CFGBin(ir, expr1, op, expr2) => {
        val (v1, excSet1) = V(expr1, st)
        val (v2, excSet2) = V(expr2, st)
        (v1, v2) match {
          case _ if v1.isBottom => (Value.Bot(utils), excSet1)
          case _ if v2.isBottom => (Value.Bot(utils), excSet1 ++ excSet2)
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
                val locSet3 = locSet2.filter((l) => atrue <= st.heap.hasInstance(l)(utils.absBool))
                val protoVal = locSet3.foldLeft(Value.Bot(utils))((v, l) => {
                  v + st.heap.proto(l, utils.absString.alpha("prototype"))(utils)
                })
                val locSet4 = protoVal.locset
                val locSet5 = locSet2.filter((l) => afalse <= st.heap.hasInstance(l)(utils.absBool))
                val b1 = locSet1.foldLeft[Value](Value.Bot(utils))((tmpVal1, loc1) => {
                  locSet4.foldLeft[Value](tmpVal1)((tmpVal2, loc2) =>
                    tmpVal2 + helper.inherit(st.heap, loc1, loc2))
                })
                val pv2 =
                  if (!v2.pvalue.isBottom && !locSet4.isEmpty) PValue(afalse)(utils)
                  else PValue.Bot(utils)
                val b2 = Value(pv2)
                val excSet3 =
                  if (!v2.pvalue.isBottom || !locSet5.isEmpty || !protoVal.pvalue.isBottom) HashSet(TypeError)
                  else ExceptionSetEmpty
                val b = b1 + b2
                val excSet = excSet1 ++ excSet2 ++ excSet3
                (b, excSet)
              case "in" => {
                val str = v1.toPrimitiveBetter(st.heap)(utils).toAbsString(utils.absString)
                val absB = v2.locset.foldLeft(utils.absBool.Bot)((tmpAbsB, loc) => {
                  tmpAbsB + st.heap.hasProperty(loc, str)(utils)
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
                val absStrPV = PValue(absStr1 + absStr2)(utils)
                (Value(absStrPV), ExceptionSetEmpty)
              case _ =>
                val absStrPV = PValue(v.typeTag(st.heap)(utils))(utils)
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

  def B(expr: CFGExpr, st: State, excSt: State, inst: CFGInst, cfg: CFG): (State, State) = {
    val h1 = st.heap //TODO should be the pruned heap

    val (v, excSet) = V(expr, st)
    val newExcSt = st.raiseException(excSet)(utils)
    val h2 =
      if (utils.absBool.alpha(true) <= v.toAbsBoolean(utils.absBool)) h1
      else Heap.Bot

    (State(h2, st.context), excSt + newExcSt)
  }
}
