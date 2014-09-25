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

object TIZENApplicationControl extends Tizen {
  val name = "ApplicationControl"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* constructor */
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.ApplicationControl.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.ApplicationControl.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val operation = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENApplicationControl.loc_proto), F, F, F))).
            update("@extensible", PropValue(T))

          val (h_2, es1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              // case for "new tizen.ApplicationControl(operation)"
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_2 = h_1.update(l_r, o_arr)
              val o_new2 = o_new.
                update("operation", PropValue(ObjectValue(Value(Helper.toString(operation._1)), F, T, T))).
                update("uri", PropValue(ObjectValue(AbsString.alpha(""), F, T, T))).
                update("mime", PropValue(ObjectValue(AbsString.alpha(""), F, T, T))).
                update("category", PropValue(ObjectValue(AbsString.alpha(""), F, T, T))).
                update("data", PropValue(ObjectValue(Value(LocSet(l_r)), F, T, T)))
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, o_new2))
              (h_3, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              // case for "new tizen.ApplicationControl(operation, uri)
              val uri = getArgValue(h_1, ctx_1, args, "1")
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_2 = h_1.update(l_r, o_arr)
              val o_new2 =
                if (uri._1._2 </ NullTop)
                  o_new.
                    update("operation", PropValue(ObjectValue(Value(Helper.toString(operation._1)), F, T, T))).
                    update("uri", PropValue(ObjectValue(Value(Helper.toString(uri._1)), F, T, T))).
                    update("mime", PropValue(ObjectValue(AbsString.alpha(""), F, T, T))).
                    update("category", PropValue(ObjectValue(AbsString.alpha(""), F, T, T))).
                    update("data", PropValue(ObjectValue(Value(LocSet(l_r)), F, T, T)))
                else
                  o_new.
                    update("operation", PropValue(ObjectValue(Value(Helper.toString(operation._1)), F, T, T))).
                    update("uri", PropValue(ObjectValue(Value(uri._1), F, T, T))).
                    update("mime", PropValue(ObjectValue(AbsString.alpha(""), F, T, T))).
                    update("category", PropValue(ObjectValue(AbsString.alpha(""), F, T, T))).
                    update("data", PropValue(ObjectValue(Value(LocSet(l_r)), F, T, T)))
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, o_new2))
              (h_3, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 3 =>
              // case for "new tizen.ApplicationControl(operation, uri, mime)
              val uri = getArgValue(h_1, ctx_1, args, "1")
              val mime = getArgValue(h_1, ctx_1, args, "2")
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_2 = h_1.update(l_r, o_arr)

              val o_new2 =
                if (uri._1._2 </ NullTop)
                  o_new.update("uri", PropValue(ObjectValue(Value(Helper.toString(uri._1)), F, T, T)))
                else o_new.update("uri", PropValue(ObjectValue(Value(uri._1), F, T, T)))
              val o_new3 =
                if (mime._1._2 </ NullTop)
                  o_new2.update("mime", PropValue(ObjectValue(Value(Helper.toString(mime._1)), F, T, T)))
                else o_new2.update("mime", PropValue(ObjectValue(Value(mime._1), F, T, T)))
              val o_new4 =
                o_new3.
                  update("operation", PropValue(ObjectValue(Value(Helper.toString(operation._1)), F, T, T))).
                  update("category", PropValue(ObjectValue(AbsString.alpha(""), F, T, T))).
                  update("data", PropValue(ObjectValue(Value(LocSet(l_r)), F, T, T)))
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, o_new4))
              (h_3, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 4 =>
              // case for "new tizen.ApplicationControl(operation, uri, mime, category)
              val uri = getArgValue(h_1, ctx_1, args, "1")
              val mime = getArgValue(h_1, ctx_1, args, "2")
              val category = getArgValue(h_1, ctx_1, args, "3")
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_2 = h_1.update(l_r, o_arr)
              val o_new2 =
                if (uri._1._2 </ NullTop)
                  o_new.update("uri", PropValue(ObjectValue(Value(Helper.toString(uri._1)), F, T, T)))
                else o_new.update("uri", PropValue(ObjectValue(Value(uri._1), F, T, T)))
              val o_new3 =
                if (mime._1._2 </ NullTop)
                  o_new2.update("mime", PropValue(ObjectValue(Value(Helper.toString(mime._1)), F, T, T)))
                else o_new2.update("mime", PropValue(ObjectValue(Value(mime._1), F, T, T)))
              val o_new4 =
                if (category._1._2 </ NullTop)
                  o_new3.update("category", PropValue(ObjectValue(Value(Helper.toString(category._1)), F, T, T)))
                else o_new3.update("category", PropValue(ObjectValue(Value(category._1), F, T, T)))
              val o_new5 = o_new4.
                update("operation", PropValue(ObjectValue(Value(Helper.toString(operation._1)), F, T, T))).
                update("data", PropValue(ObjectValue(Value(LocSet(l_r)), F, T, T)))
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, o_new5))
              (h_3, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 5 =>
              // case for "new tizen.ApplicationControl(operation, uri, mime, category, data)
              val uri = getArgValue(h_1, ctx_1, args, "1")
              val mime = getArgValue(h_1, ctx_1, args, "2")
              val category = getArgValue(h_1, ctx_1, args, "3")
              val data = getArgValue(h_1, ctx_1, args, "4")
              val es =
                if (data._2.exists((l) => Helper.IsArray(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_new2 =
                if (uri._1._2 </ NullTop)
                  o_new.update("uri", PropValue(ObjectValue(Value(Helper.toString(uri._1)), F, T, T)))
                else o_new.update("uri", PropValue(ObjectValue(Value(uri._1), F, T, T)))
              val o_new3 =
                if (mime._1._2 </ NullTop)
                  o_new2.update("mime", PropValue(ObjectValue(Value(Helper.toString(mime._1)), F, T, T)))
                else o_new2.update("mime", PropValue(ObjectValue(Value(mime._1), F, T, T)))
              val o_new4 =
                if (category._1._2 </ NullTop)
                  o_new3.update("category", PropValue(ObjectValue(Value(Helper.toString(category._1)), F, T, T)))
                else o_new3.update("category", PropValue(ObjectValue(Value(category._1), F, T, T)))
              val o_new5 = o_new4.
                update("operation", PropValue(ObjectValue(Value(Helper.toString(operation._1)), F, T, T))).
                update("data", PropValue(ObjectValue(Value(data._2), F, T, T)))

              val h_2 = lset_this.foldLeft(h_1)((_h, l) => _h.update(l, o_new5))
              (h_2, es)
            case _ => {
              (HeapBot, TizenHelper.TizenExceptionBot)
            }
          }

          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es1)
          ((Helper.ReturnStore(h_2, Value(lset_this)), ctx_1), (he + h_e, ctxe + ctx_e))
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
