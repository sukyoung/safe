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
import scala.collection.immutable.HashSet

abstract class Model {
  def init(h: Heap, cfg: CFG, utils: Utils): (Heap, Value)
}

class PrimModel(
    val pvGen: Utils => PValue
) extends Model {
  def init(h: Heap, cfg: CFG, utils: Utils): (Heap, Value) =
    (h, Value(pvGen(utils)))
}

object PrimModel {
  def apply(pvGen: Utils => PValue): PrimModel = new PrimModel(pvGen)
  def apply(n: Double): PrimModel =
    PrimModel(utils => PValue(utils.absNumber.alpha(n))(utils))
  def apply(str: String): PrimModel =
    PrimModel(utils => PValue(utils.absString.alpha(str))(utils))
  def apply(): PrimModel =
    PrimModel(utils => PValue(utils.absUndef.alpha)(utils))
  def apply(x: Null): PrimModel =
    PrimModel(utils => PValue(utils.absNull.alpha)(utils))
  def apply(b: Boolean): PrimModel =
    PrimModel(utils => PValue(utils.absBool.alpha(b))(utils))
}

class ProtoModel(
    val funcModel: FuncModel
) extends Model {
  def init(h: Heap, cfg: CFG, utils: Utils): (Heap, Value) =
    (funcModel.initHeap(h, cfg, utils), Value(funcModel.protoLoc)(utils))
}

object ProtoModel {
  def apply(funcModel: FuncModel): ProtoModel = new ProtoModel(funcModel)
}

class ObjModel(
    val name: String,
    val props: List[(String, Model, Boolean, Boolean, Boolean)] = Nil
) extends Model {
  val loc: Loc = SystemLoc(name, Recent)
  def init(h: Heap, cfg: CFG, utils: Utils): (Heap, Value) = (h(loc) match {
    case Some(_) => h
    case None => initHeap(h, cfg, utils)
  }, Value(loc)(utils))

  def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap =
    initObj(h, cfg, utils, loc, Obj.newObject(utils), props)

  protected def initObj(
    h: Heap,
    cfg: CFG,
    utils: Utils,
    loc: Loc,
    obj: Obj,
    ps: List[(String, Model, Boolean, Boolean, Boolean)]
  ): Heap = {
    ps.foldLeft((h, obj)) {
      case ((heap, obj), (name, model, writable, enumerable, configurable)) => {
        (model match {
          case SelfModel => (heap, Value(loc)(utils))
          case _ => model.init(heap, cfg, utils)
        }) match {
          case (heap, value) => (heap, obj.update(
            name,
            PropValue(ObjectValue(
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
    props: List[(String, Model, Boolean, Boolean, Boolean)] = Nil
  ): ObjModel = new ObjModel(name, props)
}

class FuncModel(
    override val name: String,
    override val props: List[(String, Model, Boolean, Boolean, Boolean)] = Nil,
    val protoProps: List[(String, Model, Boolean, Boolean, Boolean)] = Nil,
    val prototypeWritable: Boolean = true,
    val argLen: Int = 0,
    val code: Code = EmptyCode
) extends ObjModel(name, props) {
  val protoLoc: Loc = SystemLoc(s"$name.prototype", Recent)
  override def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap = {
    val h0 = h
    val AT = utils.absBool.True
    val AF = utils.absBool.False

    // [model].prototype object
    val obj = Obj.newObject(utils)
      .update("constructor", PropValue(ObjectValue(Value(loc)(utils), AT, AF, AT)))
    val h1 = initObj(h0, cfg, utils, protoLoc, obj, protoProps)

    // [model] function object
    val func = code.getCFGFunc(cfg, name, utils)
    val scope = Value(PValue(utils.absNull.Top)(utils)) // TODO get scope as args
    val n = utils.absNumber.alpha(argLen)
    val funcObj = Obj.newFunctionObject(func.id, scope, protoLoc, n, utils.absBool.alpha(prototypeWritable))(utils)
    initObj(h1, cfg, utils, loc, funcObj, props)
  }

  val protoModel: ProtoModel = ProtoModel(this)
}

object FuncModel {
  def apply(
    name: String,
    props: List[(String, Model, Boolean, Boolean, Boolean)] = Nil,
    protoProps: List[(String, Model, Boolean, Boolean, Boolean)] = Nil,
    prototypeWritable: Boolean = true,
    argLen: Int = 0,
    code: Code = EmptyCode
  ): FuncModel = new FuncModel(name, props, protoProps, prototypeWritable, argLen, code)
}

class BuiltinFuncModel(
    override val name: String,
    override val props: List[(String, Model, Boolean, Boolean, Boolean)] = Nil,
    val argLen: Int = 0,
    val code: Code = EmptyCode
) extends ObjModel(name, props) {
  override def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap = {
    val h0 = h
    val AF = utils.absBool.False

    // [model] function object
    val func = code.getCFGFunc(cfg, name, utils)
    val n = utils.absNumber.alpha(argLen)
    val funcObj = Obj.newBuiltinFunctionObject(func.id, n)(utils)
    initObj(h0, cfg, utils, loc, funcObj, props)
  }
}

object BuiltinFuncModel {
  def apply(
    name: String,
    props: List[(String, Model, Boolean, Boolean, Boolean)] = Nil,
    argLen: Int = 0,
    code: Code = EmptyCode
  ): BuiltinFuncModel = new BuiltinFuncModel(name, props, argLen, code)
}

object SelfModel extends Model {
  def init(h: Heap, cfg: CFG, utils: Utils): (Heap, Value) = (h, Value.Bot(utils))
}
