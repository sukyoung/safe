/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.CallInfo
import kr.ac.kaist.safe.analyzer.model._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util.PipeOps._
import kr.ac.kaist.safe.util._

import spray.json._

// default state abstract domain
object DefaultState extends StateDomain {
  lazy val Bot: Elem = Elem(AbsHeap.Bot, AbsContext.Bot, AllocLocSet.Bot)
  lazy val Top: Elem = Elem(AbsHeap.Top, AbsContext.Top, AllocLocSet.Top)

  def alpha(st: State): Elem = Top // TODO more precise

  def apply(heap: AbsHeap, context: AbsContext, allocs: AllocLocSet): Elem =
    Elem(heap, context, allocs)

  case class Elem(
      heap: AbsHeap,
      context: AbsContext,
      allocs: AllocLocSet
  ) extends ElemTrait {
    def gamma: ConSet[State] = ConInf // TODO more precise

    def getSingle: ConSingle[State] = ConMany // TODO more precise

    def ⊑(that: Elem): Boolean =
      this.heap ⊑ that.heap && this.context ⊑ that.context

    def ⊔(that: Elem): Elem =
      Elem(this.heap ⊔ that.heap, this.context ⊔ that.context, this.allocs ⊔ that.allocs)

    def ⊓(that: Elem): Elem =
      Elem(this.heap ⊓ that.heap, this.context ⊓ that.context, this.allocs ⊓ that.allocs)

    def raiseException(excSet: Set[Exception]): Elem = {
      if (excSet.isEmpty) Bot
      else {
        val (oldValue, _) = context.pureLocal.record.decEnvRec.GetBindingValue("@exception_all")
        val (newSt: Elem, newExcSet) = excSet.foldLeft[(Elem, LocSet)]((this, LocSet.Bot)) {
          case ((st, locSet), exc) => {
            val errLoc = Loc(s"$exc<instance>")
            val newSt = st.alloc(errLoc)
            val newErrObj = AbsObj.newErrorObj(exc.toString, Loc(s"$exc.prototype"))
            val retH = newSt.heap.update(errLoc, newErrObj)
            (newSt.copy(heap = retH), locSet + errLoc)
          }
        }
        val excValue = AbsValue(newExcSet)
        val localEnv = newSt.context.pureLocal
        val (envRec1, _) = localEnv.record.decEnvRec.SetMutableBinding("@exception", excValue)
        val (envRec2, _) = envRec1.SetMutableBinding("@exception_all", excValue ⊔ oldValue)
        val newCtx = newSt.context.subsPureLocal(localEnv.copy(record = envRec2))
        newSt.copy(context = newCtx)
      }
    }

    def remove(locs: Set[Loc]): Elem = {
      Elem(
        heap.remove(locs),
        context.remove(locs),
        allocs.remove(locs)
      )
    }

    def subsLoc(from: Loc, to: Loc): Elem = {
      Elem(
        heap.subsLoc(from, to),
        context.subsLoc(from, to),
        allocs.subsLoc(from, to)
      )
    }

    def oldify(loc: Loc): Elem = loc match {
      case locR @ Recency(l, Recent) => {
        val locO = Recency(l, Old)
        subsLoc(locR, locO)
      }
      case _ => this
    }

    def alloc(loc: Loc): Elem = {
      val Elem(heap, context, allocs) = oldify(loc)
      val newHeap = heap.alloc(loc)
      val newCtxt = context.alloc(loc)
      val newAllocs = allocs.alloc(loc)
      Elem(newHeap, newCtxt, newAllocs)
    }

    def setAllocLocSet(allocs: AllocLocSet): Elem = copy(allocs = allocs)

    def getLocSet: LocSet =
      heap.getLocSet ⊔ context.getLocSet ⊔ allocs.mayAlloc ⊔ allocs.mustAlloc

    ////////////////////////////////////////////////////////////////
    // Lookup
    ////////////////////////////////////////////////////////////////
    def lookup(id: CFGId): (AbsValue, Set[Exception]) = {
      val x = id.text
      val localEnv = context.pureLocal
      id.kind match {
        case PureLocalVar =>
          localEnv.record.decEnvRec.GetBindingValue(x)
        case CapturedVar =>
          AbsLexEnv.getId(localEnv.outer, x, true)(this)
        case CapturedCatchVar =>
          val collapsedEnv = context.getOrElse(COLLAPSED, AbsLexEnv.Bot)
          collapsedEnv.record.decEnvRec.GetBindingValue(x)
        case GlobalVar => AbsGlobalEnvRec.Top.GetBindingValue(x, true)(heap)
      }
    }

    def lookupBase(id: CFGId): AbsValue = {
      val x = id.text
      id.kind match {
        case PureLocalVar => LocSet(PURE_LOCAL)
        case CapturedVar =>
          AbsLexEnv.getIdBase(context.pureLocal.outer, x, false)(this)
        case CapturedCatchVar => LocSet(COLLAPSED)
        case GlobalVar => LocSet(GLOBAL_LOC)
      }
    }

    ////////////////////////////////////////////////////////////////
    // Store
    ////////////////////////////////////////////////////////////////
    def varStore(id: CFGId, value: AbsValue): Elem = {
      val x = id.text
      val localEnv = context.pureLocal
      id.kind match {
        case PureLocalVar =>
          val envRec = localEnv.record.decEnvRec
          val (newEnvRec, _) = envRec
            .CreateMutableBinding(x).fold(envRec)((e: AbsDecEnvRec) => e)
            .SetMutableBinding(x, value)
          val newEnv = localEnv.copy(record = newEnvRec)
          copy(context = context.subsPureLocal(newEnv))
        case CapturedVar =>
          val (newSt, _) = AbsLexEnv.setId(localEnv.outer, x, value, false)(this)
          newSt
        case CapturedCatchVar =>
          val env = context.getOrElse(COLLAPSED, AbsLexEnv.Bot).record.decEnvRec
          val (newEnv, _) = env
            .CreateMutableBinding(x).fold(env)((e: AbsDecEnvRec) => e)
            .SetMutableBinding(x, value)
          copy(context = context.weakUpdate(COLLAPSED, AbsLexEnv(newEnv)))
        case GlobalVar =>
          val (_, newH, _) = AbsGlobalEnvRec.Top
            .SetMutableBinding(x, value, false)(heap)
          copy(heap = newH)
      }
    }

    ////////////////////////////////////////////////////////////////
    // Update location
    ////////////////////////////////////////////////////////////////
    def createMutableBinding(id: CFGId, value: AbsValue): Elem = {
      val x = id.text
      id.kind match {
        case PureLocalVar =>
          val env = context.pureLocal
          val envRec = env.record.decEnvRec
          val (newEnvRec, _) = envRec
            .CreateMutableBinding(x).fold(envRec)((e: AbsDecEnvRec) => e)
            .SetMutableBinding(x, value)
          copy(context = context.subsPureLocal(env.copy(record = newEnvRec)))
        case CapturedVar =>
          val bind = AbsBinding(value)
          val newCtx = context.pureLocal.outer.foldLeft[AbsContext](AbsContext.Bot)((tmpCtx, loc) => {
            val env = context.getOrElse(loc, AbsLexEnv.Bot)
            val envRec = env.record.decEnvRec
            val (newEnvRec, _) = envRec
              .CreateMutableBinding(x).fold(envRec)((e: AbsDecEnvRec) => e)
              .SetMutableBinding(x, value)
            tmpCtx ⊔ context.update(loc, env.copy(record = newEnvRec))
          })
          copy(context = newCtx)
        case CapturedCatchVar =>
          val collapsedLoc = COLLAPSED
          val env = context.getOrElse(collapsedLoc, AbsLexEnv.Bot)
          val envRec = env.record.decEnvRec
          val (newEnvRec, _) = envRec
            .CreateMutableBinding(x).fold(envRec)((e: AbsDecEnvRec) => e)
            .SetMutableBinding(x, value)
          copy(context = context.update(collapsedLoc, env.copy(record = newEnvRec)))
        case GlobalVar =>
          val globalLoc = GLOBAL_LOC
          val objV = AbsDataProp(value, AbsBool.True, AbsBool.True, AbsBool.False)
          val newHeap =
            if (AbsBool.True == heap.get(globalLoc).HasProperty(AbsStr(x), heap)) heap
            else heap.update(globalLoc, heap.get(globalLoc).update(x, objV))
          copy(heap = newHeap)
      }
    }

    ////////////////////////////////////////////////////////////////
    // delete
    ////////////////////////////////////////////////////////////////
    def delete(loc: Loc, str: String): (Elem, AbsBool) = {
      val absStr = AbsStr(str)
      val (newHeap, b1) = heap.delete(loc, absStr)
      val (newCtx, b2) = context.delete(loc, str)
      (Elem(newHeap, newCtx, allocs), b1 ⊔ b2)
    }

    override def toString: String = toString(false)

    def toStringAll: String = toString(true)

    def toStringLoc(loc: Loc): Option[String] = heap.toStringLoc(loc) match {
      case None => context.toStringLoc(loc)
      case some => some
    }

    def toJSON(implicit uomap: UIdObjMap): JsValue = JsObject(
      "heap" -> this.heap.toJSON,
      "context" -> this.context.toJSON
    )

    private def toString(all: Boolean): String = {
      "** heap **" + LINE_SEP +
        (if (all) heap.toStringAll else heap.toString) + LINE_SEP +
        LINE_SEP +
        "** context **" + LINE_SEP +
        context.toString + LINE_SEP +
        LINE_SEP +
        "** allocated location set **" + LINE_SEP +
        allocs.toString
    }
  }

  def fromJSON(
    json: JsValue,
    cfg: CFG,
    prev: AbsState
  )(implicit uomap: UIdObjMap): Elem = json match {
    case JsString(str) if (str == "__BOT__") => Bot
    case _ =>
      val fields = json.asJsObject().fields
      val locset = LocSet.fromJSON(fields("allocs"), cfg)
      Elem(
        AbsHeap.fromJSON(fields("heap"), cfg, prev, locset),
        AbsContext.fromJSON(fields("context"), cfg, prev, locset),
        prev.allocs.alloc(locset)
      )
  }
}
