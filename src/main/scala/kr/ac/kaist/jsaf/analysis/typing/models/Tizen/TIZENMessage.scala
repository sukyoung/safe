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

object TIZENMessage extends Tizen {
  private val name = "Message"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.Message.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.Message.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_3, ctx_3) = Helper.Oldify(h_1, ctx_1, addr2)
          val v1 = getArgValue(h_3, ctx_3, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_3, ctx_3, args, "length"))
          val es =
            if (v1._1._5 != AbsString.alpha("messaging.sms") && v1._1._5 != AbsString.alpha("messaging.mms") &&
              v1._1._5 != AbsString.alpha("messaging.email"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENMessage.loc_proto), F, F, F))).
            update("@extensible", PropValue(T))

          val (h_4, ess) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(0))

              val h_4 = h_3.update(l_r1, o_arr).update(l_r2, o_arr2)
              val o_new2 = o_new.
                update("id", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("conversationId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("folderId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("type", PropValue(ObjectValue(Value(v1._1._5), F, T, T))).
                update("timestamp", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("from", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("to", PropValue(ObjectValue(Value(l_r1), T, T, T))).
                update("cc", PropValue(ObjectValue(Value(l_r1), T, T, T))).
                update("bcc", PropValue(ObjectValue(Value(l_r1), T, T, T))).
                update("body", PropValue(ObjectValue(Value(TIZENmessaging.loc_msgbody), T, T, T))).
                update("isRead", PropValue(ObjectValue(Value(F), T, T, T))).
                update("hasAttachment", PropValue(ObjectValue(Value(BoolTop), T, T, T))).
                update("isHighPriority", PropValue(ObjectValue(Value(BoolTop), T, T, T))).
                update("subject", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("inResponseTo", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T))).
                update("messageStatus", PropValue(ObjectValue(Value(AbsString.alpha("SENT") + AbsString.alpha("SENDING") +
                                                              AbsString.alpha("FAILED") + AbsString.alpha("DRAFT")), F, T, T))).
                update("attachments", PropValue(ObjectValue(Value(l_r2), T, T, T)))
              val h_5 = lset_this.foldLeft(h_4)((_h, l) => _h.update(l, o_new2))
              (h_5, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 2 =>
              val v2 = getArgValue(h_3, ctx_3, args, "1")
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(0))

              val h_4 = h_3.update(l_r1, o_arr).update(l_r2, o_arr2)
              val (obj, es_1) = v2._2.foldLeft((o_new, TizenHelper.TizenExceptionBot))((_o, l) => {
                val v_1 = Helper.Proto(h_4, l, AbsString.alpha("subject"))
                val v_2 = Helper.Proto(h_4, l, AbsString.alpha("to"))
                val v_3 = Helper.Proto(h_4, l, AbsString.alpha("cc"))
                val v_4 = Helper.Proto(h_4, l, AbsString.alpha("bcc"))
                val v_5 = Helper.Proto(h_4, l, AbsString.alpha("plainBody"))
                val v_6 = Helper.Proto(h_4, l, AbsString.alpha("htmlBody"))
                val v_7 = Helper.Proto(h_4, l, AbsString.alpha("isHighPriority"))
                val es_1 =
                  if (v_1._1._1 </ UndefTop && v_1._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_1 =
                  if (v_1._1._5 </ StrBot)
                    _o._1.update("subject", PropValue(ObjectValue(Value(v_1._1._5), T, T, T)))
                  else _o._1.update("subject", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T)))
                val (o_2, es_2) =
                  if (v_2._1._1 <= UndefBot) {
                    val es_ = v_2._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                      val n_length = Operator.ToUInt32(Helper.Proto(h_4, ll, AbsString.alpha("length")))
                      val ess = n_length.getAbsCase match {
                        case AbsBot => TizenHelper.TizenExceptionBot
                        case _ => AbsNumber.getUIntSingle(n_length) match {
                          case Some(n) => {
                            val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                              val vi = Helper.Proto(h_4, ll, AbsString.alpha(i.toString))
                              val esi =
                                if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                                else TizenHelper.TizenExceptionBot
                              _e ++ esi
                            })
                            es__
                          }
                          case _ => {
                            val vi = Helper.Proto(h_4, ll, AbsString.alpha(Str_default_number))
                            val esi =
                              if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                              else TizenHelper.TizenExceptionBot
                            esi
                          }
                        }
                      }
                      _es ++ ess
                    })
                    (_o._1.update("to", PropValue(ObjectValue(Value(v_2._2), T, T, T))), es_)
                  }
                  else (_o._1.update("to", PropValue(ObjectValue(Value(l_r1), T, T, T))), TizenHelper.TizenExceptionBot)
                val (o_3, es_3) =
                  if (v_3._1._1 <= UndefBot) {
                    val es_ = v_3._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                      val n_length = Operator.ToUInt32(Helper.Proto(h_4, ll, AbsString.alpha("length")))
                      val ess = n_length.getAbsCase match {
                        case AbsBot => TizenHelper.TizenExceptionBot
                        case _ => AbsNumber.getUIntSingle(n_length) match {
                          case Some(n) => {
                            val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                              val vi = Helper.Proto(h_4, ll, AbsString.alpha(i.toString))
                              val esi =
                                if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                                else TizenHelper.TizenExceptionBot
                              _e ++ esi
                            })
                            es__
                          }
                          case _ => {
                            val vi = Helper.Proto(h_4, ll, AbsString.alpha(Str_default_number))
                            val esi =
                              if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                              else TizenHelper.TizenExceptionBot
                            esi
                          }
                        }
                      }
                      _es ++ ess
                    })
                    (_o._1.update("cc", PropValue(ObjectValue(Value(v_3._2), T, T, T))), es_)
                  }
                  else (_o._1.update("cc", PropValue(ObjectValue(Value(l_r1), T, T, T))), TizenHelper.TizenExceptionBot)
                val (o_4, es_4) =
                  if (v_4._1._1 <= UndefBot) {
                    val es_ = v_4._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                      val n_length = Operator.ToUInt32(Helper.Proto(h_4, ll, AbsString.alpha("length")))
                      val ess = n_length.getAbsCase match {
                        case AbsBot => TizenHelper.TizenExceptionBot
                        case _ => AbsNumber.getUIntSingle(n_length) match {
                          case Some(n) => {
                            val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                              val vi = Helper.Proto(h_4, ll, AbsString.alpha(i.toString))
                              val esi =
                                if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                                else TizenHelper.TizenExceptionBot
                              _e ++ esi
                            })
                            es__
                          }
                          case _ => {
                            val vi = Helper.Proto(h_4, ll, AbsString.alpha(Str_default_number))
                            val esi =
                              if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                              else TizenHelper.TizenExceptionBot
                            esi
                          }
                        }
                      }
                      _es ++ ess
                    })
                    (_o._1.update("bcc", PropValue(ObjectValue(Value(v_4._2), T, T, T))), es_)
                  }
                  else (_o._1.update("bcc", PropValue(ObjectValue(Value(l_r1), T, T, T))), TizenHelper.TizenExceptionBot)
                val es_5 =
                  if (v_5._1._1 </ UndefTop && v_5._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_5 =
                  if (v_5._1._5 </ StrBot){
                    _o._1.update("body", PropValue(ObjectValue(Value(TIZENMessageBody.loc_proto), T, T, T)))
                    //val o = _o._1.update("body", PropValue(ObjectValue(Value(TIZENMessageBody.loc_proto), T, T, T)))
                    //val hh = Helper.PropStore(h_4, TIZENMessageBody.loc_proto, AbsString.alpha("plainBody"), Value(v_5._1._5)) //TODO: not yet implemented
                  }
                  else _o._1.update("body", PropValue(ObjectValue(Value(TIZENMessageBody.loc_proto), T, T, T)))
                val es_6 =
                  if (v_6._1._1 </ UndefTop && v_6._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_6 =
                  if (v_6._1._5 </ StrBot)
                    _o._1.update("body", PropValue(ObjectValue(Value(TIZENMessageBody.loc_proto), T, T, T)))
                    //_o._1.update("body", PropValue(ObjectValue(Value(v_6._1._5), T, T, T))) //TODO: not yet implemented
                  else _o._1.update("body", PropValue(ObjectValue(Value(TIZENMessageBody.loc_proto), T, T, T)))
                val es_7 =
                  if (v_7._1._1 </ UndefTop && v_7._1._3 </ BoolTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_7 =
                  if (v_7._1._3 </ BoolBot)
                    _o._1.update("isHighPriority", PropValue(ObjectValue(Value(v_7._1._5), T, T, T)))
                  else _o._1.update("isHighPriority", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T)))

                (o_1 + o_2 + o_3 + o_4 + o_5 + o_6 + o_7, _o._2 ++ es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7)
              })
              val o_new2 = obj.
                update("id", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("conversationId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("folderId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("type", PropValue(ObjectValue(Value(v1._1._5), F, T, T))).
                update("timestamp", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("from", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("isRead", PropValue(ObjectValue(Value(F), T, T, T))).
                update("hasAttachment", PropValue(ObjectValue(Value(BoolTop), T, T, T))).
                update("inResponseTo", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T))).
                update("messageStatus", PropValue(ObjectValue(Value(AbsString.alpha("SENT") + AbsString.alpha("SENDING") +
                AbsString.alpha("FAILED") + AbsString.alpha("DRAFT")), F, T, T))).
                update("attachments", PropValue(ObjectValue(Value(l_r2), T, T, T)))
              val h_5 = lset_this.foldLeft(h_4)((_h, l) => _h.update(l, o_new2))
              (h_5, es_1)
            case _ => (h_3, TizenHelper.TizenExceptionBot)
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ ess)
          ((Helper.ReturnStore(h_4, Value(lset_this)), ctx_3), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENMessageBody extends Tizen {
  private val name = "MessageBody"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENMessageFolder extends Tizen {
  private val name = "MessageFolder"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENMessageConversation extends Tizen {
  private val name = "MessageConversation"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
