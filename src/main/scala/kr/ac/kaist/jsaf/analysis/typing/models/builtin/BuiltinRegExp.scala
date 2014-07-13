/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.builtin

import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.bug_detector.{RegExp4_1_1, RegExp4_1_2, RegExp2_5, RegExp2_15_2}
import kr.ac.kaist.jsaf.utils.regexp.{JSRegExpSolver, ESyntax, ERegExp4_1_1, ERegExp4_1_2, ERegExp2_5, ERegExp2_15_2, SyntaxErrorException}
import scala.collection.immutable.HashSet
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

object BuiltinRegExp extends ModelData {

  val ConstLoc = newSystemLoc("RegExpConst", Recent)
  val ProtoLoc = newSystemLoc("RegExpProto", Recent)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("@scope",                   AbsConstValue(PropValueNullTop)),
    ("@function",                AbsInternalFunc("RegExp")),
    ("@construct",               AbsInternalFunc("RegExp.constructor")),
    ("@hasinstance",             AbsConstValue(PropValueNullTop)),
    ("prototype",                AbsConstValue(PropValue(ObjectValue(Value(ProtoLoc), F, F, F)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(2), F, F, F)))),
    ("$1",                       AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, F)))),
    ("$2",                       AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, F)))),
    ("$3",                       AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, F)))),
    ("$4",                       AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, F)))),
    ("$5",                       AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, F)))),
    ("$6",                       AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, F)))),
    ("$7",                       AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, F)))),
    ("$8",                       AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, F)))),
    ("$9",                       AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, F))))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("RegExp")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(BoolTrue))),
    ("constructor",          AbsConstValue(PropValue(ObjectValue(ConstLoc, F, F, F)))),
    ("exec",                 AbsBuiltinFunc("RegExp.prototype.exec", 1)),
    ("test",                 AbsBuiltinFunc("RegExp.prototype.test", 1)),
    ("toString",             AbsBuiltinFunc("RegExp.prototype.toString", 0))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (ConstLoc, prop_const), (ProtoLoc, prop_proto)
  )

  // lset_this : location set of regular expression objects
  // argVal: argument string
  // l_r: location of the return object
  def exec(h: Heap, ctx: Context, he: Heap, ctxe: Context, lset_this: LocSet, argVal: AbsString, l_r: Loc, addr1: Address): ((Heap, Context), (Heap, Context)) = {
    val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
    val lset_2 = lset_this.filter(l => {
      val s = h(l)("@class")._1._2._1._5
      AbsString.alpha("RegExp") != s && s </ StrBot
    })

    // if 'this' object is an object whose [[class]] is a 'RegExp'
    val (h_4, ctx_4) =
      if (!lset_1.isEmpty) {
        val l = lset_1.head
        val src = h(l)("source")._1._1._1._1._5.getSingle
        val b_g = h(l)("global")._1._1._1._1._3.getSingle
        val b_i = h(l)("ignoreCase")._1._1._1._1._3.getSingle
        val b_m = h(l)("multiline")._1._1._1._1._3.getSingle
        val idx = Operator.ToInteger(h(l)("lastIndex")._1._1._1).getSingle
        val s_1 = argVal.gamma

        val (h_3, ctx_3) = (lset_1.size, src, b_g, b_i, b_m, idx, s_1) match {
          case (1, Some(source), Some(g), Some(i), Some(m), Some(lastIdx), Some(argset)) => {
            val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")

            val (matcher, _, _, _, _) = JSRegExpSolver.parse(source, flags)

            val lastIdx_ : Int = if (g) lastIdx.toInt else 0

            argset.foldLeft((HeapBot, ContextBot))((hc, arg) => {
              val (array, lastIndex, index, length) = JSRegExpSolver.exec(matcher, arg, lastIdx_)

              // XXX: Need to check the semantics of [[Put]] internal method.
              val h_1 =
                if (g) Helper.PropStore(h, l, AbsString.alpha("lastIndex"), Value(AbsNumber.alpha(lastIndex)))
                else h

              val (h_2, ctx_1) = array match {
                case Some(_) => Helper.Oldify(h_1, ctx, addr1)
                case None => (h_1, ctx)
              }

              val (h_4, ctx_4) = array match {
                case Some(arr) => {
                  val newobj = Helper.NewArrayObject(AbsNumber.alpha(length))
                    .update("index", PropValue(ObjectValue(AbsNumber.alpha(index), T, T, T)))
                    .update("input", PropValue(ObjectValue(argVal, T, T, T)))

                  val newobj_1 = (0 to length - 1).foldLeft(newobj)((no, i) => {
                    val v = arr(i) match {
                      case Some(s) => Value(AbsString.alpha(s))
                      case None => Value(UndefTop)
                    }
                    no.update(AbsString.alpha(i.toString), PropValue(ObjectValue(v, T, T, T)))
                  })
                  val h_3 = h_2.update(l_r, newobj_1)
                  (Helper.ReturnStore(h_3, Value(l_r)), ctx_1)
                }
                case None => {
                  (Helper.ReturnStore(h_2, Value(NullTop)), ctx_1)
                }
              }
              (hc._1 + h_4, hc._2 + ctx_4)
            })
          }
          case _ => {
            // argument value
            if (argVal </ StrBot) {
              val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
              val newobj = Helper.NewArrayObject(UInt)
                .update("index", PropValue(ObjectValue(UInt, T, T, T)))
                .update("input", PropValue(ObjectValue(argVal, T, T, T)))
                .update("@default_number", PropValue(ObjectValue(Value(StrTop) + Value(UndefTop), T, T, T)))

              val h_2 = lset_1.foldLeft(h_1)((h_1_, l) => Helper.PropStore(h_1_, l, AbsString.alpha("lastIndex"), Value(UInt)))
              val h_3 = h_2.update(l_r, newobj)
              (Helper.ReturnStore(h_3, Value(l_r) + Value(NullTop)), ctx_1)
            } else {
              (HeapBot, ContextBot)
            }
          }
        }
        (h_3, ctx_3)
      } else {
        (HeapBot, ContextBot)
      }


    // if 'this' object is not an object whose [[class]] is a 'RegExp', throw a TypeError exception.
    val es =
      if (!lset_2.isEmpty) HashSet[Exception](TypeError)
      else ExceptionBot
    val (he_1, ctxe_1) = Helper.RaiseException(h, ctx, es)

    ((h_4, ctx_4), (he + he_1, ctxe + ctxe_1))


  }

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "RegExp" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // API address allocation
          val lset_callee = getArgValue(h, ctx, args, "callee")._2
          val abstraction = lset_callee.size > 1
          
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)

          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 = getArgValue(h, ctx, args, "1")

          // case for pattern is undefined.
          val p_1 =
            if (v_1._1._1 </ UndefBot) AbsString.alpha("")
            else StrBot
          // case for flags is undefined.
          val f_1 =
            if (v_2._1._1 </ UndefBot) AbsString.alpha("")
            else StrBot

          // case for pattern is an object whose [[class]] is RegExp.
          val lset_1 = v_1._2.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
          // case for pattern is an object whose [[class]] is not a RegExp.
          val lset_2 = v_1._2.filter(l => {
            val s = h(l)("@class")._1._2._1._5
            AbsString.alpha("RegExp") != s && s </ StrBot
          })

          // case for pattern is a value which is not an undefined or an object whose [[class] is not a RegExp.
          val v_1_ = Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), lset_2)
          val p_3 = Helper.toString(Helper.toPrimitive_better(h, v_1_))
          // case for flags is a value which is not an undefined or an object.
          val v_2_ = Value(PValue(UndefBot, v_2._1._2, v_2._1._3, v_2._1._4, v_2._1._5), v_2._2)
          val f_2 = Helper.toString(Helper.toPrimitive_better(h, v_2_))

          // If pattern is an object R whose [[Class] internal property is "RegExp" and
          // flags is not undefined, then throw a "TypeError" exception.
          val es_1 = if (!lset_1.isEmpty && !(v_2_ <= ValueBot)) {
            HashSet[Exception](TypeError)
          } else {
            ExceptionBot
          }

          // case for pattern is a value or an object whose [[class]] is not a RegExp.
          val p = p_1 + p_3
          // case for flags is a value or an object.
          val f = f_1 + f_2

          val (oo, es_2) = (p.gamma, f.gamma) match {
            case (Some(patternSet), Some(flagsSet)) =>
              var obj: Obj = ObjBot
              var exc = ExceptionBot
              for(pattern <- patternSet) for(flags <- flagsSet) {
                val s =
                  if (pattern == "") "(?:)"
                  else pattern

                try {
                  val (_, b_g, b_i, b_m, _) = JSRegExpSolver.parse(s, flags)

                  obj += Helper.NewRegExp(AbsString.alpha(s), AbsBool.alpha(b_g), AbsBool.alpha(b_i), AbsBool.alpha(b_m))
                } catch {
                  case e: SyntaxErrorException =>
                    if (Config.typingInterface != null)
                      if (Shell.params.opt_DeveloperMode || !abstraction)
                        e.getKind match {
                          case ESyntax => ()
                          case ERegExp4_1_1 => Config.typingInterface.signal(null, RegExp4_1_1, e.getMsg1, e.getMsg2)
                          case ERegExp4_1_2 => Config.typingInterface.signal(null, RegExp4_1_2, e.getMsg1, e.getMsg2)
                          case ERegExp2_5 => Config.typingInterface.signal(null, RegExp2_5, e.getMsg1, e.getMsg2)
                          case ERegExp2_15_2 => Config.typingInterface.signal(null, RegExp2_15_2, e.getMsg1, e.getMsg2)
                        }
                    exc = HashSet[Exception](SyntaxError)
                  case e: InternalError => throw e
                }
              }
              (Some(obj), exc)

            case _ if p </ StrBot && f </ StrBot => (Some(Helper.NewRegExp(p, BoolTop, BoolTop, BoolTop)), HashSet[Exception](SyntaxError))
            case _ => (None, ExceptionBot)
          }

          val (h_1, ctx_1) = oo match {
            case Some(o) => {
              val (h_1_, ctx_1_) = Helper.Oldify(h, ctx, addr1)
              val h_2_ = h_1_.update(l_r, o)
              (h_2_, ctx_1_)
            }
            case None => (h, ctx)
          }

          val v_rtn_1 = oo match {
            case Some(o) => Value(l_r)
            case None => ValueBot
          }
          val v_rtn_2 =
            if ((!lset_1.isEmpty) && v_2._1._1 </ UndefBot) Value(lset_1)
            else ValueBot

          val v_rtn = v_rtn_1 + v_rtn_2

          val (h_2, ctx_2) =
            if (v_rtn </ ValueBot) (Helper.ReturnStore(h_1, v_rtn), ctx_1)
            else (HeapBot, ContextBot)

          val es = es_1 ++ es_2
          val (he_1, ctxe_1) = Helper.RaiseException(h, ctx, es)
          ((h_2, ctx_2), (he + he_1, ctxe + ctxe_1))
        }),
      "RegExp.constructor" -> (
      (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
        val lset_callee = getArgValue(h, ctx, args, "callee")._2
        val abstraction = lset_callee.size > 1
        val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
        val v_1 = getArgValue(h, ctx, args, "0")
        val v_2 = getArgValue(h, ctx, args, "1")

        // case for pattern is undefined.
        val p_1 =
          if (v_1._1._1 </ UndefBot) AbsString.alpha("")
          else StrBot
        // case for flags is undefined.
        val f_1 =
          if (v_2._1._1 </ UndefBot) AbsString.alpha("")
          else StrBot

        // case for pattern is an object whose [[class]] is RegExp.
        val lset_1 = v_1._2.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
        val p_2 = lset_1.foldLeft[AbsString](StrBot)((s, l) => s + h(l)("source")._1._1._1._1._5)

        // case for pattern is an object whose [[class]] is not a RegExp.
        val lset_2 = v_1._2.filter(l => {
          val s = h(l)("@class")._1._2._1._5
          AbsString.alpha("RegExp") != s && s </ StrBot
        })

        // case for pattern is a value which is not an undefined or an object whose [[class] is not a RegExp.
        val v_1_ = Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), lset_2)
        val p_3 = Helper.toString(Helper.toPrimitive_better(h, v_1_))
        // case for flags is a value which is not an undefined or an object.
        val v_2_ = Value(PValue(UndefBot, v_2._1._2, v_2._1._3, v_2._1._4, v_2._1._5), v_2._2)
        val f_2 = Helper.toString(Helper.toPrimitive_better(h, v_2_))

        // If pattern is an object R whose [[Class] internal property is "RegExp" and
        // flags is not undefined, then throw a "TypeError" exception.
        val es_1 = if (!lset_1.isEmpty && !(v_2_ <= ValueBot)) {
          HashSet[Exception](TypeError)
        } else {
          ExceptionBot
        }

        // case for pattern is a value or an object
        val p = p_1 + p_2 + p_3
        // case for flags is a value or an object.
        val f = f_1 + f_2

        val (oo, es_2) = (p.gamma, f.gamma) match {
          case (Some(patternSet), Some(flagsSet)) =>
            try {
              var obj: Obj = ObjBot
              for(pattern <- patternSet) for(flags <- flagsSet) {
                val s =
                  if (pattern == "") "(?:)"
                  else pattern
                val (_, b_g, b_i, b_m, _) = JSRegExpSolver.parse(s, flags)

                obj+= Helper.NewRegExp(AbsString.alpha(s), AbsBool.alpha(b_g), AbsBool.alpha(b_i), AbsBool.alpha(b_m))
              }
              (Some(obj), ExceptionBot)
            } catch {
              case e: SyntaxErrorException =>
                if (Config.typingInterface != null)
                  if (Shell.params.opt_DeveloperMode || !abstraction)
                    e.getKind match {
                      case ESyntax => ()
                      case ERegExp4_1_1 => Config.typingInterface.signal(null, RegExp4_1_1, e.getMsg1, e.getMsg2)
                      case ERegExp4_1_2 => Config.typingInterface.signal(null, RegExp4_1_2, e.getMsg1, e.getMsg2)
                      case ERegExp2_5 => Config.typingInterface.signal(null, RegExp2_5, e.getMsg1, e.getMsg2)
                      case ERegExp2_15_2 => Config.typingInterface.signal(null, RegExp2_15_2, e.getMsg1, e.getMsg2)
                    }
                (None, HashSet[Exception](SyntaxError))
              case _ => (None, HashSet[Exception](SyntaxError))
            }
          case _ if p </ StrBot && f </ StrBot => (Some(Helper.NewRegExp(p, BoolTop, BoolTop, BoolTop)), HashSet[Exception](SyntaxError))
          case _ => (None, ExceptionBot)
        }

        val (h_1, ctx_1) = oo match {
          case Some(o) => {
            val h_1_ = lset_this.foldLeft(h)((h_, l) => h_.update(l, o))
            (h_1_, ctx)
          }
          case _ => (HeapBot, ContextBot)
        }

        val es = es_1 ++ es_2
        val (he_1, ctxe_1) = Helper.RaiseException(h, ctx, es)
        ((h_1, ctx_1), (he + he_1, ctxe + ctxe_1))
      }),
      // imprecise semantics
      "RegExp.prototype.exec" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // allocate new location
          val v_1 = getArgValue(h, ctx, args, "0")
          val argVal = Helper.toString(Helper.toPrimitive_better(h, v_1))
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          
          exec(h, ctx, he, ctxe, lset_this, argVal, l_r, addr1)
          /*
          val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
          val lset_2 = lset_this.filter(l => {
            val s = h(l)("@class")._1._2._1._5
            AbsString.alpha("RegExp") != s && s </ StrBot
          })

          // if 'this' object is an object whose [[class]] is a 'RegExp'
          val (h_4, ctx_4) =
            if (!lset_1.isEmpty) {
              val l = lset_1.head
              val src = h(l)("source")._1._1._1._1._5.getSingle
              val b_g = h(l)("global")._1._1._1._1._3.getSingle
              val b_i = h(l)("ignoreCase")._1._1._1._1._3.getSingle
              val b_m = h(l)("multiline")._1._1._1._1._3.getSingle
              val idx = Operator.ToInteger(h(l)("lastIndex")._1._1._1).getSingle
              val s_1 = argVal.getSingle

              val (h_3, ctx_3) = (lset_1.size, src, b_g, b_i, b_m, idx, s_1) match {
                case (1, Some(source), Some(g), Some(i), Some(m), Some(lastIdx), Some(arg)) => {
                  val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")

                  val (matcher, _, _, _, _) = JSRegExpSolver.parse(source, flags)

                  val lastIdx_ : Int = if (g) lastIdx.toInt else 0
                  val (array, lastIndex, index, length) = JSRegExpSolver.exec(matcher, arg, lastIdx_)

                  // XXX: Need to check the semantics of [[Put]] internal method.
                  val h_1 =
                    if (g) Helper.PropStore(h, l, AbsString.alpha("lastIndex"), Value(AbsNumber.alpha(lastIndex)))
                    else h

                  val (h_2, ctx_1) = array match {
                    case Some(_) => Helper.Oldify(h_1, ctx, addr1)
                    case None => (h_1, ctx)
                  }

                  array match {
                    case Some(arr) => {
                      val newobj = Helper.NewArrayObject(AbsNumber.alpha(length))
                        .update("index", PropValue(ObjectValue(AbsNumber.alpha(index), T, T, T)))
                        .update("input", PropValue(ObjectValue(argVal, T, T, T)))

                      val newobj_1 = (0 to length - 1).foldLeft(newobj)((no, i) => {
                        val v = arr(i) match {
                          case Some(s) => Value(AbsString.alpha(s))
                          case None => Value(UndefTop)
                        }
                        no.update(AbsString.alpha(i.toString), PropValue(ObjectValue(v, T, T, T)))
                      })
                      val h_3 = h_2.update(l_r, newobj_1)
                      (Helper.ReturnStore(h_3, Value(l_r)), ctx_1)
                    }
                    case None => {
                      (Helper.ReturnStore(h_2, Value(NullTop)), ctx_1)
                    }
                  }
                }
                case _ => {
                  // argument value
                  if (argVal </ StrBot) {
                    val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
                    val newobj = Helper.NewArrayObject(UInt)
                      .update("index", PropValue(ObjectValue(UInt, T, T, T)))
                      .update("input", PropValue(ObjectValue(argVal, T, T, T)))
                      .update("@default_number", PropValue(ObjectValue(Value(StrTop) + Value(UndefTop), T, T, T)))

                    val h_2 = lset_1.foldLeft(h_1)((h_1_, l) => Helper.PropStore(h_1_, l, AbsString.alpha("lastIndex"), Value(UInt)))
                    val h_3 = h_2.update(l_r, newobj)
                    (Helper.ReturnStore(h_3, Value(l_r) + Value(NullTop)), ctx_1)
                  } else {
                    (HeapBot, ContextBot)
                  }
                }
              }
              (h_3, ctx_3)
            } else {
              (HeapBot, ContextBot)
            }


          // if 'this' object is not an object whose [[class]] is a 'RegExp', throw a TypeError exception.
          val es =
            if (!lset_2.isEmpty) HashSet[Exception](TypeError)
            else ExceptionBot
          val (he_1, ctxe_1) = Helper.RaiseException(h, ctx, es)

          ((h_4, ctx_4), (he + he_1, ctxe + ctxe_1)) */
        }),
      "RegExp.prototype.test" -> (
       (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
         val v_1 = getArgValue(h, ctx, args, "0")
         val argVal = Helper.toString(Helper.toPrimitive_better(h, v_1))
         val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
         val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
         val lset_2 = lset_this.filter(l => {
           val cls = h(l)("@class")._1._2._1._5
           AbsString.alpha("RegExp") != cls && cls </ StrBot
         })

         // if 'this' object is an object whose [[class]] is a 'RegExp'
         val (h_4, ctx_4) =
           if (!lset_1.isEmpty) {
             val l = lset_1.head
             val a_src = h(l)("source")._1._1._1._1._5
             val a_g = h(l)("global")._1._1._1._1._3
             val a_i = h(l)("ignoreCase")._1._1._1._1._3
             val a_m = h(l)("multiline")._1._1._1._1._3
             val a_idx = h(l)("lastIndex")._1._1._1
             val src = a_src.getSingle
             val b_g = a_g.getSingle
             val b_i = a_i.getSingle
             val b_m = a_m.getSingle
             val idx = Operator.ToInteger(a_idx).getSingle
             val s_1 = argVal.getSingle

             val (a_lastidx, b_rtn) = (lset_1.size, src, b_g, b_i, b_m, idx, s_1) match {
               // case for a concrete input.
               case (1, Some(source), Some(g), Some(i), Some(m), Some(lastIdx), Some(arg)) => {
                 val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")

                 val (matcher, _, _, _, _) = JSRegExpSolver.parse(source, flags)

                 val lastIdx_ : Int = if (g) lastIdx.toInt else 0
                 val (array, lastIndex, _, _) = JSRegExpSolver.exec(matcher, arg, lastIdx_)

                 val absLastIndex = AbsNumber.alpha(lastIndex)

                 val b_rtn_ = array match {
                   case Some(_) => BoolTrue
                   case _ => BoolFalse
                 }
                 (absLastIndex, b_rtn_)
               }
               // case for an abstract input which is not a bottom.
               case _ if a_src </ StrBot && a_g </ BoolBot && a_i </ BoolBot && a_m </ BoolBot && a_idx </ ValueBot && argVal </ StrBot => (UInt, BoolTop)
               // otherwise.
               case _ => (NumBot, BoolBot)
             }

             val a_g_ = lset_1.foldLeft(AbsBool.bot)((b, l) => b + h(l)("global")._1._1._1._1._3)

             // XXX: Need to check the semantics of [[Put]] internal method.
             val h_2 =
               if (BoolTrue <= a_g_) lset_1.foldLeft(h)((h_, l) => Helper.PropStore(h_, l, AbsString.alpha("lastIndex"), Value(a_lastidx)))
               else h

             val (h_3, ctx_3) =
               if (b_rtn </ BoolBot) {
                 (Helper.ReturnStore(h_2, Value(b_rtn)), ctx)
               } else {
                 (HeapBot, ContextBot)
               }

             (h_3, ctx_3)
           } else {
             (HeapBot, ContextBot)
           }

         val es =
           if (!lset_2.isEmpty) HashSet[Exception](TypeError)
           else ExceptionBot
         val (he_1, ctxe_1) = Helper.RaiseException(h, ctx, es)

         ((h_4, ctx_4), (he + he_1, ctxe + ctxe_1))
       }),
      "RegExp.prototype.toString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2

          val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
          val lset_2 = lset_this.filter(l => AbsString.alpha("RegExp") </ h(l)("@class")._1._2._1._5)

          val s_rtn = Helper.defaultToString(h, lset_1)

          val (h_1, ctx_1) =
            if (s_rtn </ StrBot) (Helper.ReturnStore(h, Value(s_rtn)), ctx)
            else (HeapBot, ContextBot)

          val es =
            if (!lset_2.isEmpty) HashSet[Exception](TypeError)
            else ExceptionBot
          val (he_1, ctxe_1) = Helper.RaiseException(h, ctx, es)

          ((h_1, ctx_1), (he + he_1, ctxe + ctxe_1))
        })
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "RegExp" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          // API address allocation
          val lset_env = h(PureLocalLoc)("@env")._1._2._2
          val lset_callee = getArgValue_pre(h, ctx, args, "callee", PureLocalLoc)._2
          val abstraction = lset_callee.size > 1
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)

          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val v_2 = getArgValue_pre(h, ctx, args, "1", PureLocalLoc)

          // case for pattern is undefined.
          val p_1 =
            if (v_1._1._1 </ UndefBot) AbsString.alpha("")
            else StrBot
          // case for flags is undefined.
          val f_1 =
            if (v_2._1._1 </ UndefBot) AbsString.alpha("")
            else StrBot

          // case for pattern is an object whose [[class]] is RegExp.
          val lset_1 = v_1._2.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
          // case for pattern is an object whose [[class]] is not a RegExp.
          val lset_2 = v_1._2.filter(l => {
            val s = h(l)("@class")._1._2._1._5
            AbsString.alpha("RegExp") != s && s </ StrBot
          })

          // case for pattern is a value which is not an undefined or an object whose [[class] is not a RegExp.
          val v_1_ = Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), lset_2)
          val p_3 = PreHelper.toString(PreHelper.toPrimitive(v_1_))
          // case for flags is a value which is not an undefined or an object.
          val v_2_ = Value(PValue(UndefBot, v_2._1._2, v_2._1._3, v_2._1._4, v_2._1._5), v_2._2)
          val f_2 = PreHelper.toString(PreHelper.toPrimitive(v_2_))

          // If pattern is an object R whose [[Class] internal property is "RegExp" and
          // flags is not undefined, then throw a "TypeError" exception.
          val es_1 = if (!lset_1.isEmpty && !(v_2_ <= ValueBot)) {
            HashSet[Exception](TypeError)
          } else {
            ExceptionBot
          }

          // case for pattern is a value or an object whose [[class]] is not a RegExp.
          val p = p_1 + p_3
          // case for flags is a value or an object.
          val f = f_1 + f_2

          val (oo, es_2) = (p.gamma, f.gamma) match {
            case (Some(patternSet), Some(flagsSet)) => {
              try {
                var obj: Obj = ObjBot
                for(pattern <- patternSet) for(flags <- flagsSet) {
                  val s =
                    if (pattern == "") "(?:)"
                    else pattern
                  val (_, b_g, b_i, b_m, _) = JSRegExpSolver.parse(s, flags)

                  obj+= Helper.NewRegExp(AbsString.alpha(s), AbsBool.alpha(b_g), AbsBool.alpha(b_i), AbsBool.alpha(b_m))
                }
                (Some(obj), ExceptionBot)
              } catch {
                case e: SyntaxErrorException =>
                  if (Config.typingInterface != null)
                    if (Shell.params.opt_DeveloperMode || !abstraction)
                      e.getKind match {
                        case ESyntax => ()
                        case ERegExp4_1_1 => Config.typingInterface.signal(null, RegExp4_1_1, e.getMsg1, e.getMsg2)
                        case ERegExp4_1_2 => Config.typingInterface.signal(null, RegExp4_1_2, e.getMsg1, e.getMsg2)
                        case ERegExp2_5 => Config.typingInterface.signal(null, RegExp2_5, e.getMsg1, e.getMsg2)
                        case ERegExp2_15_2 => Config.typingInterface.signal(null, RegExp2_15_2, e.getMsg1, e.getMsg2)
                      }
                  (None, HashSet[Exception](SyntaxError))
                case _ => (None, HashSet[Exception](SyntaxError))
              }
            }
            case _ if p </ StrBot && f </ StrBot => (Some(Helper.NewRegExp(p, BoolTop, BoolTop, BoolTop)), HashSet[Exception](SyntaxError))
            case _ => (None, ExceptionBot)
          }

          val (h_1, ctx_1) = oo match {
            case Some(o) => {
              val (h_1_, ctx_1_) = PreHelper.Oldify(h, ctx, addr1)
              val h_2_ = h_1_.update(l_r, o)
              (h_2_, ctx_1_)
            }
            case None => (h, ctx)
          }

          val v_rtn_1 = oo match {
            case Some(o) => Value(l_r)
            case None => ValueBot
          }
          val v_rtn_2 =
            if ((!lset_1.isEmpty) && v_2._1._1 </ UndefBot) Value(lset_1)
            else ValueBot

          val v_rtn = v_rtn_1 + v_rtn_2

          val (h_2, ctx_2) =
            if (v_rtn </ ValueBot) (PreHelper.ReturnStore(h_1, PureLocalLoc, v_rtn), ctx_1)
            else (HeapBot, ContextBot)

          val es = es_1 ++ es_2
          val (he_1, ctxe_1) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
          ((h_2, ctx_2), (he + he_1, ctxe + ctxe_1))
        }),
      "RegExp.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          val lset_callee = getArgValue_pre(h, ctx, args, "callee", PureLocalLoc)._2
          val abstraction = lset_callee.size > 1
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val v_2 = getArgValue_pre(h, ctx, args, "1", PureLocalLoc)

          // case for pattern is undefined.
          val p_1 =
            if (v_1._1._1 </ UndefBot) AbsString.alpha("")
            else StrBot
          // case for flags is undefined.
          val f_1 =
            if (v_2._1._1 </ UndefBot) AbsString.alpha("")
            else StrBot

          // case for pattern is an object whose [[class]] is RegExp.
          val lset_1 = v_1._2.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
          val p_2 = lset_1.foldLeft[AbsString](StrBot)((s, l) => s + h(l)("source")._1._1._1._1._5)

          // case for pattern is an object whose [[class]] is not a RegExp.
          val lset_2 = v_1._2.filter(l => {
            val s = h(l)("@class")._1._2._1._5
            AbsString.alpha("RegExp") != s && s </ StrBot
          })

          // case for pattern is a value which is not an undefined or an object whose [[class] is not a RegExp.
          val v_1_ = Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), lset_2)
          val p_3 = PreHelper.toString(PreHelper.toPrimitive(v_1_))
          // case for flags is a value which is not an undefined or an object.
          val v_2_ = Value(PValue(UndefBot, v_2._1._2, v_2._1._3, v_2._1._4, v_2._1._5), v_2._2)
          val f_2 = PreHelper.toString(PreHelper.toPrimitive(v_2_))

          // If pattern is an object R whose [[Class] internal property is "RegExp" and
          // flags is not undefined, then throw a "TypeError" exception.
          val es_1 = if (!lset_1.isEmpty && !(v_2_ <= ValueBot)) {
            HashSet[Exception](TypeError)
          } else {
            ExceptionBot
          }

          // case for pattern is a value or an object
          val p = p_1 + p_2 + p_3
          // case for flags is a value or an object.
          val f = f_1 + f_2

          val (oo, es_2) = (p.gamma, f.gamma) match {
            case (Some(patternSet), Some(flagsSet)) => {
              try {
                var obj: Obj = ObjBot
                for(pattern <- patternSet) for(flags <- flagsSet) {
                  val s =
                    if (pattern == "") "(?:)"
                    else pattern
                  val (_, b_g, b_i, b_m, _) = JSRegExpSolver.parse(s, flags)

                  obj+= Helper.NewRegExp(AbsString.alpha(s), AbsBool.alpha(b_g), AbsBool.alpha(b_i), AbsBool.alpha(b_m))
                }
                (Some(obj), ExceptionBot)
              } catch {
                case e: SyntaxErrorException =>
                  if (Config.typingInterface != null)
                    if (Shell.params.opt_DeveloperMode || !abstraction)
                      e.getKind match {
                        case ESyntax => ()
                        case ERegExp4_1_1 => Config.typingInterface.signal(null, RegExp4_1_1, e.getMsg1, e.getMsg2)
                        case ERegExp4_1_2 => Config.typingInterface.signal(null, RegExp4_1_2, e.getMsg1, e.getMsg2)
                        case ERegExp2_5 => Config.typingInterface.signal(null, RegExp2_5, e.getMsg1, e.getMsg2)
                        case ERegExp2_15_2 => Config.typingInterface.signal(null, RegExp2_15_2, e.getMsg1, e.getMsg2)
                      }
                  (None, HashSet[Exception](SyntaxError))
                case _ => (None, HashSet[Exception](SyntaxError))
              }
            }
            case _ if p </ StrBot && f </ StrBot => (Some(Helper.NewRegExp(p, BoolTop, BoolTop, BoolTop)), HashSet[Exception](SyntaxError))
            case _ => (None, ExceptionBot)
          }

          val (h_1, ctx_1) = oo match {
            case Some(o) => {
              val h_1_ = lset_this.foldLeft(h)((h_, l) => h_.update(l, o))
              (h_1_, ctx)
            }
            case _ => (HeapBot, ContextBot)
          }

          val es = es_1 ++ es_2
          val (he_1, ctxe_1) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
          ((h_1, ctx_1), (he + he_1, ctxe + ctxe_1))
        }),
//      // imprecise semantics
      "RegExp.prototype.exec" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          // allocate new location
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val argVal = PreHelper.toString(PreHelper.toPrimitive(v_1))
          val lset_env = h(PureLocalLoc)("@env")._1._2._2
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)

          val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
          val lset_2 = lset_this.filter(l => {
            val s = h(l)("@class")._1._2._1._5
            AbsString.alpha("RegExp") != s && s </ StrBot
          })

          // if 'this' object is an object whose [[class]] is a 'RegExp'
          val (h_4, ctx_4) =
            if (!lset_1.isEmpty) {
              val l = lset_1.head
              val src = h(l)("source")._1._1._1._1._5.getSingle
              val b_g = h(l)("global")._1._1._1._1._3.getSingle
              val b_i = h(l)("ignoreCase")._1._1._1._1._3.getSingle
              val b_m = h(l)("multiline")._1._1._1._1._3.getSingle
              val idx = Operator.ToInteger(h(l)("lastIndex")._1._1._1).getSingle
              val s_1 = argVal.getSingle

              val (h_3, ctx_3) = (lset_1.size, src, b_g, b_i, b_m, idx, s_1) match {
                case (1, Some(source), Some(g), Some(i), Some(m), Some(lastIdx), Some(arg)) => {
                  val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")

                  val (matcher, _, _, _, _) = JSRegExpSolver.parse(source, flags)

                  val lastIdx_ : Int = if (g) lastIdx.toInt else 0
                  val (array, lastIndex, index, length) = JSRegExpSolver.exec(matcher, arg, lastIdx_)

                  // XXX: Need to check the semantics of [[Put]] internal method.
                  val h_1 =
                    if (g) PreHelper.PropStore(h, l, AbsString.alpha("lastIndex"), Value(AbsNumber.alpha(lastIndex)))
                    else h

                  val (h_2, ctx_1) = array match {
                    case Some(_) => PreHelper.Oldify(h_1, ctx, addr1)
                    case None => (h_1, ctx)
                  }

                  array match {
                    case Some(arr) => {
                      val newobj = PreHelper.NewArrayObject(AbsNumber.alpha(length))
                        .update("index", PropValue(ObjectValue(AbsNumber.alpha(index), T, T, T)))
                        .update("input", PropValue(ObjectValue(argVal, T, T, T)))

                      val newobj_1 = (0 to length - 1).foldLeft(newobj)((no, i) => {
                        val v = arr(i) match {
                          case Some(s) => Value(AbsString.alpha(s))
                          case None => Value(UndefTop)
                        }
                        no.update(AbsString.alpha(i.toString), PropValue(ObjectValue(v, T, T, T)))
                      })
                      val h_3 = h_2.update(l_r, newobj_1)
                      (PreHelper.ReturnStore(h_3, PureLocalLoc, Value(l_r)), ctx_1)
                    }
                    case None => {
                      (PreHelper.ReturnStore(h_2, PureLocalLoc, Value(NullTop)), ctx_1)
                    }
                  }
                }
                case _ => {
                  // argument value
                  if (argVal </ StrBot) {
                    val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)
                    val newobj = PreHelper.NewArrayObject(UInt)
                      .update("index", PropValue(ObjectValue(UInt, T, T, T)))
                      .update("input", PropValue(ObjectValue(argVal, T, T, T)))
                      .update("@default_number", PropValue(ObjectValue(Value(StrTop) + Value(UndefTop), T, T, T)))

                    val h_2 = PreHelper.PropStore(h_1, l, AbsString.alpha("lastIndex"), Value(UInt))
                    val h_3 = h_2.update(l_r, newobj)
                    (PreHelper.ReturnStore(h_3, PureLocalLoc, Value(l_r) + Value(NullTop)), ctx_1)
                  } else {
                    (HeapBot, ContextBot)
                  }
                }
              }
              (h_3, ctx_3)
            } else {
              (HeapBot, ContextBot)
            }


          // if 'this' object is not an object whose [[class]] is a 'RegExp', throw a TypeError exception.
          val es =
            if (!lset_2.isEmpty) HashSet[Exception](TypeError)
            else ExceptionBot
          val (he_1, ctxe_1) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)

          ((h_4, ctx_4), (he + he_1, ctxe + ctxe_1))
        }),
      "RegExp.prototype.test" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue(h, ctx, args, "0")
          val argVal = PreHelper.toString(PreHelper.toPrimitive(v_1))
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
          val lset_2 = lset_this.filter(l => {
            val cls = h(l)("@class")._1._2._1._5
            AbsString.alpha("RegExp") != cls && cls </ StrBot
          })

          // if 'this' object is an object whose [[class]] is a 'RegExp'
          val (h_4, ctx_4) =
            if (!lset_1.isEmpty) {
              val l = lset_1.head
              val a_src = h(l)("source")._1._1._1._1._5
              val a_g = h(l)("global")._1._1._1._1._3
              val a_i = h(l)("ignoreCase")._1._1._1._1._3
              val a_m = h(l)("multiline")._1._1._1._1._3
              val a_idx = h(l)("lastIndex")._1._1._1
              val src = a_src.getSingle
              val b_g = a_g.getSingle
              val b_i = a_i.getSingle
              val b_m = a_m.getSingle
              val idx = Operator.ToInteger(a_idx).getSingle
              val s_1 = argVal.getSingle

              val (a_lastidx, b_rtn) = (lset_1.size, src, b_g, b_i, b_m, idx, s_1) match {
                // case for a concrete input.
                case (1, Some(source), Some(g), Some(i), Some(m), Some(lastIdx), Some(arg)) => {
                  val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")

                  val (matcher, _, _, _, _) = JSRegExpSolver.parse(source, flags)

                  val lastIdx_ : Int = if (g) lastIdx.toInt else 0
                  val (array, lastIndex, _, _) = JSRegExpSolver.exec(matcher, arg, lastIdx_)

                  val absLastIndex = AbsNumber.alpha(lastIndex)

                  val b_rtn_ = array match {
                    case Some(_) => BoolTrue
                    case _ => BoolFalse
                  }
                  (absLastIndex, b_rtn_)
                }
                // case for an abstract input which is not a bottom.
                case _ if a_src </ StrBot && a_g </ BoolBot && a_i </ BoolBot && a_m </ BoolBot && a_idx </ ValueBot && argVal </ StrBot => (UInt, BoolTop)
                // otherwise.
                case _ => (NumBot, BoolBot)
              }

              // XXX: Need to check the semantics of [[Put]] internal method.
              val h_2 =
                if (BoolTrue <= a_g) PreHelper.PropStore(h, l, AbsString.alpha("lastIndex"), Value(a_lastidx))
                else h

              val (h_3, ctx_3) =
                if (b_rtn </ BoolBot) {
                  (PreHelper.ReturnStore(h_2, PureLocalLoc, Value(b_rtn)), ctx)
                } else {
                  (HeapBot, ContextBot)
                }

              (h_3, ctx_3)
            } else {
              (HeapBot, ContextBot)
            }

          val es =
            if (!lset_2.isEmpty) HashSet[Exception](TypeError)
            else ExceptionBot
          val (he_1, ctxe_1) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)

          ((h_4, ctx_4), (he + he_1, ctxe + ctxe_1))
        }),
      "RegExp.prototype.toString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2

          val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
          val lset_2 = lset_this.filter(l => AbsString.alpha("RegExp") </ h(l)("@class")._1._2._1._5)

          val s_rtn = Helper.defaultToString(h, lset_1)

          val (h_1, ctx_1) =
            if (s_rtn </ StrBot) (PreHelper.ReturnStore(h, PureLocalLoc, Value(s_rtn)), ctx)
            else (HeapBot, ContextBot)

          val es =
            if (!lset_2.isEmpty) HashSet[Exception](TypeError)
            else ExceptionBot
          val (he_1, ctxe_1) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)

          ((h_1, ctx_1), (he + he_1, ctxe + ctxe_1))
        })
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      "RegExp" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          // API address allocation
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + cfg.getAPIAddress((fid, locToAddr(l)), 0))

          val es_1 = HashSet[Exception](TypeError)
          val es_2 = HashSet[Exception](SyntaxError)

          val es = es_1 ++ es_2
          val LP_1 = set_addr.foldLeft(LPBot)((S, addr1) => S ++ AH.Oldify_def(h, ctx, addr1))
          val LP_2 = set_addr.foldLeft(LPBot)((S, addr1) => {
            val l_r = addrToLoc(addr1, Recent)
            S ++ AH.NewRegExp_def.foldLeft(LPBot)((S, p) => S + ((l_r, p)))
          })
          val LP_3 = LPSet(SinglePureLocalLoc, "@return")
          val LP_4 = AH.RaiseException_def(es)

          LP_1 ++ LP_2 ++ LP_3 ++ LP_4
        }),
      "RegExp.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2

          val es_1 = HashSet[Exception](TypeError)
          val es_2 = HashSet[Exception](SyntaxError)

          val es = es_1 ++ es_2

          val LP_1 = lset_this.foldLeft(LPBot)((S, l_this) => {
            AH.NewRegExp_def.foldLeft(S)((S_, p) => S_ + ((l_this, p)))
          })

          val LP_2 = AH.RaiseException_def(es)

          LP_1 ++ LP_2
        }),
      "RegExp.prototype.exec" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          // allocate new location
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + cfg.getAPIAddress((fid, locToAddr(l)), 0))

          val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)

          val props = AH.NewArrayObject_def ++ Set("index", "input")
          val es = HashSet[Exception](TypeError)

          val LP_1 = lset_1.foldLeft(LPBot)((S, l) => S ++ AH.PropStore_def(h, l, AbsString.alpha("lastIndex")))
          val LP_2 = set_addr.foldLeft(LPBot)((S, addr1) => S ++ AH.Oldify_def(h, ctx, addr1))
          val LP_3 = set_addr.foldLeft(LPBot)((S, addr1) => {
            val l_r = addrToLoc(addr1, Recent)
            props.foldLeft(S)((S_, p) => S_ ++ AH.absPair(h, l_r, NumStr) + ((l_r, p)))
          })
          val LP_4 = LPSet(SinglePureLocalLoc, "@return")
          val LP_5 = AH.RaiseException_def(es)

          LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5
        }),
      "RegExp.prototype.test" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)

          val es = HashSet[Exception](TypeError)

          val LP_1 = lset_1.foldLeft(LPBot)((S, l) => S ++ AH.PropStore_def(h, l, AbsString.alpha("lastIndex")))
          val LP_2 = LPSet(SinglePureLocalLoc, "@return")
          val LP_3 = AH.RaiseException_def(es)

          LP_1 ++ LP_2 ++ LP_3
        }),
      "RegExp.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val es = HashSet[Exception](TypeError)

          val LP_1 = LPSet(SinglePureLocalLoc, "@return")
          val LP_2 = AH.RaiseException_def(es)

          LP_1 ++ LP_2
        })
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      "RegExp" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          // API address allocation
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + cfg.getAPIAddress((fid, locToAddr(l)), 0))

          val v_1 = getArgValue(h, ctx, args, "0")

          val es_1 = HashSet[Exception](TypeError)
          val es_2 = HashSet[Exception](SyntaxError)

          val es = es_1 ++ es_2
          val LP_1 = LPSet(SinglePureLocalLoc, "@env")
          val LP_2 = set_addr.foldLeft(LPBot)((S, addr1) => S ++ AH.Oldify_use(h, ctx, addr1))
          val LP_3 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          val LP_4 = v_1._2.foldLeft(LPBot)((S, l) => S + ((l, "@class")))
          val LP_5 = LPSet(SinglePureLocalLoc, "@return")
          val LP_6 = AH.RaiseException_use(es)

          LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LP_6
        }),
      "RegExp.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")

          // case for pattern is an object whose [[class]] is RegExp.
          val lset_1 = v_1._2.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)

          val es_1 = HashSet[Exception](TypeError)
          val es_2 = HashSet[Exception](SyntaxError)
          val es = es_1 ++ es_2

          val LP_1 = LPSet(SinglePureLocalLoc, "@this")
          val LP_2 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          val LP_3 = v_1._2.foldLeft(LPBot)((S, l) => S + ((l, "@class")))
          val LP_4 = lset_1.foldLeft(LPBot)((S, l) => S + ((l, "source")))
          val LP_5 = AH.RaiseException_use(es)

          LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5
        }),
      "RegExp.prototype.exec" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          // allocate new location
          val v_1 = getArgValue(h, ctx, args, "0")
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + cfg.getAPIAddress((fid, locToAddr(l)), 0))

          val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)

          val es = HashSet[Exception](TypeError)

          val LP_1 = getArgValue_use(h, ctx, args, "0")
          val LP_2 = AH.toPrimitive_use(h, v_1)
          val LP_3 = LPSet(Set((SinglePureLocalLoc, "@env"), (SinglePureLocalLoc, "@this")))
          val LP_4 = lset_this.foldLeft(LPBot)((S, l) => S + ((l, "@class")))
          val LP_5 = lset_1.foldLeft(LPBot)((S, l) => {
            val props = List("source", "global", "ignoreCase", "multiline", "lastIndex")
            props.foldLeft(S)((S_, p) => S_ + ((l, p)))
          })
          val LP_6 = lset_1.foldLeft(LPBot)((S, l) => S ++ AH.PropStore_use(h, l, AbsString.alpha("lastIndex")))
          val LP_7 = set_addr.foldLeft(LPBot)((S, addr1) => S ++ AH.Oldify_use(h, ctx, addr1))
          val LP_8 = LPSet(SinglePureLocalLoc, "@return")
          val LP_9 = AH.RaiseException_use(es)

          LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LP_6 ++ LP_7 ++ LP_8 ++ LP_9
        }),
      "RegExp.prototype.test" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
          val es = HashSet[Exception](TypeError)

          val LP_1 = getArgValue_use(h, ctx, args, "0")
          val LP_2 = AH.toPrimitive_use(h, v_1)
          val LP_3 = LPSet(SinglePureLocalLoc, "@this")
          val LP_4 = lset_this.foldLeft(LPBot)((S, l) => S + ((l, "@class")))
          val LP_5 = lset_1.foldLeft(LPBot)((S, l) => {
            val props = List("source", "global", "ignoreCase", "multiline", "lastIndex")
            props.foldLeft(S)((S_, p) => S_ + ((l, p)))
          })
          val LP_6 = lset_1.foldLeft(LPBot)((S, l) => S ++ AH.PropStore_use(h, l, AbsString.alpha("lastIndex")))
          val LP_7 = LPSet(SinglePureLocalLoc, "@return")
          val LP_8 = AH.RaiseException_use(es)

          LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LP_6 ++ LP_7 ++ LP_8
        }),
      "RegExp.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_1 = lset_this.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._1._2._1._5)
          val es = HashSet[Exception](TypeError)

          val LP_1 = LPSet(SinglePureLocalLoc, "@this")
          val LP_2 = lset_this.foldLeft(LPBot)((S, l) => S + ((l, "@class")))
          val LP_3 = AH.defaultToString(h, lset_1)
          val LP_4 = LPSet(SinglePureLocalLoc, "@return")
          val LP_5 = AH.RaiseException_use(es)

          LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5
        })
    )
  }
}
