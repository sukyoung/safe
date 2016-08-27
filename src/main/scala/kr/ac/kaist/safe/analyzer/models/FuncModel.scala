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
    val construct: Option[Code] = None,
    val protoModel: Option[(ObjModel, Boolean, Boolean, Boolean)] = None
) extends ObjModel(name, props) {
  override def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap = h(loc) match {
    case Some(_) => h
    case None => {
      val AT = utils.absBool.True
      val AF = utils.absBool.False
      val AB = utils.absBool.Bot
      def toAB(b: Boolean): AbsBool = utils.absBool.alpha(b)

      // [model].prototype object
      val (h1, protoOpt, writable, enumerable, configurable) = protoModel match {
        case Some((objModel, writable, enumerable, configurable)) => {
          (h(objModel.loc) match {
            case Some(_) => h
            case None => {
              val heap = objModel.initHeap(h, cfg, utils)
              heap.getOrElse(objModel.loc)(Heap.Bot)(obj => {
                heap.update(objModel.loc, obj.update(
                  "constructor",
                  PropValue(DataProperty(utils.value(loc), AT, AF, AT))
                ))
              })
            }
          }, Some(objModel.loc), toAB(writable), toAB(enumerable), toAB(configurable))
        }
        case None => (h, None, AB, AB, AB)
      }

      // [model] function object
      val func = code.getCFGFunc(cfg, name, utils)
      val fidOpt = Some(func.id)
      val constructIdOpt = construct.map(_.getCFGFunc(cfg, name, utils).id)
      val scope = utils.value.alpha(null) // TODO get scope as args
      val n = utils.absNumber.alpha(code.argLen)
      val funcObj = Obj.newFunctionObject(
        fidOpt,
        constructIdOpt,
        scope,
        protoOpt,
        writable,
        enumerable,
        configurable,
        n
      )(utils)
      initObj(h1, cfg, utils, loc, funcObj, props)
    }
  }
}

object FuncModel {
  def apply(
    name: String,
    props: List[PropDesc] = Nil,
    code: Code = EmptyCode(),
    construct: Option[Code] = None,
    protoModel: Option[(ObjModel, Boolean, Boolean, Boolean)] = None
  ): FuncModel = new FuncModel(name, props, code, construct, protoModel)
}
