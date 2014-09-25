/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.models.jquery

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper => AH, _}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue

object JQueryData extends ModelData {

  private val jquery_expando = "jQuery00000000000000000000"
  val CacheLoc = newSystemLoc("jQueryCache", Recent)
  val CacheDataLoc = newSystemLoc("jQueryCacheData", Old)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("data",        AbsBuiltinFunc("jQuery.data", 4)),
    ("dequeue",     AbsBuiltinFunc("jQuery.dequeue", 2)),
    ("hasData",     AbsBuiltinFunc("jQuery.hasData", 1)),
    ("queue",       AbsBuiltinFunc("jQuery.queue", 3)),
    ("removeData",  AbsBuiltinFunc("jQuery.removeData", 3)),
    // property
    ("cache",       AbsConstValue(PropValue(ObjectValue(CacheLoc, T, T, T)))),
    ("guid",        AbsConstValue(PropValue(ObjectValue(UInt, T, T, T))))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("cleareQueue", AbsBuiltinFunc("jQuery.prototype.cleareQueue", 1)),
    ("data",        AbsBuiltinFunc("jQuery.prototype.data", 2)),
    ("dequeue",     AbsBuiltinFunc("jQuery.prototype.dequeue", 1)),
    ("queue",       AbsBuiltinFunc("jQuery.prototype.queue", 2)),
    ("removeData",  AbsBuiltinFunc("jQuery.prototype.removeData", 1))
  )

  private val prop_cache: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    // weak update
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(CacheDataLoc, T, T, T))))
  )

  private val prop_cache_data: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ConstLoc, prop_const), (JQuery.ProtoLoc, prop_proto),
    (CacheLoc, prop_cache), (CacheDataLoc, prop_cache_data)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "jQuery.prototype.data" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* new loc */
          val l_ret = addrToLoc(addr1, Recent)

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* arguments */
          var v_key = getArgValue(h, ctx, args, "0")
          var v_value = getArgValue(h, ctx, args, "1")

          // no arguements
          val (h_ret1, ctx_ret1, v_ret1) =
            if (UndefTop <= v_key._1._1) {
              val v_len = lset_this.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h, l, AbsString.alpha("length")))
              val (h_1, ctx_1, v_1) =
                if (BoolTrue </ Helper.toBoolean(v_len)) {
                  val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
                  val h_2 = h_1.update(l_ret, h_1(CacheDataLoc))
                  (h_2, ctx_1, Value(l_ret))
                }
                else
                  (HeapBot, ContextBot, ValueBot)
              val v_2 =
                if (BoolFalse </ Helper.toBoolean(v_len))
                  Value(NullTop)
                else
                  ValueBot
              (h_1, ctx_1, v_1 + v_2)
            }
            else
              (HeapBot, ContextBot, ValueBot)

          // 1st argument is object
          val (h_ret2, v_ret2) =
            if (!v_key._2.isEmpty) {
              val _h = v_key._2.foldLeft(h)((_h, l) => {
                val _h1 = _h(l).getProps.foldLeft(_h)((hh, prop) =>
                  Helper.PropStore(hh, CacheDataLoc, AbsString.alpha(prop), hh(l)(prop)._1._1))
                val o_data = _h1(CacheDataLoc)
                val v_def_num = _h1(l)(Str_default_number)
                val v_def_oth = _h1(l)(Str_default_other)
                val o_data1 = o_data.update(NumStr, v_def_num + o_data(NumStr))
                  .update(OtherStr, v_def_oth + o_data(OtherStr))
                _h1.update(CacheDataLoc, o_data1)
              })
              (_h, Value(lset_this))
            }
            else
              (HeapBot, ValueBot)

          // one argument, 1st argument ia string
          val (h_ret3, v_ret3) =
            if (v_key._1._5 </ StrBot && UndefTop <= v_value._1._1)
              (h, Helper.Proto(h, CacheDataLoc, v_key._1._5))
            else
              (HeapBot, ValueBot)

          // two arguments
          val (h_ret4, v_ret4) =
            if (v_key._1._5 </ StrBot && v_value </ ValueBot)
              (Helper.PropStore(h, CacheDataLoc, v_key._1._5, v_value), Value(lset_this))
            else
              (HeapBot, ValueBot)

          val h_ret = h_ret1 + h_ret2 + h_ret3 + h_ret4
          val v_ret = v_ret1 + v_ret2 + v_ret3 + v_ret4
          val ctx_ret = ctx_ret1 + ctx
          if (v_ret </ ValueBot)
            ((Helper.ReturnStore(h_ret, v_ret), ctx_ret), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      ("jQuery.prototype.removeData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          // do nothing
          ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
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
