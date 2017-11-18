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

package kr.ac.kaist.safe.analyzer.models

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._

// Object Model
class ObjModel(
    val name: String,
    val props: List[PropDesc] = Nil
) extends Model {
  val asite: PredAllocSite = PredAllocSite(name)
  val loc: Loc = Loc(asite)
  def init(h: AbsHeap, cfg: CFG): (AbsHeap, AbsValue) =
    (initHeap(h, cfg), AbsValue(loc))

  def initHeap(h: AbsHeap, cfg: CFG): AbsHeap = {
    if (h.get(loc).isBottom) {
      cfg.registerPredASite(asite)
      initObj(h, cfg, loc, AbsObj.newObject, props)
    } else h
  }

  protected def initObj(
    h: AbsHeap,
    cfg: CFG,
    loc: Loc,
    obj: AbsObj,
    ps: List[PropDesc]
  ): AbsHeap = {
    ps.foldLeft((h, obj)) {
      case ((heap, obj), NormalProp(name, model, writable, enumerable, configurable)) => {
        (model match {
          case SelfModel => (heap, AbsValue(loc))
          case _ => model.init(heap, cfg)
        }) match {
          case (heap, value) => (heap, obj.update(
            name,
            AbsDataProp(
              value,
              AbsBool(writable),
              AbsBool(enumerable),
              AbsBool(configurable)
            )
          ))
        }
      }
      case ((heap, obj), InternalProp(name, model)) => {
        (model match {
          case SelfModel => (heap, AbsValue(loc))
          case _ => model.init(heap, cfg)
        }) match {
          case (heap, value) => (heap, obj.update(
            name,
            AbsIValue(value)
          ))
        }
      }
    } match {
      case (heap, obj) => heap.update(loc, obj)
    }
  }
}

object ObjModel {
  def apply(
    name: String,
    props: List[PropDesc] = Nil
  ): ObjModel = new ObjModel(name, props)
}
