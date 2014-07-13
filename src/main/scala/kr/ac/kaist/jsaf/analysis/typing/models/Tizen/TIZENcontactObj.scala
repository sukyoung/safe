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
import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinArray

object TIZENcontactObj extends Tizen {
  private val name = "contact"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_contact
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_cont: Loc        = newSystemLoc("Contact", Old)
  val loc_contarr: Loc        = newSystemLoc("ContactArr", Old)
  val loc_contname: Loc        = newSystemLoc("ContactName", Old)
  val loc_contaddr: Loc        = newSystemLoc("ContactAddress", Old)
  val loc_contaddrarr: Loc        = newSystemLoc("ContactAddressArr", Old)
  val loc_contphonenum: Loc        = newSystemLoc("ContactPhoneNumber", Old)
  val loc_contphonenumarr: Loc        = newSystemLoc("ContactPhoneNumberArr", Old)
  val loc_contemailaddr: Loc        = newSystemLoc("ContactEmailAddress", Old)
  val loc_contemailaddrarr: Loc        = newSystemLoc("ContactEmailAddressArr", Old)
  val loc_contanniv: Loc        = newSystemLoc("ContactAnniversary", Old)
  val loc_contannivarr: Loc        = newSystemLoc("ContactAnniversaryArr", Old)
  val loc_contorgan: Loc        = newSystemLoc("ContactOrganization", Old)
  val loc_contorganarr: Loc        = newSystemLoc("ContactOrganizationArr", Old)
  val loc_contweb: Loc        = newSystemLoc("ContactWebSite", Old)
  val loc_contwebarr: Loc        = newSystemLoc("ContactWebSiteArr", Old)
  val loc_strarr: Loc              = newSystemLoc("contactStrArr", Old)
  val loc_person: Loc             = newSystemLoc("Person", Old)
  val loc_personarr: Loc             = newSystemLoc("PersonArr", Old)
  val loc_addrbook: Loc             = newSystemLoc("AddressBook", Old)
  val loc_addrbookarr: Loc             = newSystemLoc("AddressBookArr", Old)

  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class",        AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",        AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible",   AbsConstValue(PropValue(T)))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",                  AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",                  AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",             AbsConstValue(PropValue(T))),
    ("getAddressBooks",         AbsBuiltinFunc("tizen.contact.getAddressBooks", 2)),
    ("getUnifiedAddressBook",   AbsBuiltinFunc("tizen.contact.getUnifiedAddressBook", 0)),
    ("getDefaultAddressBook",   AbsBuiltinFunc("tizen.contact.getDefaultAddressBook", 0)),
    ("getAddressBook",          AbsBuiltinFunc("tizen.contact.getAddressBook", 1)),
    ("get",                     AbsBuiltinFunc("tizen.contact.get", 1)),
    ("update",                  AbsBuiltinFunc("tizen.contact.update", 1)),
    ("updateBatch",             AbsBuiltinFunc("tizen.contact.updateBatch", 3)),
    ("remove",                  AbsBuiltinFunc("tizen.contact.remove", 1)),
    ("removeBatch",             AbsBuiltinFunc("tizen.contact.removeBatch", 3)),
    ("find",                    AbsBuiltinFunc("tizen.contact.find", 4)),
    ("addChangeListener",       AbsBuiltinFunc("tizen.contact.addChangeListener", 1)),
    ("removeChangeListener",    AbsBuiltinFunc("tizen.contact.removeChangeListener", 1))
  )

  private val prop_strarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )

  private val prop_cont_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENContact.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T)))),
    ("personId", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T)))),
    ("addressBookId", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T)))),
    ("lastUpdated", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), F, T, T)))),
    ("isFavorite", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_contname)), T, T, T)))),
    ("addresses", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_contaddrarr)), T, T, T)))),
    ("photoURI", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("phoneNumbers", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_contphonenumarr)), T, T, T)))),
    ("emails", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_contemailaddrarr)), T, T, T)))),
    ("birthday", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), T, T, T)))),
    ("anniversaries", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_contannivarr)), T, T, T)))),
    ("organizations", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_contorganarr)), T, T, T)))),
    ("notes", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_strarr)), T, T, T)))),
    ("urls", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_contwebarr)), T, T, T)))),
    ("ringtoneURI", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("groupIds", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_strarr)), T, T, T))))
  )
  private val prop_contarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_cont), T, T, T))))
  )

  private val prop_contname_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENContactName.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("prefix", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("suffix", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("firstName", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("middleName", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("lastName", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("nicknames", AbsConstValue(PropValue(ObjectValue(Value(UndefTop), T, T, T)))),
    ("phoneticFirstName", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("phoneticLastName", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("displayName", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T))))
  )

  private val prop_contaddr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENContactAddress.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("country", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("region", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("city", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("streetAddress", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("additionalInformation", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("postalCode", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("isDefault", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("types", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), T, T, T))))
  )
  private val prop_contaddrarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_contaddr), T, T, T))))
  )
  private val prop_contphonenum_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENContactPhoneNumber.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("number", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("isDefault", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("types", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), T, T, T))))
  )
  private val prop_contphonenumarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_contphonenum), T, T, T))))
  )
  private val prop_contemailaddr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENContactEmailAddress.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("email", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("isDefault", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("types", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), T, T, T))))
  )
  private val prop_contemailaddrarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_contemailaddr), T, T, T))))
  )
  private val prop_contanniv_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENContactAnniversary.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("date", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("label", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T))))
  )
  private val prop_contannivarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_contanniv), T, T, T))))
  )
  private val prop_contorgan_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENContactOrganization.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("date", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("label", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T))))
  )
  private val prop_contorganarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_contorgan), T, T, T))))
  )
  private val prop_contweb_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENContactWebSite.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("url", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )
  private val prop_contwebarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_contweb), T, T, T))))
  )

  private val prop_person_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENPerson.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("displayName", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("contactCount", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("hasPhoneNumber", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("hasEmail", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("isFavorite", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("photoURI", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("ringtoneURI", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("displayContactId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )
  private val prop_personarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_person), T, T, T))))
  )

  private val prop_addrbook_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENAddressBook.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T)))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("readOnly", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
  )
  private val prop_addrbookarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_addrbook), T, T, T))))
  )


  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_cont, prop_cont_ins), (loc_contarr, prop_contarr_ins),
    (loc_contname, prop_contname_ins), (loc_contaddr, prop_contaddr_ins),
    (loc_contaddrarr, prop_contaddrarr_ins), (loc_contphonenum, prop_contphonenum_ins), (loc_contphonenumarr, prop_contphonenumarr_ins),
    (loc_contemailaddr, prop_contemailaddr_ins), (loc_contemailaddrarr, prop_contemailaddrarr_ins), (loc_contanniv, prop_contanniv_ins),
    (loc_contannivarr, prop_contannivarr_ins), (loc_contorgan, prop_contorgan_ins), (loc_contorganarr, prop_contorganarr_ins),
    (loc_contweb, prop_contweb_ins), (loc_contwebarr, prop_contwebarr_ins), (loc_strarr, prop_strarr_ins),
    (loc_person, prop_person_ins), (loc_personarr, prop_personarr_ins),
    (loc_addrbook, prop_addrbook_ins), (loc_addrbookarr, prop_addrbookarr_ins)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.contact.getAddressBooks" -> (
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
          val v_1 = getArgValue(h_2, ctx_2, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es_1 =
            if (v_1._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(TIZENcontactObj.loc_addrbookarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("AddrBookArrSuccessCB"), Value(v_1._2), Value(l_r1))

          val (h_5, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))

        }
        )),
      ("tizen.contact.getUnifiedAddressBook" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)

          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENAddressBook.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(NullTop), F, T, T))).
            update("name", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("readOnly", PropValue(ObjectValue(Value(BoolTop), F, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.contact.getDefaultAddressBook" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)

          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENAddressBook.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("name", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("readOnly", PropValue(ObjectValue(Value(BoolTop), F, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.contact.getAddressBook" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")

          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENAddressBook.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(Helper.toString(v_1._1)), F, T, T))).
            update("name", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("readOnly", PropValue(ObjectValue(Value(BoolTop), F, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.contact.get" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")

          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENPerson.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(Helper.toString(v_1._1)), F, T, T))).
            update("displayName", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("contactCount", PropValue(ObjectValue(Value(NumTop), F, T, T))).
            update("hasPhoneNumber", PropValue(ObjectValue(Value(BoolTop), F, T, T))).
            update("hasEmail", PropValue(ObjectValue(Value(BoolTop), F, T, T))).
            update("isFavorite", PropValue(ObjectValue(Value(BoolTop), F, T, T))).
            update("photoURI", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T))).
            update("ringtoneURI", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T))).
            update("displayContactId", PropValue(ObjectValue(Value(StrTop), F, T, T)))

          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.contact.update" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v_1, Value(TIZENPerson.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.contact.updateBatch" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

          val es_1 = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(h_1, ll, AbsString.alpha("length")))
            val ess = n_length.getAbsCase match {
              case AbsBot =>
                TizenHelper.TizenExceptionBot
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                    val vi = Helper.Proto(h_1, ll, AbsString.alpha(i.toString))
                    val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENPerson.loc_proto))
                    val esi =
                      if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esj ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h_1, ll, AbsString.alpha("@default_number"))
                  val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENPerson.loc_proto))
                  val esi =
                    if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  esi ++ esj
                }
              }
          }
            _es ++ ess
          })

          val (h_2, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_2, es_2)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_4, es_2 ++ es_3)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.contact.remove" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.contact.removeBatch" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

          val es_1 = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(h_1, ll, AbsString.alpha("length")))
            val ess = n_length.getAbsCase match {
              case AbsBot =>
                TizenHelper.TizenExceptionBot
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                    val vi = Helper.Proto(h_1, ll, AbsString.alpha(i.toString))
                    val esi =
                      if (vi._1._5 </ StrTop)
                        Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h_1, ll, AbsString.alpha("@default_number"))
                  val esi =
                    if (vi._1._5 </ StrTop)
                      Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  esi
                }
              }
            }
            _es ++ ess
          })

          val (h_2, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_2, es_2)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_4, es_2 ++ es_3)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.contact.find" -> (
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
          val v_1 = getArgValue(h_2, ctx_2, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es_1 =
            if (v_1._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(TIZENcontactObj.loc_personarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("PersonArraySuccessCB"), Value(v_1._2), Value(l_r1))

          val (h_5, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2)
            case Some(n) if n == 3 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_1, es_3) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_4) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeRangeFilter.loc_proto))
              val (b_3, es_5) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENCompositeFilter.loc_proto))
              val es_6 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6)
            case Some(n) if n == 4 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_1, es_3) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_4) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeRangeFilter.loc_proto))
              val (b_3, es_5) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENCompositeFilter.loc_proto))
              val es_6 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es_7) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENSortMode.loc_proto))
              val es_8 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))

        }
        )),
      ("tizen.contact.addChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val v_1 = getArgValue(h_3, ctx_3, args, "0")
          val (h_4, es_1) = v_1._2.foldLeft((h_3, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onpersonsadded"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("onpersonsupdated"))
            val v3 = Helper.Proto(_he._1, l, AbsString.alpha("onpersonsremoved"))
            val es1 =
              if (v1._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es2 =
              if (v2._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es3 =
              if (v3._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcontactObj.loc_personarr), T, T, T)))
            val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcontactObj.loc_personarr), T, T, T)))
            val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcontactObj.loc_strarr), T, T, T)))
            val h_4 = _he._1.update(l_r1, o_arr).update(l_r2, o_arr1).update(l_r3, o_arr2)
            val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("PersonsChangeCB.onpersonsadded"), Value(v1._2), Value(l_r1))
            val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("PersonsChangeCB.onpersonsupdated"), Value(v2._2), Value(l_r2))
            val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("PersonsChangeCB.onpersonsremoved"), Value(v3._2), Value(l_r3))
            (h_7, _he._2 ++ es1 ++ es2 ++ es3)
          })
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ est)
          ((Helper.ReturnStore(h_4, Value(NumTop)), ctx_3), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.contact.removeChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1._4 </ NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
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

object TIZENAddressBook extends Tizen {
  private val name = "AddressBook"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",                  AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",                  AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",             AbsConstValue(PropValue(T))),
    ("get",                     AbsBuiltinFunc("tizen.AddressBook.get", 1)),
    ("add",                     AbsBuiltinFunc("tizen.AddressBook.add", 1)),
    ("addBatch",                AbsBuiltinFunc("tizen.AddressBook.addBatch", 3)),
    ("update",                  AbsBuiltinFunc("tizen.AddressBook.update", 1)),
    ("updateBatch",             AbsBuiltinFunc("tizen.AddressBook.updateBatch", 3)),
    ("remove",                  AbsBuiltinFunc("tizen.AddressBook.remove", 1)),
    ("removeBatch",             AbsBuiltinFunc("tizen.AddressBook.removeBatch", 3)),
    ("find",                    AbsBuiltinFunc("tizen.AddressBook.find", 4)),
    ("addChangeListener",       AbsBuiltinFunc("tizen.AddressBook.addChangeListener", 2)),
    ("removeChangeListener",    AbsBuiltinFunc("tizen.AddressBook.removeChangeListener", 1)),
    ("getGroup",                AbsBuiltinFunc("tizen.AddressBook.getGroup", 1)),
    ("addGroup",                AbsBuiltinFunc("tizen.AddressBook.addGroup", 1)),
    ("updateGroup",             AbsBuiltinFunc("tizen.AddressBook.updateGroup", 1)),
    ("removeGroup",             AbsBuiltinFunc("tizen.AddressBook.removeGroup", 1)),
    ("getGroups",               AbsBuiltinFunc("tizen.AddressBook.getGroups", 0))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.AddressBook.get" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2)  = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3)  = Helper.Oldify(h_2, ctx_2, addr3)
          val v_1 = getArgValue(h_3, ctx_3, args, "0")

          val o_arr1 = Helper.NewArrayObject(UInt)
          val o_arr2 = o_arr1.update("@default_number", PropValue(ObjectValue(Value(StrTop), T, T, T)))
          val o_arr3 = Helper.NewArrayObject(UInt)
          val o_arr4 = o_arr3.update("@default_number", PropValue(ObjectValue(Value(StrTop), T, T, T)))
          val h_4 = h_3.update(l_r1, o_arr2).update(l_r2, o_arr4)
          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENContact.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(Helper.toString(v_1._1)), F, T, T))).
            update("personId", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T))).
            update("addressBookId", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T))).
            update("lastUpdated", PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), F, T, T))).
            update("isFavorite", PropValue(ObjectValue(Value(BoolTop), F, T, T))).
            update("name", PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENcontactObj.loc_contname)), T, T, T))).
            update("addresses", PropValue(ObjectValue(Value(TIZENcontactObj.loc_contaddrarr), T, T, T))).
            update("photoURI", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T))).
            update("phoneNumbers", PropValue(ObjectValue(Value(TIZENcontactObj.loc_contphonenumarr), T, T, T))).
            update("emails", PropValue(ObjectValue(Value(TIZENcontactObj.loc_contemailaddrarr), T, T, T))).
            update("birthday", PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), T, T, T))).
            update("anniversaries", PropValue(ObjectValue(Value(TIZENcontactObj.loc_contannivarr), T, T, T))).
            update("organizations", PropValue(ObjectValue(Value(TIZENcontactObj.loc_contorganarr), T, T, T))).
            update("notes", PropValue(ObjectValue(Value(l_r1), T, T, T))).
            update("urls", PropValue(ObjectValue(Value(TIZENcontactObj.loc_contwebarr), T, T, T))).
            update("ringtoneURI", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T))).
            update("groupIds", PropValue(ObjectValue(Value(l_r2), T, T, T)))

          val h_5 = h_4.update(l_r3, o_new)
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_5, Value(l_r3)), ctx_3), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.add" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v_1, Value(TIZENContact.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val h_1 = v_1._2.foldLeft(h)((_h, l) => {
            Helper.PropStore(_h, l, AbsString.alpha("id"), Value(PValue(StrTop)))
          })
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h_1, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.addBatch" -> (
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
          val v_1 = getArgValue(h_2, ctx_2, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val (h_3, es_1) = v_1._2.foldLeft(h_2, TizenHelper.TizenExceptionBot)((_hs, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(_hs._1, ll, AbsString.alpha("length")))
            val (h_, ess) = n_length.getAbsCase match {
              case AbsBot =>
                (_hs._1, TizenHelper.TizenExceptionBot)
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val (h__, es__) = (0 until n.toInt).foldLeft((_hs._1, TizenHelper.TizenExceptionBot))((_he, i) => {
                    val vi = Helper.Proto(_he._1, ll, AbsString.alpha(i.toString))
                    val (b_1, esj) = TizenHelper.instanceOf(_he._1, vi, Value(TIZENContact.loc_proto))
                    val esi =
                      if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    val h = vi._2.foldLeft(_he._1)((hh, lj) => {
                      Helper.PropStore(hh, lj, AbsString.alpha("id"), Value(StrTop))
                    })
                    (h,
                      _he._2 ++ esj ++ esi)
                  })
                  (h__, es__)
                }
                case _ => {
                  val vi = Helper.Proto(_hs._1, ll, AbsString.alpha("@default_number"))
                  val (b_1, esj) = TizenHelper.instanceOf(_hs._1, vi, Value(TIZENContact.loc_proto))
                  val esi =
                    if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  val h = vi._2.foldLeft(_hs._1)((hh, lj) => {
                    Helper.PropStore(hh, lj, AbsString.alpha("id"), Value(StrTop))
                  })
                  (h,
                    esi ++ esj)
                }
              }
            }
            (_hs._1 + h_, _hs._2 ++ ess)
          })

          val (h_4, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_3, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_3, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_3, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(v_1._2), T, T, T)))
              val h_4 = h_3.update(l_r1, o_arr)
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("ContactArraySuccessCB"), Value(v_2._2), Value(l_r1))
              (h_5, es_2)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_3, ctx_2, args, "1")
              val v_3 = getArgValue(h_3, ctx_2, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_3, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_3, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(v_1._2), T, T, T)))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_4 = h_3.update(l_r1, o_arr).update(l_r2, o_arr2)
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("ContactArraySuccessCB"), Value(v_2._2), Value(l_r1))
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es_2 ++ es_3)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_4, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.update" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v_1, Value(TIZENContact.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.updateBatch" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

          val es_1 = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(h_1, ll, AbsString.alpha("length")))
            val ess = n_length.getAbsCase match {
              case AbsBot =>
                TizenHelper.TizenExceptionBot
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                    val vi = Helper.Proto(h_1, ll, AbsString.alpha(i.toString))
                    val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENContact.loc_proto))
                    val esi =
                      if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esj ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h_1, ll, AbsString.alpha("@default_number"))
                  val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENContact.loc_proto))
                  val esi =
                    if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  esi ++ esj
                }
              }
            }
            _es ++ ess
          })

          val (h_2, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_2, es_2)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_4, es_2 ++ es_3)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.remove" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.removeBatch" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

          val es_1 = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(h_1, ll, AbsString.alpha("length")))
            val ess = n_length.getAbsCase match {
              case AbsBot =>
                TizenHelper.TizenExceptionBot
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                    val vi = Helper.Proto(h_1, ll, AbsString.alpha(i.toString))
                    val esi =
                      if (vi._1._5 </ StrTop)
                        Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h_1, ll, AbsString.alpha("@default_number"))
                  val esi =
                    if (vi._1._5 </ StrTop)
                      Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  esi
                }
              }
            }
            _es ++ ess
          })

          val (h_2, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_2, es_2)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_4, es_2 ++ es_3)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.find" -> (
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
          val v_1 = getArgValue(h_2, ctx_2, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es_1 =
            if (v_1._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(TIZENcontactObj.loc_contarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("ContactArraySuccessCB"), Value(v_1._2), Value(l_r1))

          val (h_5, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2)
            case Some(n) if n == 3 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_1, es_3) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_4) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeRangeFilter.loc_proto))
              val (b_3, es_5) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENCompositeFilter.loc_proto))
              val es_6 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6)
            case Some(n) if n == 4 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_1, es_3) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_4) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeRangeFilter.loc_proto))
              val (b_3, es_5) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENCompositeFilter.loc_proto))
              val es_6 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es_7) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENSortMode.loc_proto))
              val es_8 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.addChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val l_r4 = addrToLoc(addr4, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)
          val v_1 = getArgValue(h_4, ctx_4, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_4, ctx_4, args, "length"))
          val es_1 =
            if (v_1._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_5, es_2) = v_1._2.foldLeft((h_4, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("oncontactsadded"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("oncontactsupdated"))
            val v3 = Helper.Proto(_he._1, l, AbsString.alpha("oncontactsremoved"))
            val es1 =
              if (v1._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es2 =
              if (v2._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es3 =
              if (v3._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcontactObj.loc_contarr), T, T, T)))
            val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcontactObj.loc_contarr), T, T, T)))
            val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcontactObj.loc_strarr), T, T, T)))
            val h_5 = _he._1.update(l_r1, o_arr).update(l_r2, o_arr1).update(l_r3, o_arr2)
            val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("AddrBookChangeCB.oncontactsadded"), Value(v1._2), Value(l_r1))
            val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("AddrBookChangeCB.oncontactsupdated"), Value(v2._2), Value(l_r2))
            val h_8 = TizenHelper.addCallbackHandler(h_7, AbsString.alpha("AddrBookChangeCB.oncontactsremoved"), Value(v3._2), Value(l_r3))
            (h_8, _he._2 ++ es1 ++ es2 ++ es3)
          })

          val (h_6, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_5, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h_5, ctx_4, args, "1")
              val es_1 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_5, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_6 = h_5.update(l_r4, o_arr2)
              val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r4))
              (h_7, es_1)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((Helper.ReturnStore(h_6, Value(NumTop)), ctx_4), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.removeChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1._4 </ NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.getGroup" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")

          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENContactGroup.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(Helper.toString(v_1._1)), F, T, T))).
            update("addressBookId", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T))).
            update("name", PropValue(ObjectValue(Value(StrTop), T, T, T))).
            update("ringtoneURI", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T))).
            update("photoURI", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T))).
            update("readOnly", PropValue(ObjectValue(Value(StrTop), F, T, T)))

          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.addGroup" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v_1, Value(TIZENContactGroup.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val h_1 = v_1._2.foldLeft(h)((_h, l) => {
            val hh = Helper.PropStore(_h, l, AbsString.alpha("id"), Value(StrTop))
            Helper.PropStore(hh, l, AbsString.alpha("addressBookId"), Value(StrTop))
          })
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h_1, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.updateGroup" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v_1, Value(TIZENContactGroup.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.removeGroup" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AddressBook.getGroups" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2)  = Helper.Oldify(h_1, ctx_1, addr2)

          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENContactGroup.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("@scope", PropValueNullTop).
            update("@hasinstance", PropValueNullTop).
            update("id", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("addressBookId", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T))).
            update("name", PropValue(ObjectValue(Value(StrTop), T, T, T))).
            update("ringtoneURI", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T))).
            update("photoURI", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T))).
            update("readOnly", PropValue(ObjectValue(Value(StrTop), F, T, T)))
          val h_3 = h_2.update(l_r1, o_new)
          val o_arr = Helper.NewArrayObject(UInt)
          val o_arr2 = o_arr.update("@default_number", PropValue(ObjectValue(Value(l_r1), T, T, T)))
          val h_4 = h_3.update(l_r2, o_arr2)
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_4, Value(l_r2)), ctx_2), (he + h_e, ctxe + ctx_e))
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

object TIZENPerson extends Tizen {
  private val name = "Person"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",                  AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",                  AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",             AbsConstValue(PropValue(T))),
    ("link",                     AbsBuiltinFunc("tizen.Person.link", 1)),
    ("unlink",                     AbsBuiltinFunc("tizen.Person.unlink", 1))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.Person.link" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Person.unlink" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val v = getArgValue(h_1, ctx_1, args, "0")
          val es =
            if (v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENPerson.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("displayName", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("contactCount", PropValue(ObjectValue(Value(AbsNumber.alpha(1)), F, T, T))).
            update("hasPhoneNumber", PropValue(ObjectValue(Value(BoolTop), F, T, T))).
            update("hasEmail", PropValue(ObjectValue(Value(BoolTop), F, T, T))).
            update("isFavorite", PropValue(ObjectValue(Value(BoolTop), F, T, T))).
            update("photoURI", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T))).
            update("ringtoneURI", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T))).
            update("displayContactId", PropValue(ObjectValue(Value(StrTop), F, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
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
