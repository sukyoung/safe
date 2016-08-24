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

// Function Model
class FuncModel(
    override val name: String,
    override val props: List[PropDesc] = Nil,
    val code: Code = EmptyCode(),
    val hasConstruct: Boolean = F,
    val protoModel: Option[(ObjModel, Boolean, Boolean, Boolean)] = None
) extends ObjModel(name, props) {
  override def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap = {
    val AT = utils.absBool.True
    val AF = utils.absBool.False

    // [model].prototype object
    val (h1, protoOpt) = protoModel match {
      case Some((objModel, writable, enumerable, configurable)) => {
        (h(objModel.loc) match {
          case Some(_) => h
          case None =>
            val obj = Obj.newObject(utils)
              .update(
                "constructor",
                PropValue(ObjectValue(Value(loc)(utils), AT, AF, AT))
              )
            initObj(h, cfg, utils, objModel.loc, obj, objModel.props)
        }, Some(objModel.loc))
      }
      case None => (h, None)
    }

    // [model] function object
    val func = code.getCFGFunc(cfg, name, utils)
    val fidOpt = Some(func.id)
    val constructIdOpt = if (hasConstruct) Some(func.id) else None
    val scope = Value(PValue(utils.absNull.Top)(utils)) // TODO get scope as args
    val n = utils.absNumber.alpha(code.argLen)
    val funcObj = Obj.newFunctionObject(fidOpt, constructIdOpt, scope, protoOpt, n)(utils)
    initObj(h1, cfg, utils, loc, funcObj, props)
  }
}

object FuncModel {
  def apply(
    name: String,
    props: List[PropDesc] = Nil,
    code: Code = EmptyCode(),
    hasConstruct: Boolean = F,
    protoModel: Option[(ObjModel, Boolean, Boolean, Boolean)] = None
  ): FuncModel = new FuncModel(name, props, code, hasConstruct, protoModel)
}
