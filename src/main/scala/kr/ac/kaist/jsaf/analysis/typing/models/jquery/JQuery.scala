/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.models.jquery

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.{HTMLTopElement, HTMLDocument}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.{DOMElement, DOMNodeList}

object JQuery extends ModelData {
  val ConstLoc = newSystemLoc("jQueryConst", Recent)
  val ProtoLoc = newSystemLoc("jQueryProto", Recent)
  val RootJQLoc = newSystemLoc("jQueryRoot", Recent)
  val EasingLoc = newSystemLoc("jQueryEasing", Recent)
  
  // only used internally 
  val EnvLoc = newSystemLoc("jQueryEnv", Recent)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("@scope",               AbsConstValue(PropValueNullTop)),
    ("@function",            AbsInternalFunc("jQuery")),
    ("@construct",           AbsInternalFunc("jQuery.constructor")),
    ("@hasinstance",         AbsConstValue(PropValueNullTop)),
    ("prototype",            AbsConstValue(PropValue(ObjectValue(Value(ProtoLoc), F, F, F)))),
    ("length",               AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(2), F, F, F)))),
    ("fn",                   AbsConstValue(PropValue(ObjectValue(Value(ProtoLoc), T, T, T)))),
    ("easing",               AbsConstValue(PropValue(ObjectValue(Value(EasingLoc), T, T, T))))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(BoolTrue))),
    ("constructor",          AbsConstValue(PropValue(ObjectValue(ConstLoc, F, F, F)))),
    ("jquery",               AbsConstValue(PropValue(ObjectValue(StrTop, F, F, F))))
  )

  private val prop_global: List[(String, AbsProperty)] = List(
    ("jQuery",  AbsConstValue(PropValue(ObjectValue(Value(ConstLoc), F, F, F)))),
    ("$",       AbsConstValue(PropValue(ObjectValue(Value(ConstLoc), F, F, F))))
  )

  private val prop_rootjq: List[(String, AbsProperty)] = List(
    ("@class",       AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",       AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",  AbsConstValue(PropValue(BoolTrue))),
    ("0",            AbsConstValue(PropValue(ObjectValue(HTMLDocument.GlobalDocumentLoc, T, T, T)))),
    ("context",      AbsConstValue(PropValue(ObjectValue(HTMLDocument.GlobalDocumentLoc, T, T, T)))),
    ("length",       AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0), T, T, T))))
  )
  private val prop_easing: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("linear",      AbsBuiltinFunc("jQuery.easing.linear", 1)),
    ("swing",       AbsBuiltinFunc("jQuery.easing.swing", 1))
  )
  
  private val prop_env: List[(String, AbsProperty)] = List(
    ("_$", AbsConstValue(PropValue(ObjectValue(Value(UndefTop), T, T, T)))),
    ("_jQuery", AbsConstValue(PropValue(ObjectValue(Value(UndefTop), T, T, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (ConstLoc, prop_const), (ProtoLoc, prop_proto),
    (RootJQLoc, prop_rootjq), (EasingLoc, prop_easing), (EnvLoc, prop_env)
  ) 

  private val reg_quick = """^(?:[^#<]*(<[\w\W]+>)[^>]*$|#([\w\-]*)$)""".r

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "jQuery" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)

          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)

          // start here
          val l_jq = addrToLoc(addr1, Recent)
          val l_tag = addrToLoc(addr2, Recent)
          val l_child = addrToLoc(addr3, Recent)

          /* arguments :  selector, context */
          val v_selector = getArgValue(h_3, ctx_3, args, "0")
          val v_context = getArgValue(h_3, ctx_3, args, "1")

          val (h_ret, v_ret) = JQueryHelper.init(h_3, v_selector, v_context, l_jq, l_tag, l_child)

          if (v_ret </ ValueBot)
            ((Helper.ReturnStore(h_ret, v_ret), ctx_3), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      ("jQuery.easing.linear" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          if (v_arg </ ValueBot)
            ((Helper.ReturnStore(h, v_arg), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.easing.swing" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val n_num = Helper.toNumber(Helper.toPrimitive_better(h, v_arg))
          val pi = 3.1415926535897932
          val v_ret = n_num.getAbsCase match {
            case AbsBot => Value(NumBot)
            case AbsSingle => AbsNumber.getUIntSingle(n_num) match {
              case Some(n) => Value(AbsNumber.alpha(scala.math.cos(n*pi)/2))
              case _ => Value(NaN)
            }
            case _ if AbsNumber.isInfinity(n_num) => Value(NaN)
            case _ => Value(NumTop)
          }

          if (v_ret </ ValueBot)
            ((Helper.ReturnStore(h, v_ret), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map()
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map()
  }
}
