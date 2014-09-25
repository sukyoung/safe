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

import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinArray
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap

object TIZENcontentObj extends Tizen {
  private val name = "content"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_content
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_contentdir: Loc = newSystemLoc("ContentDirectory", Old)
  val loc_contentdirarr: Loc = newSystemLoc("ContentDirectoryArr", Old)
  val loc_content: Loc = newSystemLoc("Content", Old)
  val loc_contentarr: Loc = newSystemLoc("ContentArr", Old)
  val loc_strarr: Loc = newSystemLoc("contentStrArr", Old)
  val loc_uintarr: Loc = newSystemLoc("contentUIntArr", Old)
  val loc_vidcontent: Loc = newSystemLoc("VideoContent", Old)
  val loc_audcontentlyrics: Loc = newSystemLoc("AudioContentLyrics", Old)
  val loc_audcontent: Loc = newSystemLoc("AudioContent", Old)
  val loc_imgcontent: Loc = newSystemLoc("ImageContent", Old)
  val loc_geoloc: Loc = newSystemLoc("geolocation", Old)

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_contentdir, prop_contentdir_ins),
    (loc_contentdirarr, prop_contentdirarr_ins), (loc_content, prop_content_ins), (loc_contentarr, prop_contentarr_ins),
    (loc_strarr, prop_strarr_ins), (loc_vidcontent, prop_vidcontent_ins), (loc_audcontent, prop_audcontent_ins),
    (loc_audcontentlyrics, prop_audcontentlyrics_ins), (loc_imgcontent, prop_imgcontent_ins), (loc_uintarr, prop_uintarr_ins),
    (loc_geoloc, prop_geoloc_ins)
  )
  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T)))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T))),
    ("update",              AbsBuiltinFunc("tizen.content.update", 1)),
    ("updateBatch",         AbsBuiltinFunc("tizen.content.updateBatch", 3)),
    ("getDirectories",      AbsBuiltinFunc("tizen.content.getDirectories", 2)),
    ("find",                AbsBuiltinFunc("tizen.content.find", 7)),
    ("scanFile",            AbsBuiltinFunc("tizen.content.scanFile", 3)),
    ("setChangeListener",   AbsBuiltinFunc("tizen.content.setChangeListener", 1)),
    ("unsetChangeListener", AbsBuiltinFunc("tizen.content.unsetChangeListener", 0))
  )

  private val prop_contentdir_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENContentDirectory.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("directoryURI", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("title", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("storageType", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("INTERNAL") + AbsString.alpha("EXTERNAL")), T, T, T)))),
    ("modifiedDate", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), T, T, T))))
  )
  private val prop_contentdirarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_contentdir), T, T, T))))
  )

  private val prop_content_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENContent.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("editableAttributes", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), F, T, T)))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("IMAGE") + AbsString.alpha("VIDEO") +
                                                     AbsString.alpha("AUDIO") + AbsString.alpha("OTHER")), F, T, T)))),
    ("mimeType", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("title", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("contentURI", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("thumbnailURIs", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_strarr)), F, T, T)))),
    ("releaseDate", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), F, T, T)))),
    ("modifiedDate", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), F, T, T)))),
    ("size", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("description", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("rating", AbsConstValue(PropValue(ObjectValue(Value(UInt), T, T, T))))
  )
  private val prop_contentarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(LocSet(loc_content) ++ LocSet(loc_vidcontent) ++ LocSet(loc_audcontent) ++ LocSet(loc_imgcontent)), T, T, T))))
  )

  private val prop_strarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )

  private val prop_uintarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(UInt), T, T, T))))
  )

  private val prop_vidcontent_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENVideoContent.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("editableAttributes", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), F, T, T)))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("IMAGE") + AbsString.alpha("VIDEO") +
      AbsString.alpha("AUDIO") + AbsString.alpha("OTHER")), F, T, T)))),
    ("mimeType", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("title", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("contentURI", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("thumbnailURIs", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_strarr)), F, T, T)))),
    ("releaseDate", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), F, T, T)))),
    ("modifiedDate", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), F, T, T)))),
    ("size", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("description", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("rating", AbsConstValue(PropValue(ObjectValue(Value(UInt), T, T, T)))),
    ("geolocation", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_geoloc)), T, T, T)))),
    ("album", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("artists", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_strarr)), T, T, T)))),
    ("duration", AbsConstValue(PropValue(ObjectValue(Value(UInt), T, T, T)))),
    ("width", AbsConstValue(PropValue(ObjectValue(Value(UInt), T, T, T)))),
    ("height", AbsConstValue(PropValue(ObjectValue(Value(UInt), T, T, T))))
  )

  private val prop_audcontent_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENAudioContent.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("editableAttributes", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), F, T, T)))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("IMAGE") + AbsString.alpha("VIDEO") +
      AbsString.alpha("AUDIO") + AbsString.alpha("OTHER")), F, T, T)))),
    ("mimeType", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("title", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("contentURI", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("thumbnailURIs", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_strarr)), F, T, T)))),
    ("releaseDate", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), F, T, T)))),
    ("modifiedDate", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), F, T, T)))),
    ("size", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("description", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("rating", AbsConstValue(PropValue(ObjectValue(Value(UInt), T, T, T)))),
    ("album", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T)))),
    ("genres", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_strarr)), F, T, T)))),
    ("artists", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_strarr)), F, T, T)))),
    ("composers", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_strarr)), F, T, T)))),
    ("lyrics", AbsConstValue(PropValue(ObjectValue(Value(loc_audcontentlyrics), F, T, T)))),
    ("copyright", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T)))),
    ("bitrate", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("trackNumber", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, UInt, StrBot)), F, T, T)))),
    ("duration", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T))))
  )

  private val prop_audcontentlyrics_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENAudioContentLyrics.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("SYNCHRONIZED") + AbsString.alpha("UNSYNCHRONIZED")), F, T, T)))),
    ("timestamps", AbsConstValue(PropValue(ObjectValue(Value(loc_uintarr), F, T, T)))),
    ("texts", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), F, T, T))))
  )

  private val prop_imgcontent_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENImageContent.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("editableAttributes", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), F, T, T)))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("IMAGE") + AbsString.alpha("VIDEO") +
      AbsString.alpha("AUDIO") + AbsString.alpha("OTHER")), F, T, T)))),
    ("mimeType", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("title", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("contentURI", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("thumbnailURIs", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_strarr)), F, T, T)))),
    ("releaseDate", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), F, T, T)))),
    ("modifiedDate", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENtizen.loc_date)), F, T, T)))),
    ("size", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("description", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("rating", AbsConstValue(PropValue(ObjectValue(Value(UInt), T, T, T)))),
    ("geolocation", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_geoloc)), T, T, T)))),
    ("width", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("height", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("orientation", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("NORMAL") + AbsString.alpha("FLIP_HORIZONTAL") +
      AbsString.alpha("ROTATE_180") + AbsString.alpha("FLIP_VERTICAL") + AbsString.alpha("TRANSPOSE") + AbsString.alpha("ROTATE_90") +
      AbsString.alpha("TRANSVERSE") + AbsString.alpha("ROTATE_270")), T, T, T))))
  )

  private val prop_geoloc_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSimpleCoordinates.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("latitude", AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T)))),
    ("longitude", AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T))))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.content.update" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v_1, Value(TIZENContent.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, InvalidValuesError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.content.updateBatch" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
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
                    val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENContent.loc_proto))
                    val esi =
                      if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esj ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h_1, ll, AbsString.alpha(Str_default_number))
                  val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENContent.loc_proto))
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
          val est = Set[WebAPIException](SecurityError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.content.getDirectories" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
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
            update("0", PropValue(ObjectValue(Value(TIZENcontentObj.loc_contentdirarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("ContentDirArraySuccessCB"), Value(v_1._2), Value(l_r1))
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
                update("0", PropValue(ObjectValue(Value(TIZENtizen.loc_unknownerr), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.content.find" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
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
            update("0", PropValue(ObjectValue(Value(loc_contentarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("ContentArrSuccessCB"), Value(v_1._2), Value(l_r1))

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
              val es_3 =
                if (v_3._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3)
            case Some(n) if n == 4 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_1, es_4) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_5) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENAttributeRangeFilter.loc_proto))
              val (b_3, es_6) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENCompositeFilter.loc_proto))
              val es_7 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7)
            case Some(n) if n == 5 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val v_5 = getArgValue(h_4, ctx_2, args, "4")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_1, es_4) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_5) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENAttributeRangeFilter.loc_proto))
              val (b_3, es_6) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENCompositeFilter.loc_proto))
              val es_7 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es_8) = TizenHelper.instanceOf(h_4, v_5, Value(TIZENSortMode.loc_proto))
              val es_9 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8 ++ es_9)
            case Some(n) if n == 6 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val v_5 = getArgValue(h_4, ctx_2, args, "4")
              val v_6 = getArgValue(h_4, ctx_2, args, "5")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_1, es_4) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_5) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENAttributeRangeFilter.loc_proto))
              val (b_3, es_6) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENCompositeFilter.loc_proto))
              val es_7 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es_8) = TizenHelper.instanceOf(h_4, v_5, Value(TIZENSortMode.loc_proto))
              val es_9 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_10 =
                if (v_6._1._2 </ NullTop && v_6._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8 ++ es_9 ++ es_10)
            case Some(n) if n >= 7 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val v_5 = getArgValue(h_4, ctx_2, args, "4")
              val v_6 = getArgValue(h_4, ctx_2, args, "5")
              val v_7 = getArgValue(h_4, ctx_2, args, "6")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_1, es_4) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_5) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENAttributeRangeFilter.loc_proto))
              val (b_3, es_6) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENCompositeFilter.loc_proto))
              val es_7 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es_8) = TizenHelper.instanceOf(h_4, v_5, Value(TIZENSortMode.loc_proto))
              val es_9 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_10 =
                if (v_6._1._2 </ NullTop && v_6._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_11 =
                if (v_7._1._2 </ NullTop && v_7._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8 ++ es_9 ++ es_10 ++ es_11)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))

        }
        )),
      ("tizen.content.scanFile" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
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
            if (v_1._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_3, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_2, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(v_1._1._5), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("ContentScanSuccessCB"), Value(v_2._2), Value(l_r1))
              (h_4, es_2)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val v_3 = getArgValue(h_2, ctx_2, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(v_1._1._5), T, T, T)))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1).update(l_r2, o_arr2)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("ContentScanSuccessCB"), Value(v_2._2), Value(l_r1))
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_5, es_2 ++ es_3)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, InvalidValuesError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_3, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.content.setChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
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
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("oncontentadded"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("oncontentupdated"))
            val v3 = Helper.Proto(_he._1, l, AbsString.alpha("oncontentremoved"))
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
              update("0", PropValue(ObjectValue(Value(TIZENcontactObj.loc_cont), T, T, T)))
            val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcontactObj.loc_cont), T, T, T)))
            val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(StrTop), T, T, T)))
            val h_4 = _he._1.update(l_r1, o_arr).update(l_r2, o_arr1).update(l_r3, o_arr2)
            val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("ContentChangeCB.oncontentadded"), Value(v1._2), Value(l_r1))
            val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("ContentChangeCB.oncontentupdated"), Value(v2._2), Value(l_r2))
            val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("ContentChangeCB.oncontentremoved"), Value(v3._2), Value(l_r3))
            (h_7, _he._2 ++ es1 ++ es2 ++ es3)
          })
          val est = Set[WebAPIException](SecurityError, InvalidValuesError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ est)
          ((h_4, ctx_3), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.content.unsetChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENContent extends Tizen {
  private val name = "Content"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENContentDirectory extends Tizen {
  private val name = "ContentDirectory"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENVideoContent extends Tizen {
  private val name = "VideoContent"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_parent = TIZENContent.loc_proto
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(loc_parent), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENAudioContentLyrics extends Tizen {
  private val name = "AudioContentLyrics"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENAudioContent extends Tizen {
  private val name = "AudioContent"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_parent = TIZENContent.loc_proto
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(loc_parent), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENImageContent extends Tizen {
  private val name = "ImageContent"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_parent = TIZENContent.loc_proto
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(loc_parent), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
