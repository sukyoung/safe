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

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util.{ Loc, Address, Old, Recent }

case class Helper(utils: Utils) {

  def allocObject(h: Heap, locSetV: Set[Loc], locR: Loc): Heap = {
    val newObj = Obj.newObject(locSetV)(utils)
    h.update(locR, newObj)
  }

  def createMutableBinding(h: Heap, id: CFGId, value: Value): Heap = {
    val x = id.text
    id.kind match {
      case PureLocalVar =>
        val localLoc = PredefLoc.SINGLE_PURE_LOCAL
        val objV = ObjectValue(value, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        h.update(localLoc, h.getOrElse(localLoc, Obj.Bot(utils)).update(x, propV))
      case CapturedVar =>
        val localLoc = PredefLoc.SINGLE_PURE_LOCAL
        val objV = ObjectValue(value, utils.absBool.True, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        val localObj = h.getOrElse(localLoc, Obj.Bot(utils))
        localObj.getOrElse("@env")(Heap.Bot) { propv =>
          propv.objval.value.locset.foldLeft(Heap.Bot)((tmpHeap, loc) => {
            tmpHeap + h.update(loc, h.getOrElse(loc, Obj.Bot(utils)).update(x, propV))
          })
        }
      case CapturedCatchVar =>
        val collapsedLoc = PredefLoc.COLLAPSED
        val objV = ObjectValue(value, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        h.update(collapsedLoc, h.getOrElse(collapsedLoc, Obj.Bot(utils)).update(x, propV))
      case GlobalVar =>
        val globalLoc = PredefLoc.GLOBAL
        val objV = ObjectValue(value, utils.absBool.True, utils.absBool.True, utils.absBool.False)
        val propV = PropValue(objV)
        if (utils.absBool.True == h.hasProperty(globalLoc, utils.absString.alpha(x))(utils)) h
        else h.update(globalLoc, h.getOrElse(globalLoc, Obj.Bot(utils)).update(x, propV))
    }
  }

  def delete(h: Heap, loc: Loc, absStr: AbsString): (Heap, AbsBool) = {
    val test = h.hasOwnProperty(loc, absStr)(utils)
    val targetObj = h.getOrElse(loc, Obj.Bot(utils))
    val isConfigurable = targetObj.getOrElse(absStr)(utils.absBool.Bot) { _.objval.configurable }
    val (h1, b1) =
      if ((utils.absBool.True <= test) && (utils.absBool.False <= isConfigurable))
        (h, utils.absBool.False)
      else
        (Heap.Bot, utils.absBool.Bot)
    val (h2, b2) =
      if (((utils.absBool.True <= test) && (utils.absBool.False != isConfigurable))
        || utils.absBool.False <= test)
        (h.update(loc, (targetObj - absStr)(utils)), utils.absBool.True)
      else
        (Heap.Bot, utils.absBool.Bot)
    (h1 + h2, b1 + b2)
  }

  def raiseException(st: State, excSet: Set[Exception]): State = {
    if (excSet.isEmpty)
      State(Heap.Bot, Context.Bot)
    else {
      val localLoc = PredefLoc.SINGLE_PURE_LOCAL
      val localObj = st.heap.getOrElse(localLoc, Obj.Bot(utils))
      val oldValue = localObj.getOrElse("@exception_all")(Value.Bot(utils)) { _.objval.value }
      val newExcSet = excSet.foldLeft(LocSetEmpty)((locSet, exc) => locSet + exc.getLoc)
      val excValue = Value(PValue.Bot(utils), newExcSet)
      val newExcObjV = ObjectValue(excValue)(utils)
      val newExcSetObjV = ObjectValue(excValue + oldValue)(utils)
      val h1 = st.heap.update(
        localLoc,
        localObj.update("@exception", PropValue(newExcObjV)).
          update("@exception_all", PropValue(newExcSetObjV))
      )
      State(h1, st.context)
    }
  }

  def getThis(h: Heap, value: Value): Set[Loc] = {
    val locSet1 = (value.pvalue.nullval.gamma, value.pvalue.undefval.gamma) match {
      case (ConSimpleBot, ConSimpleBot) => LocSetEmpty
      case _ => HashSet(PredefLoc.GLOBAL)
    }

    val foundDeclEnvRecord = value.locset.exists(loc => utils.absBool.False <= h.isObject(loc)(utils))

    val locSet2 =
      if (foundDeclEnvRecord) HashSet(PredefLoc.GLOBAL)
      else LocSetEmpty
    val locSet3 = value.locset.foldLeft(LocSetEmpty)((tmpLocSet, loc) => {
      if (utils.absBool.True <= h.isObject(loc)(utils)) tmpLocSet + loc
      else tmpLocSet
    })

    locSet1 ++ locSet2 ++ locSet3
  }

  def lookup(h: Heap, id: CFGId): (Value, Set[Exception]) = {
    val x = id.text
    val localObj = h.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
    id.kind match {
      case PureLocalVar => (localObj.getOrElse(x)(Value.Bot(utils)) { _.objval.value }, ExceptionSetEmpty)
      case CapturedVar =>
        val envLocSet = localObj.getOrElse("@env")(LocSetEmpty) { _.objval.value.locset }
        val value = envLocSet.foldLeft(Value.Bot(utils))((tmpVal, envLoc) => {
          tmpVal + lookupL(h, envLoc, x)
        })
        (value, ExceptionSetEmpty)
      case CapturedCatchVar =>
        val collapsedObj = h.getOrElse(PredefLoc.COLLAPSED, Obj.Bot(utils))
        (collapsedObj.getOrElse(x)(Value.Bot(utils)) { _.objval.value }, ExceptionSetEmpty)
      case GlobalVar => lookupG(h, x)
    }
  }

  def lookupG(h: Heap, x: String): (Value, Set[Exception]) = {
    if (h domIn PredefLoc.GLOBAL) {
      val globalObj = h.getOrElse(PredefLoc.GLOBAL, Obj.Bot(utils))
      val v1 =
        if (utils.absBool.True <= (globalObj domIn x)(utils.absBool))
          globalObj.getOrElse(x)(Value.Bot(utils)) { _.objval.value }
        else
          Value.Bot(utils)
      val protoLocSet = globalObj.getOrElse("@proto")(LocSetEmpty) { _.objval.value.locset }
      val (v2, excSet) =
        if (utils.absBool.False <= (globalObj domIn x)(utils.absBool)) {
          val excSet = protoLocSet.foldLeft(ExceptionSetEmpty)((tmpExcSet, protoLoc) => {
            if (utils.absBool.False <= h.hasProperty(protoLoc, utils.absString.alpha(x))(utils)) {
              tmpExcSet + ReferenceError
            } else tmpExcSet
          })
          val v3 = protoLocSet.foldLeft(Value.Bot(utils))((tmpVal, protoLoc) => {
            if (utils.absBool.True <= h.hasProperty(protoLoc, utils.absString.alpha(x))(utils)) {
              tmpVal + proto(h, protoLoc, utils.absString.alpha(x))
            } else {
              tmpVal
            }
          })
          (v3, excSet)
        } else {
          (Value.Bot(utils), ExceptionSetEmpty)
        }
      (v1 + v2, excSet)
    } else {
      (Value.Bot(utils), ExceptionSetEmpty)
    }
  }

  def lookupL(h: Heap, loc: Loc, x: String): Value = {
    var visited = LocSetEmpty
    def visit(l: Loc): Value = {
      if (visited.contains(l)) Value.Bot(utils)
      else {
        visited += l
        val env = h.getOrElse(l, Obj.Bot(utils))
        val isDomIn = (env domIn x)(utils.absBool)
        val v1 =
          if (utils.absBool.True <= isDomIn) env.getOrElse(x)(Value.Bot(utils)) { _.objval.value }
          else Value.Bot(utils)
        val v2 =
          if (utils.absBool.False <= isDomIn) {
            val outerLocSet = env.getOrElse("@outer")(LocSetEmpty) { _.objval.value.locset }
            outerLocSet.foldLeft(Value.Bot(utils))((tmpVal, outerLoc) => tmpVal + visit(outerLoc))
          } else {
            Value.Bot(utils)
          }
        v1 + v2
      }
    }
    visit(loc)
  }

  def lookupBase(h: Heap, id: CFGId): Set[Loc] = {
    val x = id.text
    id.kind match {
      case PureLocalVar => HashSet(PredefLoc.SINGLE_PURE_LOCAL)
      case CapturedVar =>
        val localObj = h.getOrElse(PredefLoc.SINGLE_PURE_LOCAL, Obj.Bot(utils))
        val envLocSet = localObj.getOrElse("@env")(LocSetEmpty) { _.objval.value.locset }
        envLocSet.foldLeft(LocSetEmpty)((tmpLocSet, l) => {
          tmpLocSet ++ lookupBaseL(h, l, x)
        })
      case CapturedCatchVar => HashSet(PredefLoc.COLLAPSED)
      case GlobalVar => lookupBaseG(h, x)
    }
  }

  def lookupBaseG(h: Heap, x: String): Set[Loc] = {
    val globalObj = h.getOrElse(PredefLoc.GLOBAL, Obj.Bot(utils))
    val isDomIn = (globalObj domIn x)(utils.absBool)
    val locSet1 =
      if (utils.absBool.True <= isDomIn)
        HashSet(PredefLoc.GLOBAL)
      else
        LocSetEmpty
    val locSet2 =
      if (utils.absBool.False <= isDomIn) {
        val protoLocSet = globalObj.getOrElse("@proto")(LocSetEmpty) { _.objval.value.locset }
        protoLocSet.foldLeft(LocSetEmpty)((res, protoLoc) => {
          res ++ protoBase(h, protoLoc, utils.absString.alpha(x))
        })
      } else {
        LocSetEmpty
      }
    locSet1 ++ locSet2
  }

  def lookupBaseL(h: Heap, loc: Loc, x: String): Set[Loc] = {
    var visited = LocSetEmpty
    def visit(l: Loc): Set[Loc] = {
      if (visited.contains(l)) LocSetEmpty
      else {
        visited += l
        val env = h.getOrElse(l, Obj.Bot(utils))
        val isDomIn = (env domIn x)(utils.absBool)
        val locSet1 =
          if (utils.absBool.True <= isDomIn) HashSet(l)
          else LocSetEmpty
        val locSet2 =
          if (utils.absBool.False <= isDomIn) {
            val outerLocSet = env.getOrElse("@outer")(LocSetEmpty) { _.objval.value.locset }
            outerLocSet.foldLeft(LocSetEmpty)((res, outerLoc) => res ++ visit(outerLoc))
          } else {
            LocSetEmpty
          }
        locSet1 ++ locSet2
      }
    }
    visit(loc)
  }

  def oldify(st: State, addr: Address): State = {
    if (st.context.isBottom) State.Bot
    else {
      val locR = Loc(addr, Recent)
      val locO = Loc(addr, Old)
      val h1 =
        if ((st.heap domIn locR))
          st.heap.update(locO, st.heap.getOrElse(locR, Obj.Bot(utils))).remove(locR).subsLoc(locR, locO)
        else
          st.heap.subsLoc(locR, locO)
      val ctx1 = st.context.subsLoc(locR, locO)
      State(h1, ctx1)
    }
  }

  def fixOldify(ctx: Context, obj: Obj, mayOld: Set[Address], mustOld: Set[Address]): (Context, Obj) = {
    if (ctx.isBottom) (Context.Bot, Obj.Bot(utils))
    else {
      mayOld.foldLeft((ctx, obj))((res, a) => {
        val (resCtx, resObj) = res
        val locR = Loc(a, Recent)
        val locO = Loc(a, Old)
        if (mustOld contains a) {
          val newCtx = resCtx.subsLoc(locR, locO)
          val newObj = resObj.subsLoc(locR, locO)
          (newCtx, newObj)
        } else {
          val newCtx = resCtx.weakSubsLoc(locR, locO)
          val newObj = resObj.weakSubsLoc(locR, locO)
          (newCtx, newObj)
        }
      })
    }
  }

  def proto(h: Heap, loc: Loc, absStr: AbsString): Value = {
    var visited = LocSetEmpty
    def visit(currentLoc: Loc): Value = {
      if (visited.contains(currentLoc)) Value.Bot(utils)
      else {
        visited += currentLoc
        val test = (h.getOrElse(currentLoc, Obj.Bot(utils)) domIn absStr)(utils.absBool)
        val v1 =
          if (utils.absBool.True <= test) {
            h.getOrElse(currentLoc, Obj.Bot(utils)).getOrElse(absStr)(Value.Bot(utils)) { _.objval.value }
          } else {
            Value.Bot(utils)
          }
        val v2 =
          if (utils.absBool.False <= test) {
            val protoV = h.getOrElse(currentLoc, Obj.Bot(utils)).getOrElse("@proto")(Value.Bot(utils)) { _.objval.value }
            val v3 = protoV.pvalue.nullval.fold(Value.Bot(utils))(_ => {
              Value(PValue(utils.absUndef.Top)(utils))
            })
            v3 + protoV.locset.foldLeft(Value.Bot(utils))((v, protoLoc) => {
              v + visit(protoLoc)
            })
          } else {
            Value.Bot(utils)
          }
        v1 + v2
      }
    }
    visit(loc)
  }

  def protoBase(h: Heap, loc: Loc, absStr: AbsString): Set[Loc] = {
    var visited = LocSetEmpty

    def visit(l: Loc): Set[Loc] = {
      if (visited.contains(l)) LocSetEmpty
      else {
        visited += l
        val obj = h.getOrElse(l, Obj.Bot(utils))
        val isDomIn = (obj domIn absStr)(utils.absBool)
        val locSet1 =
          if (utils.absBool.True <= isDomIn) HashSet(l)
          else LocSetEmpty
        val locSet2 =
          if (utils.absBool.False <= isDomIn) {
            val protoLocSet = obj.getOrElse("@proto")(LocSetEmpty) { _.objval.value.locset }
            protoLocSet.foldLeft(LocSetEmpty)((res, protoLoc) => {
              res ++ visit(protoLoc)
            })
          } else {
            LocSetEmpty
          }
        locSet1 ++ locSet2
      }
    }
    visit(loc)
  }

  def varStore(h: Heap, id: CFGId, value: Value): Heap = {
    val pureLocalLoc = PredefLoc.SINGLE_PURE_LOCAL
    val x = id.text
    id.kind match {
      case PureLocalVar =>
        val pv = PropValue(ObjectValue(value, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False))
        val obj = h.getOrElse(pureLocalLoc, Obj.Bot(utils))
        h.update(pureLocalLoc, obj.update(x, pv))
      case CapturedVar =>
        h.getOrElse(pureLocalLoc, Obj.Bot(utils)).getOrElse("@env")(Heap.Bot) { propv =>
          propv.objval.value.locset.foldLeft(Heap.Bot)((tmpH, loc) => {
            tmpH + varStoreL(h, loc, x, value)
          })
        }
      case CapturedCatchVar =>
        val propV = PropValue(ObjectValue(value, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False))
        val obj = h.getOrElse(PredefLoc.COLLAPSED, Obj.Bot(utils))
        h.update(PredefLoc.COLLAPSED, obj.update(x, propV))
      case GlobalVar => {
        val h1 =
          if (utils.absBool.True <= h.canPutVar(x)(utils)) varStoreG(h, x, value)
          else Heap.Bot
        val h2 =
          if (utils.absBool.False <= h.canPutVar(x)(utils)) h
          else Heap.Bot
        h1 + h2
      }
    }
  }

  def varStoreL(h: Heap, loc: Loc, x: String, value: Value): Heap = {
    val envObj = h.getOrElse(loc, Obj.Bot(utils))
    val h1 = envObj(x) match {
      case Some(propV) if propV.objval.writable == utils.absBool.True =>
        val newPropV = PropValue(ObjectValue(value, utils.absBool.True, utils.absBool.Bot, utils.absBool.False))
        h.update(loc, envObj.update(x, newPropV))
      case Some(propV) if propV.objval.writable == utils.absBool.False => h
      case _ => Heap.Bot
    }
    val outerLocSet = envObj("@outer") match {
      case Some(propV) => propV.objval.value.locset
      case None => LocSetEmpty
    }
    val h2 =
      if (utils.absBool.False <= (envObj domIn x)(utils.absBool))
        outerLocSet.foldLeft(Heap.Bot)((tmpH, outerLoc) => varStoreL(h, outerLoc, x, value))
      else
        Heap.Bot
    h1 + h2
  }

  def varStoreG(h: Heap, x: String, value: Value): Heap = {
    val globalLoc = PredefLoc.GLOBAL
    val obj = h.getOrElse(globalLoc, Obj.Bot(utils))
    val h1 =
      if (utils.absBool.False <= (obj domIn x)(utils.absBool))
        propStore(h, globalLoc, utils.absString.alpha(x), value)
      else Heap.Bot
    val h2 =
      if (utils.absBool.True <= (obj domIn x)(utils.absBool)) {
        val oldObjVal = obj.getOrElse(x)(ObjectValue.Bot(utils)) { _.objval }
        val newObjVal = oldObjVal.copyWith(value)
        h.update(globalLoc, obj.update(x, PropValue(newObjVal)))
      } else Heap.Bot
    h1 + h2
  }

  def propStore(h: Heap, loc: Loc, absStr: AbsString, value: Value): Heap = {
    val findingObj = h.getOrElse(loc, Obj.Bot(utils))
    val objDomIn = (findingObj domIn absStr)(utils.absBool)
    objDomIn.gamma match {
      case ConSingleTop() =>
        val oldObjV: ObjectValue = findingObj.getOrElse(absStr)(ObjectValue.Bot(utils)) { _.objval }
        val newObjV = ObjectValue(
          value,
          oldObjV.writable + utils.absBool.True,
          oldObjV.enumerable + utils.absBool.True,
          oldObjV.configurable + utils.absBool.True
        )
        h.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
      case ConSingleBot() => Heap.Bot
      case ConSingleCon(true) =>
        val oldObjV: ObjectValue = findingObj.getOrElse(absStr)(ObjectValue.Bot(utils)) { _.objval }
        val newObjV = oldObjV.copyWith(value)
        h.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
      case ConSingleCon(false) =>
        val oldObjV: ObjectValue = findingObj.getOrElse(absStr)(ObjectValue.Bot(utils)) { _.objval }
        val newObjV = ObjectValue(value, utils.absBool.True, utils.absBool.True, utils.absBool.True)
        h.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
    }
  }

  def propStoreWeak(h: Heap, loc: Loc, absStr: AbsString, value: Value): Heap = Heap.Bot

  def returnStore(h: Heap, value: Value): Heap = Heap.Bot

  def toObject(st: State, value: Value, newAddr: Address): (Value, State, Set[Exception]) = {
    val locSet = value.locset
    val pv = value.pvalue

    val excSet = (pv.undefval.gamma, pv.nullval.gamma) match {
      case (ConSimpleBot, ConSimpleBot) => ExceptionSetEmpty
      case _ => HashSet[Exception](TypeError)
    }
    val obj1 = pv.strval.fold(Obj.Bot(utils)) { Obj.newStringObj(_)(utils) }
    val obj2 = pv.boolval.fold(Obj.Bot(utils)) { Obj.newBooleanObj(_)(utils) }
    val obj3 = pv.numval.fold(Obj.Bot(utils)) { Obj.newNumberObj(_)(utils) }
    val obj = obj1 + obj2 + obj3

    val recLoc = Loc(newAddr, Recent)
    val (locSet1, h2, ctx2) =
      if (!obj.isBottom) {
        val st1 = oldify(st, newAddr)
        (HashSet(recLoc), st1.heap.update(recLoc, obj), st1.context)
      } else {
        (LocSetEmpty, Heap.Bot, Context.Bot)
      }
    val (locSet2, st3) =
      if (!locSet.isEmpty) (locSet, st)
      else (LocSetEmpty, State.Bot)

    (Value(PValue.Bot(utils), locSet1 ++ locSet2), State(h2, ctx2) + st3, excSet)
  }

  def typeTag(h: Heap, value: Value): AbsString = {
    val s1 = value.pvalue.undefval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("undefined")
    })
    val s2 = value.pvalue.nullval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("object") //TODO: check null type?
    })
    val s3 = value.pvalue.numval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("number")
    })
    val s4 = value.pvalue.boolval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("boolean")
    })
    val s5 = value.pvalue.strval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("string")
    })

    val isCallableLocSet = value.locset.foldLeft(utils.absBool.Bot)((tmpAbsB, l) => tmpAbsB + h.isCallable(l)(utils))
    val s6 =
      if (!value.locset.isEmpty && (utils.absBool.False <= isCallableLocSet))
        utils.absString.alpha("object")
      else utils.absString.Bot
    val s7 =
      if (!value.locset.isEmpty && (utils.absBool.True <= isCallableLocSet))
        utils.absString.alpha("function")
      else utils.absString.Bot

    s1 + s2 + s3 + s4 + s5 + s6 + s7
  }
}
