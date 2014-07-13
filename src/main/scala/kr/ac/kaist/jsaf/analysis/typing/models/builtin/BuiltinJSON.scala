/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.builtin

import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.bug_detector.JSONStringify
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object BuiltinJSON extends ModelData {

  val ConstLoc = newSystemLoc("JSONConst", Recent)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("JSON")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("constructor",              AbsConstValue(PropValue(ObjectValue(Value(BuiltinObject.ConstLoc), F, F, F)))),
    ("parse",     AbsBuiltinFunc("JSON.parse",     2)), // the value of length property is from Chrome browser.
    ("stringify", AbsBuiltinFunc("JSON.stringify", 3))  // the value of length property is from Chrome browser.
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (ConstLoc, prop_const)
  )

//  class StringifyState(_indent: String, _gap: String, _propertyList: List[String]) {
//    var indent: String = _indent
//    var gap: String = _gap
//    var propertyList: List[String] = _propertyList
//    val stack: MStack[Loc] = MStack.empty
//  }
//
//  def quote(string: String): String = {
//    val product = new StringBuffer(string.length() + 2) // two extra chars for " on either side
//    product.append('"')
//    val length = string.length()
//
//    (0 to length-1).foreach(i => {
//      val c = string.charAt(i)
//      c match {
//        case '"' => product.append("\\\"")
//        case '\\' => product.append("\\\\")
//        case '\b' => product.append("\\b")
//        case '\f' => product.append("\\f")
//        case '\n' => product.append("\\n")
//        case '\r' => product.append("\\r")
//        case '\t' => product.append("\\t")
//        case _ => {
//          if (c < ' ') {
//            product.append("\\u");
//            val hex = String.format("%04x", c.toInt)
//            product.append(hex)
//          } else {
//            product.append(c)
//          }
//        }
//      }
//    })
//    product.append('"').toString
//  }
//
//  abstract class CValue
//  case class CNumber(v: Long) extends CValue {
//    override def toString: String = v.toString
//  }
//  case class CString(v: String) extends CValue {
//    override def toString: String = v
//  }
//  case object CUndefined extends CValue {
//    override def toString: String = "undefined"
//  }
//  case object CObject extends CValue {
//    override def toString: String = throw new InternalError("must not call toString of Obj")
//  }
//
//  def getConcreteValue(h: Heap, pv: (PropValue, Absent)): CValue = {
//    // TODO
//    if (AbsentTop <= pv._2) throw new InternalError("abstracted")
//    CUndefined
//  }
//
//  private def join(objs: List[String], delimiter: String): String = {
//    if (objs == null) ""
//    objs.mkString(delimiter)
//  }
//
//  private def ja(h: Heap, l_array: Loc, state: StringifyState): String = {
//    if (state.stack.search(l_array) != -1)
//      throw new InternalError("detect cycle")
//
//    val o = h(l_array)
//
//    state.stack.push(l_array)
//    val stepback: String = state.indent
//    state.indent = state.indent + state.gap
//    val partial: List[String] = new MLinkedList[String]()
//
//    val pv_len = o("length")
//    val len = getConcreteValue(pv) match {
//      case CNumber(v) => v
//      case _ =>  throw new InternalError("abstracted")
//    }
//    (0 to len - 1).foreach(index => {
//      val strP = str(h, index.toString, o, state)
//      strP match {
//        case CUndefined => partial.add("null")
//        case _ => partial.add(strP.toString)
//      }
//    })
//
//    var finalValue: String = ""
//    if (partial.isEmpty) {
//      finalValue = "[]"
//    } else {
//      if (state.gap.length() == 0) {
//        finalValue = '[' + join(partial, ",") + ']'
//      } else {
//        val sep = ",\n" + state.indent
//        val prop = join(partial, sep)
//        finalValue = "[\n" + state.indent + prop + '\n' + stepback + ']'
//      }
//    }
//
//    state.stack.pop()
//    state.indent = stepback
//    finalValue
//  }
//
//  private def str(h: Heap, key: String, o: Obj, state: StringifyState): CValue = {
//    val cv = getConcreteValue(h, o(key))
//
//  }

  def detectCycle(h: Heap, l: Loc): Boolean = {
    def detectCycle_(l: Loc, visited: LocSet): Unit = {
      val o = h(l)
      val s_set = o.getProps.filter(s => BoolTrue <= o(s)._1._1._3)
      s_set.foreach(s => {
        val lset = o(s)._1._1._1._2
        if (!visited.intersect(lset).isEmpty) {
          throw new InternalError("cycle detected")
        }
        lset.foreach(l => detectCycle_(l, visited + l))
      })
    }

    try {
      detectCycle_(l, LocSetBot)
      false
    } catch {
      case _: InternalError => true
    }
  }

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("JSON.parse" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val value = JSONValueTop
          ((Helper.ReturnStore(h, value), ctx), (he, ctxe))
        })),
      "JSON.stringify" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 = getArgValue(h, ctx, args, "1")
          
          val lset_callee = getArgValue(h, ctx, args, "callee")._2
          val abstraction = (lset_callee.size > 1)

          val lset_fn = v_2._2.filter(l => BoolTrue <= Helper.IsCallable(h, l))
          if (!lset_fn.isEmpty) System.err.println("* Warning: Semantics of the API function 'JSON.stringify(value, replacer)' is not defined.")

          val lset = v_1._2
          val cycle = lset.toSet.find(l => detectCycle(h, l))

          val value = Value(StrTop) + Value(UndefTop)

          val es = cycle match {
                     case Some(l) =>
                       if (Config.typingInterface != null)
                         if(Shell.params.opt_DeveloperMode || !abstraction)
                           Config.typingInterface.signal(Config.typingInterface.getSpan, JSONStringify, null, null)
                       Set[Exception](TypeError)
                     case _ => ExceptionBot
                   }

          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)

          ((Helper.ReturnStore(h, value), ctx), (he + h_e, ctxe + ctx_e))
        })
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("JSON.parse" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val value = JSONValueTop
          ((PreHelper.ReturnStore(h, cfg.getPureLocal(cp), value), ctx), (he, ctxe))
        })),
      "JSON.stringify" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val value = Value(StrTop) + Value(UndefTop)
          val es = Set[Exception](TypeError)

          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)

          ((PreHelper.ReturnStore(h, cfg.getPureLocal(cp), value), ctx), (he + h_e, ctxe + ctx_e))
        })
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("JSON.parse" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      "JSON.stringify" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val es = Set[Exception](TypeError)

          val LP_1 = AH.RaiseException_def(es)
          LP_1 ++ LPSet((SinglePureLocalLoc, "@return"))
        })
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("JSON.parse" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      "JSON.stringify" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 = getArgValue(h, ctx, args, "1")
          val es = Set[Exception](TypeError)

          val LP_1 = getArgValue_use(h, ctx, args, "0")
          val LP_2 = getArgValue_use(h, ctx, args, "0")
          val LP_3 = v_2._2.foldLeft(LPBot)((S, l) => S ++ AH.IsCallable_use(h, l))
          val LP_4 = v_1._2.foldLeft(LPBot)((S, l) => S ++ AH.DetectCycle_use(h, l))
          val LP_5 = AH.RaiseException_use(es)
          LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LPSet((SinglePureLocalLoc, "@return"))
        })
    )
  }
}
