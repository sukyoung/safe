/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinDate
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing._

object TIZENContact extends Tizen {
  private val name = "Contact"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.Contact.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("convertToString", AbsBuiltinFunc("tizen.Contact.convertToString", 1)),
    ("clone", AbsBuiltinFunc("tizen.Contact.clone", 0))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.Contact.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val addr5 = cfg.getAPIAddress(addr_env, 4)
          val addr6 = cfg.getAPIAddress(addr_env, 5)
          val addr7 = cfg.getAPIAddress(addr_env, 6)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val l_r4 = addrToLoc(addr4, Recent)
          val l_r5 = addrToLoc(addr5, Recent)
          val l_r6 = addrToLoc(addr6, Recent)
          val l_r7 = addrToLoc(addr7, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)
          val (h_5, ctx_5) = Helper.Oldify(h_4, ctx_4, addr5)
          val (h_6, ctx_6) = Helper.Oldify(h_5, ctx_5, addr6)
          val (h_8, ctx_8) = Helper.Oldify(h_6, ctx_6, addr7)
          val n_arglen = Operator.ToUInt32(getArgValue(h_8, ctx_8, args, "length"))

          val o_new = Obj.empty.
           update("@class", PropValue(AbsString.alpha("Object"))).
           update("@proto", PropValue(ObjectValue(Value(TIZENContact.loc_proto), F, F, F))).
           update("@extensible", PropValue(T))

         val (h_9, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
           case Some(n) if n == 0 =>
             val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr4 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr5 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr6 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr7 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val h_9 = h_8.
               update(l_r1, o_arr).
               update(l_r2, o_arr2).
               update(l_r3, o_arr3).
               update(l_r4, o_arr4).
               update(l_r5, o_arr5).
               update(l_r6, o_arr6).
               update(l_r7, o_arr7)
             val o_new2 = o_new.
               update("id", PropValue(ObjectValue(Value(NullTop), F, T, T))).
               update("personId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
               update("addressBookId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
               update("lastUpdated", PropValue(ObjectValue(Value(NullTop), F, T, T))).
               update("isFavorite", PropValue(ObjectValue(Value(F), F, T, T))).
               update("name", PropValue(ObjectValue(Value(NullTop), T, T, T))).
               update("addresses", PropValue(ObjectValue(Value(l_r1), T, T, T))).
               update("photoURI", PropValue(ObjectValue(Value(NullTop), T, T, T))).
               update("phoneNumbers", PropValue(ObjectValue(Value(l_r2), T, T, T))).
               update("emails", PropValue(ObjectValue(Value(l_r3), T, T, T))).
               update("birthday", PropValue(ObjectValue(Value(NullTop), T, T, T))).
               update("anniversaries", PropValue(ObjectValue(Value(l_r4), T, T, T))).
               update("organizations", PropValue(ObjectValue(Value(l_r5), T, T, T))).
               update("notes", PropValue(ObjectValue(Value(l_r6), T, T, T))).
               update("urls", PropValue(ObjectValue(Value(l_r7), T, T, T))).
               update("ringtoneURI", PropValue(ObjectValue(Value(NullTop), T, T, T))).
               update("groupIds", PropValue(ObjectValue(Value(l_r6), T, T, T)))
             val h_10 = lset_this.foldLeft(h_9)((_h, l) => _h.update(l, o_new2))
             (h_10, TizenHelper.TizenExceptionBot)
           case Some(n) if n >= 1 =>
             val v = getArgValue(h_8, ctx_8, args, "0")
             val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr4 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr5 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr6 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val o_arr7 = Helper.NewArrayObject(AbsNumber.alpha(0))
             val h_9 = h_8.
               update(l_r1, o_arr).
               update(l_r2, o_arr2).
               update(l_r3, o_arr3).
               update(l_r4, o_arr4).
               update(l_r5, o_arr5).
               update(l_r6, o_arr6).
               update(l_r7, o_arr7)
             val (obj, es_1) = v._2.foldLeft((o_new, TizenHelper.TizenExceptionBot))((_o, l) => {
               val v_1 = Helper.Proto(h_9, l, AbsString.alpha("name"))
               val v_2 = Helper.Proto(h_9, l, AbsString.alpha("addresses"))
               val v_3 = Helper.Proto(h_9, l, AbsString.alpha("photoURI"))
               val v_4 = Helper.Proto(h_9, l, AbsString.alpha("phoneNumbers"))
               val v_5 = Helper.Proto(h_9, l, AbsString.alpha("emails"))
               val v_6 = Helper.Proto(h_9, l, AbsString.alpha("birthday"))
               val v_7 = Helper.Proto(h_9, l, AbsString.alpha("anniversaries"))
               val v_8 = Helper.Proto(h_9, l, AbsString.alpha("organizations"))
               val v_9 = Helper.Proto(h_9, l, AbsString.alpha("notes"))
               val v_10 = Helper.Proto(h_9, l, AbsString.alpha("urls"))
               val v_11 = Helper.Proto(h_9, l, AbsString.alpha("ringtoneURI"))
               val v_12 = Helper.Proto(h_9, l, AbsString.alpha("groupIds"))
               val (b_1, es_1) = TizenHelper.instanceOf(h_9, v_1, Value(TIZENContactName.loc_proto))
               val es_2 =
                 if (v_1._1._1 </ UndefTop && b_1._1._3 <= F)
                   Set[WebAPIException](TypeMismatchError)
                 else TizenHelper.TizenExceptionBot
               val o_1 =
                 if (b_1._1._3 <= T)
                   _o._1.update("name", PropValue(ObjectValue(Value(v_1._2), T, T, T)))
                 else _o._1.update("name", PropValue(ObjectValue(Value(UndefTop), T, T, T)))
               val (o_2, es_3) =
                 if (v_2._1._1 <= UndefBot) {
                   val es_ = v_2._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                     val n_length = Operator.ToUInt32(Helper.Proto(h_9, ll, AbsString.alpha("length")))
                     val ess = n_length.getAbsCase match {
                       case AbsBot => TizenHelper.TizenExceptionBot
                       case _ => AbsNumber.getUIntSingle(n_length) match {
                         case Some(n) => {
                           val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                             val vi = Helper.Proto(h_9, ll, AbsString.alpha(i.toString))
                             val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactAddress.loc_proto))
                             val esii =
                               if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                               else TizenHelper.TizenExceptionBot
                             _e ++ esi ++ esii
                           })
                           es__
                         }
                         case _ => {
                           val vi = Helper.Proto(h_9, ll, AbsString.alpha(Str_default_number))
                           val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactAddress.loc_proto))
                           val esii =
                             if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                             else TizenHelper.TizenExceptionBot
                           esi ++ esii
                         }
                       }
                     }
                     _es ++ ess
                   })
                   (_o._1.update("addresses", PropValue(ObjectValue(Value(v_2._2), T, T, T))), es_)
                 }
                 else (_o._1.update("addresses", PropValue(ObjectValue(Value(l_r1), T, T, T))), TizenHelper.TizenExceptionBot)
               val es_4 =
                 if (v_3._1._1 </ UndefTop && v_3._1._5 </ StrTop)
                   Set[WebAPIException](TypeMismatchError)
                 else TizenHelper.TizenExceptionBot
               val o_3 =
                 if (v_3._1._1 </ UndefBot)
                   _o._1.update("photoURI", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                 else _o._1.update("photoURI", PropValue(ObjectValue(Value(v_3._1._5), T, T, T)))
               val (o_4, es_5) =
                 if (v_4._1._1 <= UndefBot) {
                   val es_ = v_4._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                     val n_length = Operator.ToUInt32(Helper.Proto(h_9, ll, AbsString.alpha("length")))
                     val ess = n_length.getAbsCase match {
                       case AbsBot => TizenHelper.TizenExceptionBot
                       case _ => AbsNumber.getUIntSingle(n_length) match {
                         case Some(n) => {
                           val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                             val vi = Helper.Proto(h_9, ll, AbsString.alpha(i.toString))
                             val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactPhoneNumber.loc_proto))
                             val esii =
                               if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                               else TizenHelper.TizenExceptionBot
                             _e ++ esi ++ esii
                           })
                           es__
                         }
                         case _ => {
                           val vi = Helper.Proto(h_9, ll, AbsString.alpha(Str_default_number))
                           val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactPhoneNumber.loc_proto))
                           val esii =
                             if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                             else TizenHelper.TizenExceptionBot
                           esi ++ esii
                         }
                       }
                     }
                     _es ++ ess
                   })
                   (_o._1.update("phoneNumbers", PropValue(ObjectValue(Value(v_4._2), T, T, T))), es_)
                 }
                 else (_o._1.update("phoneNumbers", PropValue(ObjectValue(Value(l_r2), T, T, T))), TizenHelper.TizenExceptionBot)
               val (o_5, es_6) =
                 if (v_5._1._1 <= UndefBot) {
                   val es_ = v_5._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                     val n_length = Operator.ToUInt32(Helper.Proto(h_9, ll, AbsString.alpha("length")))
                     val ess = n_length.getAbsCase match {
                       case AbsBot => TizenHelper.TizenExceptionBot
                       case _ => AbsNumber.getUIntSingle(n_length) match {
                         case Some(n) => {
                           val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                             val vi = Helper.Proto(h_9, ll, AbsString.alpha(i.toString))
                             val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactEmailAddress.loc_proto))
                             val esii =
                               if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                               else TizenHelper.TizenExceptionBot
                             _e ++ esi ++ esii
                           })
                           es__
                         }
                         case _ => {
                           val vi = Helper.Proto(h_9, ll, AbsString.alpha(Str_default_number))
                           val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactEmailAddress.loc_proto))
                           val esii =
                             if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                             else TizenHelper.TizenExceptionBot
                           esi ++ esii
                         }
                       }
                     }
                     _es ++ ess
                   })
                   (_o._1.update("emails", PropValue(ObjectValue(Value(v_5._2), T, T, T))), es_)
                 }
                 else (_o._1.update("emails", PropValue(ObjectValue(Value(l_r3), T, T, T))), TizenHelper.TizenExceptionBot)
               val (b_2, es_7) = TizenHelper.instanceOf(h_9, v_6, Value(BuiltinDate.ProtoLoc))
               val es_8 =
                 if (v_6._1._1 </ UndefTop && b_2._1._3 <= F)
                   Set[WebAPIException](TypeMismatchError)
                 else TizenHelper.TizenExceptionBot
               val o_6 =
                 if (b_2._1._3 <= T)
                   _o._1.update("birthday", PropValue(ObjectValue(Value(v_6._2), T, T, T)))
                 else _o._1.update("birthday", PropValue(ObjectValue(Value(NullTop), T, T, T)))
               val (o_7, es_9) =
                 if (v_7._1._1 <= UndefBot) {
                   val es_ = v_7._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                     val n_length = Operator.ToUInt32(Helper.Proto(h_9, ll, AbsString.alpha("length")))
                     val ess = n_length.getAbsCase match {
                       case AbsBot => TizenHelper.TizenExceptionBot
                       case _ => AbsNumber.getUIntSingle(n_length) match {
                         case Some(n) => {
                           val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                             val vi = Helper.Proto(h_9, ll, AbsString.alpha(i.toString))
                             val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactAnniversary.loc_proto))
                             val esii =
                               if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                               else TizenHelper.TizenExceptionBot
                             _e ++ esi ++ esii
                           })
                           es__
                         }
                         case _ => {
                           val vi = Helper.Proto(h_9, ll, AbsString.alpha(Str_default_number))
                           val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactAnniversary.loc_proto))
                           val esii =
                             if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                             else TizenHelper.TizenExceptionBot
                           esi ++ esii
                         }
                       }
                     }
                     _es ++ ess
                   })
                   (_o._1.update("anniversaries", PropValue(ObjectValue(Value(v_7._2), T, T, T))), es_)
                 }
                 else (_o._1.update("anniversaries", PropValue(ObjectValue(Value(l_r4), T, T, T))), TizenHelper.TizenExceptionBot)
               val (o_8, es_10) =
                 if (v_8._1._1 <= UndefBot) {
                   val es_ = v_8._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                     val n_length = Operator.ToUInt32(Helper.Proto(h_9, ll, AbsString.alpha("length")))
                     val ess = n_length.getAbsCase match {
                       case AbsBot => TizenHelper.TizenExceptionBot
                       case _ => AbsNumber.getUIntSingle(n_length) match {
                         case Some(n) => {
                           val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                             val vi = Helper.Proto(h_9, ll, AbsString.alpha(i.toString))
                             val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactOrganization.loc_proto))
                             val esii =
                               if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                               else TizenHelper.TizenExceptionBot
                             _e ++ esi ++ esii
                           })
                           es__
                         }
                         case _ => {
                           val vi = Helper.Proto(h_9, ll, AbsString.alpha(Str_default_number))
                           val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactOrganization.loc_proto))
                           val esii =
                             if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                             else TizenHelper.TizenExceptionBot
                           esi ++ esii
                         }
                       }
                     }
                     _es ++ ess
                   })
                   (_o._1.update("organizations", PropValue(ObjectValue(Value(v_8._2), T, T, T))), es_)
                 }
                 else (_o._1.update("organizations", PropValue(ObjectValue(Value(l_r5), T, T, T))), TizenHelper.TizenExceptionBot)
               val (o_9, es_11) =
                 if (v_9._1._1 <= UndefBot) {
                   val es_ = v_9._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                     val n_length = Operator.ToUInt32(Helper.Proto(h_9, ll, AbsString.alpha("length")))
                     val ess = n_length.getAbsCase match {
                       case AbsBot => TizenHelper.TizenExceptionBot
                       case _ => AbsNumber.getUIntSingle(n_length) match {
                         case Some(n) => {
                           val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                             val vi = Helper.Proto(h_9, ll, AbsString.alpha(i.toString))
                             val esi =
                               if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                               else TizenHelper.TizenExceptionBot
                             _e ++ esi
                           })
                           es__
                         }
                         case _ => {
                           val vi = Helper.Proto(h_9, ll, AbsString.alpha(Str_default_number))
                           val esi =
                             if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                             else TizenHelper.TizenExceptionBot
                           esi
                         }
                       }
                     }
                     _es ++ ess
                   })
                   (_o._1.update("notes", PropValue(ObjectValue(Value(v_9._2), T, T, T))), es_)
                 }
                 else (_o._1.update("notes", PropValue(ObjectValue(Value(l_r6), T, T, T))), TizenHelper.TizenExceptionBot)
               val (o_10, es_12) =
                 if (v_10._1._1 <= UndefBot) {
                   val es_ = v_10._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                     val n_length = Operator.ToUInt32(Helper.Proto(h_9, ll, AbsString.alpha("length")))
                     val ess = n_length.getAbsCase match {
                       case AbsBot => TizenHelper.TizenExceptionBot
                       case _ => AbsNumber.getUIntSingle(n_length) match {
                         case Some(n) => {
                           val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                             val vi = Helper.Proto(h_9, ll, AbsString.alpha(i.toString))
                             val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactWebSite.loc_proto))
                             val esii =
                               if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                               else TizenHelper.TizenExceptionBot
                             _e ++ esi ++ esii
                           })
                           es__
                         }
                         case _ => {
                           val vi = Helper.Proto(h_9, ll, AbsString.alpha(Str_default_number))
                           val (bi, esi) = TizenHelper.instanceOf(h_9, vi, Value(TIZENContactWebSite.loc_proto))
                           val esii =
                             if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                             else TizenHelper.TizenExceptionBot
                           esi ++ esii
                         }
                       }
                     }
                     _es ++ ess
                   })
                   (_o._1.update("urls", PropValue(ObjectValue(Value(v_10._2), T, T, T))), es_)
                 }
                 else (_o._1.update("urls", PropValue(ObjectValue(Value(l_r7), T, T, T))), TizenHelper.TizenExceptionBot)
               val es_13 =
                 if (v_11._1._1 </ UndefTop && v_11._1._5 </ StrTop)
                   Set[WebAPIException](TypeMismatchError)
                 else TizenHelper.TizenExceptionBot
               val o_11 =
                 if (v_11._1._5 </ StrBot)
                   _o._1.update("ringtoneURI", PropValue(ObjectValue(Value(v_11._1._5), T, T, T)))
                 else _o._1.update("ringtoneURI", PropValue(ObjectValue(Value(NullTop), T, T, T)))
               val (o_12, es_14) =
                 if (v_12._1._1 <= UndefBot) {
                   val es_ = v_12._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                     val n_length = Operator.ToUInt32(Helper.Proto(h_9, ll, AbsString.alpha("length")))
                     val ess = n_length.getAbsCase match {
                       case AbsBot => TizenHelper.TizenExceptionBot
                       case _ => AbsNumber.getUIntSingle(n_length) match {
                         case Some(n) => {
                           val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                             val vi = Helper.Proto(h_9, ll, AbsString.alpha(i.toString))
                             val esi =
                               if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                               else TizenHelper.TizenExceptionBot
                             _e ++ esi
                           })
                           es__
                         }
                         case _ => {
                           val vi = Helper.Proto(h_9, ll, AbsString.alpha(Str_default_number))
                           val esi =
                             if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                             else TizenHelper.TizenExceptionBot
                           esi
                         }
                       }
                     }
                     _es ++ ess
                   })
                   (_o._1.update("groupIds", PropValue(ObjectValue(Value(v_12._2), T, T, T))), es_)
                 }
                 else (_o._1.update("groupIds", PropValue(ObjectValue(Value(l_r6), T, T, T))), TizenHelper.TizenExceptionBot)
               (o_1 + o_2 + o_3 + o_4 + o_5 + o_6 + o_7 + o_8 + o_9 + o_10 + o_11 + o_12,
                 _o._2 ++ es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8 ++ es_9 ++ es_10 ++ es_11 ++ es_12 ++
                   es_13 ++ es_14)
             })

             val o_new2 = obj.
               update("id", PropValue(ObjectValue(Value(NullTop), F, T, T))).
               update("personId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
               update("addressBookId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
               update("lastUpdated", PropValue(ObjectValue(Value(NullTop), F, T, T))).
               update("isFavorite", PropValue(ObjectValue(Value(F), F, T, T)))
             val h_10 = lset_this.foldLeft(h_9)((_h, l) => _h.update(l, o_new2))
             (h_10, es_1)
           case _ => {
             (h_8, TizenHelper.TizenExceptionBot)
           }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1)
          ((Helper.ReturnStore(h_9, Value(lset_this)), ctx_8), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Contact.convertToString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1._5 </ AbsString.alpha("VCARD_30"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Contact.clone" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2

          val o_new = lset_this.foldLeft(Obj.empty)((o, l) => o + h_1(l))
          val h_2 = h_1.update(l_r1, o_new)

          val h_3 = h_2.update(l_r1, h_2(l_r1).update(AbsString.alpha("id"), PropValue(ObjectValue(Value(NullTop), F, T, T))))
          ((Helper.ReturnStore(h_3, Value(l_r1)), ctx_1), (he, ctxe))
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
