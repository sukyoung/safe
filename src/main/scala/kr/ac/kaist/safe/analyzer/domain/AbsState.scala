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

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models.PredefLoc
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util.{ AllocSite, PredAllocSite }
import scala.collection.immutable.{ HashMap }

////////////////////////////////////////////////////////////////////////////////
// concrete state type
////////////////////////////////////////////////////////////////////////////////
trait State // TODO

////////////////////////////////////////////////////////////////////////////////
// state abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsState extends AbsDomain[State, AbsState] {
  val heap: AbsHeap
  val context: AbsContext

  def raiseException(excSet: Set[Exception]): AbsState
  def oldify(asite: AllocSite): AbsState

  // Lookup
  def lookup(id: CFGId): (AbsValue, Set[Exception])
  def lookupBase(id: CFGId): AbsValue

  // Store
  def varStore(id: CFGId, value: AbsValue): AbsState

  // Update location
  def createMutableBinding(id: CFGId, value: AbsValue): AbsState

  // delete
  def delete(loc: Loc, str: String): (AbsState, AbsBool)

  // toString
  def toStringAll: String
  def toStringLoc(loc: Loc): Option[String]
}

trait AbsStateUtil extends AbsDomainUtil[State, AbsState] {
  def apply(heap: AbsHeap, context: AbsContext): AbsState
}

////////////////////////////////////////////////////////////////////////////////
// default state abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultState extends AbsStateUtil {
  lazy val Bot: AbsState = Dom(AbsHeap.Bot, AbsContext.Bot)
  lazy val Top: AbsState = Dom(AbsHeap.Top, AbsContext.Top)

  def alpha(st: State): AbsState = Top // TODO more precise

  def apply(heap: AbsHeap, context: AbsContext): AbsState = Dom(heap, context)

  case class Dom(
      heap: AbsHeap,
      context: AbsContext
  ) extends AbsState {
    def gamma: ConSet[State] = ConInf() // TODO more precise

    def getSingle: ConSingle[State] = ConMany() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsState): Boolean =
      this.heap <= that.heap && this.context <= that.context

    def +(that: AbsState): AbsState =
      Dom(this.heap + that.heap, this.context + that.context)

    def <>(that: AbsState): AbsState =
      Dom(this.heap <> that.heap, this.context <> that.context)

    def raiseException(excSet: Set[Exception]): AbsState = {
      if (excSet.isEmpty) Bot
      else {
        val (oldValue, _) = context.pureLocal.record.decEnvRec.GetBindingValue("@exception_all")
        val (newSt: AbsState, newExcSet: AbsLoc) = excSet.foldLeft((this, AbsLoc.Bot)) {
          case ((st, locSet), exc) => {
            val errModel = exc.getModel
            val errASite = PredAllocSite(errModel.name + "<instance>")
            val newSt = st.oldify(errASite)
            val loc = Loc(errASite, Recent)
            val (protoModel, _, _, _) = errModel.protoModel.get
            val newErrObj = AbsObject.newErrorObj(errModel.name, protoModel.loc)
            val retH = newSt.heap.update(loc, newErrObj)
            (Dom(retH, newSt.context), locSet + loc)
          }
        }
        val excValue = AbsValue(newExcSet)
        val localEnv = newSt.context.pureLocal
        val (envRec1, _) = localEnv.record.decEnvRec.SetMutableBinding("@exception", excValue)
        val (envRec2, _) = envRec1.SetMutableBinding("@exception_all", excValue + oldValue)
        val newCtx = newSt.context.subsPureLocal(localEnv.copyWith(record = envRec2))
        Dom(newSt.heap, newCtx)
      }
    }

    def oldify(asite: AllocSite): AbsState = {
      Dom(this.heap.oldify(asite), this.context.oldify(asite))
    }

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
          val collapsedEnv = context.getOrElse(PredefLoc.COLLAPSED, AbsLexEnv.Bot)
          collapsedEnv.record.decEnvRec.GetBindingValue(x)
        case GlobalVar => AbsGlobalEnvRec.Top.GetBindingValue(x, true)(heap)
      }
    }

    def lookupBase(id: CFGId): AbsValue = {
      val x = id.text
      id.kind match {
        case PureLocalVar => AbsLoc(PredefLoc.PURE_LOCAL)
        case CapturedVar =>
          AbsLexEnv.getIdBase(context.pureLocal.outer, x, false)(this)
        case CapturedCatchVar => AbsLoc(PredefLoc.COLLAPSED)
        case GlobalVar => AbsLoc(BuiltinGlobal.loc)
      }
    }

    ////////////////////////////////////////////////////////////////
    // Store
    ////////////////////////////////////////////////////////////////
    def varStore(id: CFGId, value: AbsValue): AbsState = {
      val x = id.text
      val localEnv = context.pureLocal
      id.kind match {
        case PureLocalVar =>
          val envRec = localEnv.record.decEnvRec
          val (newEnvRec, _) = envRec
            .CreateMutableBinding(x).fold(envRec)((e: AbsDecEnvRec) => e)
            .SetMutableBinding(x, value)
          val newEnv = localEnv.copyWith(record = newEnvRec)
          Dom(heap, context.subsPureLocal(newEnv))
        case CapturedVar =>
          val (newSt, _) = AbsLexEnv.setId(localEnv.outer, x, value, false)(this)
          newSt
        case CapturedCatchVar =>
          val env = context.getOrElse(PredefLoc.COLLAPSED, AbsLexEnv.Bot).record.decEnvRec
          val (newEnv, _) = env
            .CreateMutableBinding(x).fold(env)((e: AbsDecEnvRec) => e)
            .SetMutableBinding(x, value)
          Dom(heap, context.update(PredefLoc.COLLAPSED, AbsLexEnv(newEnv)))
        case GlobalVar =>
          val (_, newH, _) = AbsGlobalEnvRec.Top
            .SetMutableBinding(x, value, false)(heap)
          Dom(newH, context)
      }
    }

    ////////////////////////////////////////////////////////////////
    // Update location
    ////////////////////////////////////////////////////////////////
    def createMutableBinding(id: CFGId, value: AbsValue): AbsState = {
      val x = id.text
      id.kind match {
        case PureLocalVar =>
          val env = context.pureLocal
          val envRec = env.record.decEnvRec
          val (newEnvRec, _) = envRec
            .CreateMutableBinding(x).fold(envRec)((e: AbsDecEnvRec) => e)
            .SetMutableBinding(x, value)
          Dom(heap, context.subsPureLocal(env.copyWith(record = newEnvRec)))
        case CapturedVar =>
          val bind = AbsBinding(value)
          val newCtx = context.pureLocal.outer.foldLeft(AbsContext.Bot)((tmpCtx, loc) => {
            val env = context.getOrElse(loc, AbsLexEnv.Bot)
            val envRec = env.record.decEnvRec
            val (newEnvRec, _) = envRec
              .CreateMutableBinding(x).fold(envRec)((e: AbsDecEnvRec) => e)
              .SetMutableBinding(x, value)
            tmpCtx + context.update(loc, env.copyWith(record = newEnvRec))
          })
          Dom(heap, newCtx)
        case CapturedCatchVar =>
          val collapsedLoc = PredefLoc.COLLAPSED
          val env = context.getOrElse(collapsedLoc, AbsLexEnv.Bot)
          val envRec = env.record.decEnvRec
          val (newEnvRec, _) = envRec
            .CreateMutableBinding(x).fold(envRec)((e: AbsDecEnvRec) => e)
            .SetMutableBinding(x, value)
          Dom(heap, context.update(collapsedLoc, env.copyWith(record = newEnvRec)))
        case GlobalVar =>
          val globalLoc = BuiltinGlobal.loc
          val objV = AbsDataProp(value, AbsBool.True, AbsBool.True, AbsBool.False)
          val newHeap =
            if (AbsBool.True == heap.get(globalLoc).HasProperty(AbsString(x), heap)) heap
            else heap.update(globalLoc, heap.get(globalLoc).update(x, objV))
          Dom(newHeap, context)
      }
    }

    ////////////////////////////////////////////////////////////////
    // delete
    ////////////////////////////////////////////////////////////////
    def delete(loc: Loc, str: String): (AbsState, AbsBool) = {
      val absStr = AbsString(str)
      val (newHeap, b1) = heap.delete(loc, absStr)
      val (newCtx, b2) = context.delete(loc, str)
      (Dom(newHeap, newCtx), b1 + b2)
    }

    override def toString: String = toString(false)

    def toStringAll: String = toString(true)

    def toStringLoc(loc: Loc): Option[String] = heap.toStringLoc(loc) match {
      case None => context.toStringLoc(loc)
      case some => some
    }

    private def toString(all: Boolean): String = {
      "** heap **" + LINE_SEP +
        (if (all) heap.toStringAll else heap.toString) + LINE_SEP +
        LINE_SEP +
        "** context **" + LINE_SEP +
        context.toString + LINE_SEP +
        LINE_SEP +
        "** old allocation site set **" + LINE_SEP +
        context.old.toString
    }
  }
}
