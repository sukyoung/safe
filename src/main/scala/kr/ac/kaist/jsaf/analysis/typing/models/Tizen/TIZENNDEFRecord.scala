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
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing._


object TIZENNDEFRecord extends Tizen {
  private val name = "NDEFRecord"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.NDEFRecord.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.NDEFRecord.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_2, ctx_2) = Helper.Oldify(h, ctx, addr1)
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))

          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENNDEFRecord.loc_proto), F, F, F))).
            update("@extensible", PropValue(T))

          val (h_3, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 3 =>
              val v_1 = getArgValue(h_2, ctx_2, args, "0")
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val v_3 = getArgValue(h_2, ctx_2, args, "2")
              val es1 =
                if (v_1._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es2 =
                if (v_2._2.exists((l) => Helper.IsArray(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es3 =
                if (v_3._2.exists((l) => Helper.IsArray(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot

              val es4 = v_2._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                val n_length = Operator.ToUInt32(Helper.Proto(h_2, ll, AbsString.alpha("length")))
                val ess = n_length.getAbsCase match {
                  case AbsBot =>
                    TizenHelper.TizenExceptionBot
                  case _ => AbsNumber.getUIntSingle(n_length) match {
                    case Some(n) => {
                      val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                        val vi = Helper.Proto(h_2, ll, AbsString.alpha(i.toString))
                        val esi =
                          if (vi._1._4 </ NumTop)
                            Set[WebAPIException](TypeMismatchError)
                          else TizenHelper.TizenExceptionBot
                        _e ++ esi
                      })
                      es__
                    }
                    case _ => {
                      val vi = Helper.Proto(h_2, ll, AbsString.alpha("@default_number"))
                      val esi =
                        if (vi._1._4 </ NumTop)
                          Set[WebAPIException](TypeMismatchError)
                        else TizenHelper.TizenExceptionBot
                      esi
                    }
                  }
                }
                _es ++ ess
              })

              val es5 = v_3._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                val n_length = Operator.ToUInt32(Helper.Proto(h_2, ll, AbsString.alpha("length")))
                val ess = n_length.getAbsCase match {
                  case AbsBot =>
                    TizenHelper.TizenExceptionBot
                  case _ => AbsNumber.getUIntSingle(n_length) match {
                    case Some(n) => {
                      val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                        val vi = Helper.Proto(h_2, ll, AbsString.alpha(i.toString))
                        val esi =
                          if (vi._1._4 </ NumTop)
                            Set[WebAPIException](TypeMismatchError)
                          else TizenHelper.TizenExceptionBot
                        _e ++ esi
                      })
                      es__
                    }
                    case _ => {
                      val vi = Helper.Proto(h_2, ll, AbsString.alpha("@default_number"))
                      val esi =
                        if (vi._1._4 </ NumTop)
                          Set[WebAPIException](TypeMismatchError)
                        else TizenHelper.TizenExceptionBot
                      esi
                    }
                  }
                }
                _es ++ ess
              })
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_3 = h_2.update(l_r1, o_arr)
              val o_new2 = o_new.
                update("tnf", PropValue(ObjectValue(Value(v_1._1._4), F, T, T))).
                update("type", PropValue(ObjectValue(Value(v_2._2), F, T, T))).
                update("payload", PropValue(ObjectValue(Value(v_3._2), F, T, T))).
                update("id", PropValue(ObjectValue(Value(l_r1), F, T, T)))
              val h_4 = lset_this.foldLeft(h_3)((_h, l) => _h.update(l, o_new2))
              (h_4, es1 ++ es2 ++ es3 ++ es4 ++ es5)
            case Some(n) if n >= 4 =>
              val v_1 = getArgValue(h_2, ctx_2, args, "0")
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val v_3 = getArgValue(h_2, ctx_2, args, "2")
              val v_4 = getArgValue(h_2, ctx_2, args, "3")
              val es1 =
                if (v_1._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es2 =
                if (v_2._2.exists((l) => Helper.IsArray(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es3 =
                if (v_3._2.exists((l) => Helper.IsArray(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es4 =
                if (v_4._2.exists((l) => Helper.IsArray(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot

              val es5 = v_2._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                val n_length = Operator.ToUInt32(Helper.Proto(h_2, ll, AbsString.alpha("length")))
                val ess = n_length.getAbsCase match {
                  case AbsBot =>
                    TizenHelper.TizenExceptionBot
                  case _ => AbsNumber.getUIntSingle(n_length) match {
                    case Some(n) => {
                      val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                        val vi = Helper.Proto(h_2, ll, AbsString.alpha(i.toString))
                        val esi =
                          if (vi._1._4 </ NumTop)
                            Set[WebAPIException](TypeMismatchError)
                          else TizenHelper.TizenExceptionBot
                        _e ++ esi
                      })
                      es__
                    }
                    case _ => {
                      val vi = Helper.Proto(h_2, ll, AbsString.alpha("@default_number"))
                      val esi =
                        if (vi._1._4 </ NumTop)
                          Set[WebAPIException](TypeMismatchError)
                        else TizenHelper.TizenExceptionBot
                      esi
                    }
                  }
                }
                _es ++ ess
              })

              val es6 = v_3._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                val n_length = Operator.ToUInt32(Helper.Proto(h_2, ll, AbsString.alpha("length")))
                val ess = n_length.getAbsCase match {
                  case AbsBot =>
                    TizenHelper.TizenExceptionBot
                  case _ => AbsNumber.getUIntSingle(n_length) match {
                    case Some(n) => {
                      val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                        val vi = Helper.Proto(h_2, ll, AbsString.alpha(i.toString))
                        val esi =
                          if (vi._1._4 </ NumTop)
                            Set[WebAPIException](TypeMismatchError)
                          else TizenHelper.TizenExceptionBot
                        _e ++ esi
                      })
                      es__
                    }
                    case _ => {
                      val vi = Helper.Proto(h_2, ll, AbsString.alpha("@default_number"))
                      val esi =
                        if (vi._1._4 </ NumTop)
                          Set[WebAPIException](TypeMismatchError)
                        else TizenHelper.TizenExceptionBot
                      esi
                    }
                  }
                }
                _es ++ ess
              })

              val es7 = v_4._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                val n_length = Operator.ToUInt32(Helper.Proto(h_2, ll, AbsString.alpha("length")))
                val ess = n_length.getAbsCase match {
                  case AbsBot =>
                    TizenHelper.TizenExceptionBot
                  case _ => AbsNumber.getUIntSingle(n_length) match {
                    case Some(n) => {
                      val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                        val vi = Helper.Proto(h_2, ll, AbsString.alpha(i.toString))
                        val esi =
                          if (vi._1._4 </ NumTop)
                            Set[WebAPIException](TypeMismatchError)
                          else TizenHelper.TizenExceptionBot
                        _e ++ esi
                      })
                      es__
                    }
                    case _ => {
                      val vi = Helper.Proto(h_2, ll, AbsString.alpha("@default_number"))
                      val esi =
                        if (vi._1._4 </ NumTop)
                          Set[WebAPIException](TypeMismatchError)
                        else TizenHelper.TizenExceptionBot
                      esi
                    }
                  }
                }
                _es ++ ess
              })

              val o_new2 = o_new.
                update("tnf", PropValue(ObjectValue(Value(v_1._1._4), F, T, T))).
                update("type", PropValue(ObjectValue(Value(v_2._2), F, T, T))).
                update("payload", PropValue(ObjectValue(Value(v_3._2), F, T, T))).
                update("id", PropValue(ObjectValue(Value(v_4._2), F, T, T)))
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, o_new2))
              (h_3, es1 ++ es2 ++ es3 ++ es4 ++ es5 ++ es6 ++ es7)
            case _ => (h_2, TizenHelper.TizenExceptionBot)
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1)
          ((Helper.ReturnStore(h_3, Value(lset_this)), ctx_2), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
