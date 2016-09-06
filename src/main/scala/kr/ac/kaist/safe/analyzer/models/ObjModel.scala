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

package kr.ac.kaist.safe.analyzer.models

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util.{ Loc, SystemLoc, Recent }

// Object Model
class ObjModel(
    val name: String,
    val props: List[PropDesc] = Nil
) extends Model {
  val loc: Loc = SystemLoc(name, Recent)
  def init(h: Heap, cfg: CFG): (Heap, Value) =
    (initHeap(h, cfg), ValueUtil(loc))

  def initHeap(h: Heap, cfg: CFG): Heap = h(loc) match {
    case Some(_) => h
    case None => initObj(h, cfg, loc, AbsObjectUtil.newObject, props)
  }

  protected def initObj(
    h: Heap,
    cfg: CFG,
    loc: Loc,
    obj: Obj,
    ps: List[PropDesc]
  ): Heap = {
    ps.foldLeft((h, obj)) {
      case ((heap, obj), NormalProp(name, model, writable, enumerable, configurable)) => {
        (model match {
          case SelfModel => (heap, ValueUtil(loc))
          case _ => model.init(heap, cfg)
        }) match {
          case (heap, value) => (heap, obj.update(
            name,
            PropValue(DataProperty(
              value,
              AbsBool.alpha(writable),
              AbsBool.alpha(enumerable),
              AbsBool.alpha(configurable)
            ))
          ))
        }
      }
      case ((heap, obj), InternalProp(name, model)) => {
        (model match {
          case SelfModel => (heap, ValueUtil(loc))
          case _ => model.init(heap, cfg)
        }) match {
          case (heap, value) => (heap, obj.update(
            name,
            InternalValueUtil(value)
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
