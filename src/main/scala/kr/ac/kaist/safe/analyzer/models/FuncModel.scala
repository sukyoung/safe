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

// Function Model
class FuncModel(
    override val name: String,
    override val props: List[PropDesc] = Nil,
    val code: Code = EmptyCode(),
    val construct: Option[Code] = None,
    val protoModel: Option[(ObjModel, Boolean, Boolean, Boolean)] = None
) extends ObjModel(name, props) {
  override def initHeap(h: Heap, cfg: CFG): Heap = h(loc) match {
    case Some(_) => h
    case None => {
      val AT = AbsBool.True
      val AF = AbsBool.False
      val AB = AbsBool.Bot
      def toAB(b: Boolean): AbsBool = AbsBool.alpha(b)

      // [model].prototype object
      val (h1, protoOpt, writable, enumerable, configurable) = protoModel match {
        case Some((objModel, writable, enumerable, configurable)) => {
          (h(objModel.loc) match {
            case Some(_) => h
            case None => {
              val heap = objModel.initHeap(h, cfg)
              heap.getOrElse(objModel.loc)(Heap.Bot)(obj => {
                heap.update(objModel.loc, obj.update(
                  "constructor",
                  PropValue(DataProperty(ValueUtil(loc), AT, AF, AT))
                ))
              })
            }
          }, Some(objModel.loc), toAB(writable), toAB(enumerable), toAB(configurable))
        }
        case None => (h, None, AB, AB, AB)
      }

      // [model] function object
      val func = code.getCFGFunc(cfg, name)
      val fidOpt = Some(func.id)
      val constructIdOpt = construct.map(_.getCFGFunc(cfg, name).id)
      val scope = ValueUtil.alpha(null) // TODO get scope as args
      val n = AbsNumber.alpha(code.argLen)
      val funcObj = AbsObjectUtil.newFunctionObject(
        fidOpt,
        constructIdOpt,
        scope,
        protoOpt,
        writable,
        enumerable,
        configurable,
        n
      )
      initObj(h1, cfg, loc, funcObj, props)
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
