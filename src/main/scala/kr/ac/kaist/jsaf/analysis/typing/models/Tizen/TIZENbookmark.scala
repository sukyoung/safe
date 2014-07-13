/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context

object TIZENbookmark extends Tizen {
  val name = "bookmark"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_bookmark
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto)
  )
    /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T)))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("get", AbsBuiltinFunc("tizen.bookmark.get",2)),
    ("add", AbsBuiltinFunc("tizen.bookmark.add",2)),
    ("remove", AbsBuiltinFunc("tizen.bookmark.remove",1))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.bookmark.get" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))

          val (h_3, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              val parFld = getArgValue(h_2, ctx_2, args, "0")
              val (b, es) = TizenHelper.instanceOf(h_2, parFld, Value(TIZENBookmarkFolder.loc_cons))
              val es_1 =
                if (b._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h_2, es ++ es_1)
            case Some(n) if n >= 2 =>
              val parFld = getArgValue(h_2, ctx_2, args, "0")
              val recur = getArgValue(h_2, ctx_2, args, "1")
              val (b, es) = TizenHelper.instanceOf(h_2, parFld, Value(TIZENBookmarkFolder.loc_cons))
              val es_1 =
                if (b._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_2 =
                if (recur._1._3 </ BoolTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h_2, es ++ es_1 ++ es_2)
            case _ => {
              (h_2, TizenHelper.TizenExceptionBot)
            }
          }
          /* BookmarkItem */
          val o_bmitem = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENBookmarkItem.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("parent", PropValue(ObjectValue(Value(NullTop), F, T, T))).
            update("title", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("url", PropValue(ObjectValue(Value(StrTop), F, T, T)))
          /* BookmarkFolder */
          val o_bmfolder = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENBookmarkFolder.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("parent", PropValue(ObjectValue(Value(NullTop), F, T, T))).
            update("title", PropValue(ObjectValue(Value(StrTop), F, T, T)))

          val h_4 = h_3.update(l_r1, o_bmitem + o_bmfolder)
          val o_arr = Helper.NewArrayObject(UInt)
          val o_arr2 = o_arr.update("@default_number", PropValue(ObjectValue(l_r1, T, T, T)))
          val h_5 = h_4.update(l_r2, o_arr2)
          val est = Set[WebAPIException](NotFoundError, SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ est)
          ((Helper.ReturnStore(h_5, Value(l_r2)), ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.bookmark.add" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val bm = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val (b, es) = TizenHelper.instanceOf(h, bm, Value(TIZENBookmarkFolder.loc_cons + TIZENBookmarkItem.loc_cons))
          val es_1 =
            if (b._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_1, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val parFld = getArgValue(h, ctx, args, "1")
              val (b, es_3) = TizenHelper.instanceOf(h, parFld, Value(TIZENBookmarkFolder.loc_cons))
              val es_4 =
                if (b._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h, es_3 ++ es_4)
            case _ => {
              (h, TizenHelper.TizenExceptionBot)
            }
          }
          val est = Set[WebAPIException](NotFoundError, InvalidValuesError, SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ est)
          ((h_1, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.bookmark.remove" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val (h_1, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              val bm = getArgValue(h, ctx, args, "0")
              val (b, es_1) = TizenHelper.instanceOf(h, bm, Value(TIZENBookmarkFolder.loc_proto))
              val (b_1, es_2) = TizenHelper.instanceOf(h, bm, Value(TIZENBookmarkItem.loc_proto))
              val es_3 =
                if (b._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (b_1._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h, es_1 ++ es_2 ++ es_3 ++ es_4)
            case _ => {
              (h, TizenHelper.TizenExceptionBot)
            }
          }
          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h_1, ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }
  override def getDefMap(): Map[String, AccessFun] = {
    Map()
  }
  override def getUseMap(): Map[String, AccessFun] = {
    Map()
  }
}
