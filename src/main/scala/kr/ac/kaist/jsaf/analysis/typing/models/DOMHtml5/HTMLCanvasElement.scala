/*******************************************************************************
    Copyright (c) 2013-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml5

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import org.w3c.dom.Node
import org.w3c.dom.Element
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.HTMLElement
import kr.ac.kaist.jsaf.analysis.typing.{Semantics, ControlPoint, Helper, PreHelper}
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

object HTMLCanvasElement extends DOM {
  private val name = "HTMLCanvasElement"

  /* predefined locatoins */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_ins = newSystemRecentLoc(name + "Ins")

  /* constructor */
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* instance */
  private val prop_ins: List[(String, AbsProperty)] = 
       HTMLElement.getInsList2() ++ List(
      ("@class",    AbsConstValue(PropValue(AbsString.alpha("Object")))),
      ("@proto",    AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
      ("@extensible", AbsConstValue(PropValue(BoolTrue))),
      // DOM Level 1
      ("height", AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
      ("width", AbsConstValue(PropValue(ObjectValue(UInt, T, T, T))))
    )
  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(HTMLElement.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("toDataURL",   AbsBuiltinFunc("HTMLCanvasElement.toDataURL", 1)),
    ("toBlob",   AbsBuiltinFunc("HTMLCanvasElement.toBlob", 2)),
    ("getContext",   AbsBuiltinFunc("HTMLCanvasElement.getContext", 1))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = if(Shell.params.opt_Dommodel2) List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global), (loc_ins, prop_ins)

  ) else List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)  ) 

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //case "HTMLCanvasElement.toDataURL"
      //case "HTMLCanvasElement.toBlob"
      ("HTMLCanvasElement.getContext" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          // argument
          val context = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          // CanvasRenderingContext2D object location
          val loc_2d = CanvasRenderingContext2D.getInstance.get
          // the 'canvas' property points to this canvas element
          val obj_2d = h(loc_2d).update(AbsString.alpha("canvas"), PropValue(ObjectValue(Value(lset_this), F, T, T)))
          context.gamma match {
            // returns CanvasRenderingContext2D object
            case Some(vs) =>
              val (h_1, v) = if(vs.contains("2d")) {
                val h_1 = h.update(loc_2d, obj_2d)
                val v = Value(loc_2d) + (if(vs.size == 1) ValueBot else Value(NullTop))
                (h_1, v)
              }
              else (h, Value(NullTop))
              ((Helper.ReturnStore(h_1, v), ctx), (he, ctxe))
            case _ => context.getAbsCase match {
              // returns null
              case AbsMulti if context.isAllNums =>
                ((Helper.ReturnStore(h, Value(NullTop)), ctx), (he, ctxe))
              // returns (null join CavansRenderingContext2D object)
              case AbsTop | AbsMulti =>
                val h_1 = h.update(loc_2d, obj_2d)
                ((Helper.ReturnStore(h_1, Value(NullTop) + Value(loc_2d)), ctx), (he, ctxe))
              case AbsBot => ((HeapBot, ContextBot), (he, ctxe))
              case _ => throw new InternalError("impossible case.")
            }
          }
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //case "HTMLCanvasElement.toDataURL" => ((h, ctx), (he, ctxe))
      //case "HTMLCanvasElement.toBlob"  => ((h, ctx), (he, ctxe))
      ("HTMLCanvasElement.getContext" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          // argument
          val context=PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          // CanvasRenderingContext2D object location
          val loc_2d = CanvasRenderingContext2D.getInstance.get
          // the 'canvas' property points to this canvas element
          val obj_2d = h(loc_2d).update(AbsString.alpha("canvas"), PropValue(ObjectValue(Value(lset_this), F, T, T)))
          context.gamma match {
            // returns CanvasRenderingContext2D object
            case Some(vs) =>
              val (h_1, v) = if(vs.contains("2d")) {
                val h_1 = h.update(loc_2d, obj_2d)
                val v = Value(loc_2d) + (if(vs.size == 1) ValueBot else Value(NullTop))
                (h_1, v)
              }
              else (h, Value(NullTop))
              ((Helper.ReturnStore(h_1, v), ctx), (he, ctxe))
            case _ => context.getAbsCase match {
              // returns null
              case AbsMulti if context.isAllNums =>
                ((PreHelper.ReturnStore(h, PureLocalLoc, Value(NullTop)), ctx), (he, ctxe))
              // returns (null join CavansRenderingContext2D object)
              case AbsTop | AbsMulti =>
                val h_1 = h.update(loc_2d, obj_2d)
                ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(NullTop) + Value(loc_2d)), ctx), (he, ctxe))
              case AbsBot => ((h, ctx), (he, ctxe))
              case _ => throw new InternalError("impossible case.")
            }
          }
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //case "HTMLCanvasElement.toDataURL"
      //case "HTMLCanvasElement.toBlob"
      ("HTMLCanvasElement.getContext" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          // CanvasRenderingContext2D object location
          val loc_2d = CanvasRenderingContext2D.getInstance.get
          LPSet(Set((loc_2d, "canvas"), (SinglePureLocalLoc, "@return")))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //case "HTMLCanvasElement.toDataURL"
      //case "HTMLCanvasElement.toBlob"
      ("HTMLCanvasElement.getContext" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val loc_2d = CanvasRenderingContext2D.getInstance.get
          LP1 ++ LPSet(Set((loc_2d, "canvas"), (SinglePureLocalLoc, "@this"),(SinglePureLocalLoc, "@return")))
        }))
    )
  }




  /* instance */
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case e: Element =>
      // This object has all properties of the HTMLElement object
      HTMLElement.getInsList(node) ++ List(
        ("@class",    PropValue(AbsString.alpha("Object"))),
        ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
        ("@extensible", PropValue(BoolTrue)),
        // DOM Level 1
        ("width",     PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("width")))), T, T, T))),
        ("height",     PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("height")))), T, T, T))))
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }

  def getInsList(width: PropValue, height: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("width",    width),
    ("height",    height)
  )

  override def default_getInsList(): List[(String, PropValue)] =
  // This object has all properties of the HTMLElement object
    HTMLElement.default_getInsList ::: getInsList(PropValue(ObjectValue(NumTop, T, T, T)),
      PropValue(ObjectValue(NumTop, T, T, T))
    )

}
