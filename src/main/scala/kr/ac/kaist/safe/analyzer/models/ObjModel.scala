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
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util.{ Loc, SystemLoc, Recent }

// Object Model
class ObjModel(
    val name: String,
    val props: List[PropDesc] = Nil
) extends Model {
  val loc: Loc = SystemLoc(name, Recent)
  def init(h: Heap, cfg: CFG, utils: Utils): (Heap, Value) =
    (initHeap(h, cfg, utils), utils.value(loc))

  def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap = h(loc) match {
    case Some(_) => h
    case None => initObj(h, cfg, utils, loc, utils.absObject.newObject, props)
  }

  protected def initObj(
    h: Heap,
    cfg: CFG,
    utils: Utils,
    loc: Loc,
    obj: Obj,
    ps: List[PropDesc]
  ): Heap = {
    ps.foldLeft((h, obj)) {
      case ((heap, obj), (name, model, writable, enumerable, configurable)) => {
        (model match {
          case SelfModel => (heap, utils.value(loc))
          case _ => model.init(heap, cfg, utils)
        }) match {
          case (heap, value) => (heap, obj.update(
            name,
            PropValue(DataProperty(
              value,
              utils.absBool.alpha(writable),
              utils.absBool.alpha(enumerable),
              utils.absBool.alpha(configurable)
            ))
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
