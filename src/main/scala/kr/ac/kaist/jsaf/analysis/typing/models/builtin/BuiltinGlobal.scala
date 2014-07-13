/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/
package kr.ac.kaist.jsaf.analysis.typing.models.builtin

import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.bug_detector.URIErrorArg
import kr.ac.kaist.jsaf.nodes_util.NodeUtil
import kr.ac.kaist.jsaf.utils.uri.URIHandling

object BuiltinGlobal extends ModelData {

  //val GlobalLoc = newPreDefLoc("Global", Recent)

  private val prop_global: List[(String, AbsProperty)] = List(
    ("@class",             AbsConstValue(PropValue(AbsString.alpha("Object")))), // implementation dependent
    ("@proto",             AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))), // implementation dependent
    ("@extensible",        AbsConstValue(PropValue(BoolTrue))),
    // 15.1.1 Value Properties of the Global Object
    ("NaN",                AbsConstValue(PropValue(ObjectValue(NaN, F, F, F)))),
    ("Infinity",           AbsConstValue(PropValue(ObjectValue(PosInf, F, F, F)))),
    ("undefined",          AbsConstValue(PropValue(ObjectValue(UndefTop, F, F, F)))),
    // 15.1.2 Function Properties of the Global Object
    ("eval",               AbsBuiltinFunc("Global.eval", 1)),
    ("parseInt",           AbsBuiltinFunc("Global.parseInt", 2)),
    ("parseFloat",         AbsBuiltinFunc("Global.parseFloat", 1)),
    ("isNaN",              AbsBuiltinFunc("Global.isNaN", 1)),
    ("isFinite",           AbsBuiltinFunc("Global.isFinite", 1)),
    // 15.1.3 URI Handling Function Properties
    ("decodeURI",          AbsBuiltinFunc("Global.decodeURI", 1)),
    ("decodeURIComponent", AbsBuiltinFunc("Global.decodeURIComponent", 1)),
    ("encodeURI",          AbsBuiltinFunc("Global.encodeURI", 1)),
    ("encodeURIComponent", AbsBuiltinFunc("Global.encodeURIComponent", 1)),
    // builtin objects
    ("Array",              AbsConstValue(PropValue(ObjectValue(BuiltinArray.ConstLoc, T, F, T)))),
    ("Boolean",            AbsConstValue(PropValue(ObjectValue(BuiltinBoolean.ConstLoc, T, F, T)))),
    ("Date",               AbsConstValue(PropValue(ObjectValue(BuiltinDate.ConstLoc, T, F, T)))),
    ("Error",              AbsConstValue(PropValue(ObjectValue(BuiltinError.ErrConstLoc, T, F, T)))),
    ("EvalError",          AbsConstValue(PropValue(ObjectValue(BuiltinError.EvalErrConstLoc, T, F, T)))),
    ("RangeError",         AbsConstValue(PropValue(ObjectValue(BuiltinError.RangeErrConstLoc, T, F, T)))),
    ("ReferenceError",     AbsConstValue(PropValue(ObjectValue(BuiltinError.RefErrConstLoc, T, F, T)))),
    ("SyntaxError",        AbsConstValue(PropValue(ObjectValue(BuiltinError.SyntaxErrConstLoc, T, F, T)))),
    ("TypeError",          AbsConstValue(PropValue(ObjectValue(BuiltinError.TypeErrConstLoc, T, F, T)))),
    ("URIError",           AbsConstValue(PropValue(ObjectValue(BuiltinError.URIErrConstLoc, T, F, T)))),
    ("Function",           AbsConstValue(PropValue(ObjectValue(BuiltinFunction.ConstLoc, T, F, T)))),
    ("JSON",               AbsConstValue(PropValue(ObjectValue(BuiltinJSON.ConstLoc, T, F, T)))),
    ("Math",               AbsConstValue(PropValue(ObjectValue(BuiltinMath.ConstLoc, T, F, T)))),
    ("Number",             AbsConstValue(PropValue(ObjectValue(BuiltinNumber.ConstLoc, T, F, T)))),
    ("Object",             AbsConstValue(PropValue(ObjectValue(BuiltinObject.ConstLoc, T, F, T)))),
    ("RegExp",             AbsConstValue(PropValue(ObjectValue(BuiltinRegExp.ConstLoc, T, F, T)))),
    ("String",             AbsConstValue(PropValue(ObjectValue(BuiltinString.ConstLoc, T, F, T)))),
    // predefined constant variables from IR
    (NodeUtil.varTrue, AbsConstValue(PropValue(ObjectValue(BoolTrue, F, F, F)))),
    (NodeUtil.varOne, AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1.0), F, F, F)))),
    (NodeUtil.freshGlobalName("global"), AbsConstValue(PropValue(ObjectValue(Value(GlobalSingleton), F, F, F))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List((GlobalLoc, prop_global))

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Global.eval" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          System.out.println("* Warning: the 'Global.eval' call is detected during analysis, analysis results may not be sound.")
          // arguments
          val argv = getArgValue(h, ctx, args, "0")
          if(argv._1._5</StrBot){
            val s = Helper.toString(Helper.toPrimitive_better(h, argv))
            val message = s.gamma match {
              case Some(_) => s.toString
              case _ => s.getAbsCase match {
                case AbsTop => "StrTop"
                case AbsBot => "StrBot"
                case _ if s.isAllNums => "NumStr"
                case _ => "OtherStr"
              }
            }
            System.out.println("* Warning: the argument of 'Global.eval' is in the below ...")           
            System.out.println(message)
            // unsound
            ((h, ctx), (he, ctxe))
          }
          else {
            System.out.println("* Warning: the argument of 'Global.eval' is a non-string")           
            System.out.println("* The argument is " + DomainPrinter.printValue(argv))           
            ((Helper.ReturnStore(h, argv), ctx), (he, ctxe))
          }
        })
      ),

      ("Global.parseInt" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // 15.1.2.2 parseInt(string, radix)
          val v_1 = getArgValue(h, ctx, args, "0") /* string */
          val v_2 = getArgValue(h, ctx, args, "1") /* radix */

          val inputString = Helper.toString(Helper.toPrimitive_better(h, v_1))
          // TODO: Simple implementation. Must be revised. Not the same as the original.
          val r = Operator.ToInt32(v_2)

          val value = Operator.parseInt(inputString, r)
          val rtn = Value(value)

          ((Helper.ReturnStore(h, rtn), ctx), (he, ctx))
        })
      ),

      ("Global.parseFloat" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // 15.1.2.2 parseInt(string, radix)
          val v_1 = getArgValue(h, ctx, args, "0") /* string */

          val inputString = Helper.toString(Helper.toPrimitive(v_1))
          // TODO: Simple implementation. Must be revised. Not the same as the original.

          val value = Operator.parseFloat(inputString)
          val rtn = Value(value)

          ((Helper.ReturnStore(h, rtn), ctx), (he, ctx))
        })
        ),

      "Global.decodeURI" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val s = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val lset_callee = getArgValue(h, ctx, args, "callee")._2
          val abstraction = (lset_callee.size > 1)
          val (value, es): (Value, Set[Exception]) = s.gamma match {
            case Some(vs) => {
              vs.foldLeft((ValueBot, ExceptionBot))((r, string) => {
                val rtn = URIHandling.decode(string, URIHandling.decodeURIString)
                val (v_1, es_1) =
                  if (rtn != null) {
                    (Value(AbsString.alpha(rtn)), ExceptionBot)
                  } else {
                    if (Config.typingInterface != null)
                      if(Shell.params.opt_DeveloperMode || !abstraction)
                        Config.typingInterface.signal(Config.typingInterface.getSpan, URIErrorArg, "Global.decodeURI", string)
                    (ValueBot, Set[Exception](URIError))
                  }

                (r._1 + v_1, r._2 ++ es_1)
              })
            }
            case None =>
              (Value(StrTop), Set[Exception](URIError))
          }
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          val (h_1, ctx_1) =
            if (value </ ValueBot) {
              (Helper.ReturnStore(h, value), ctx)
            } else {
              (HeapBot, ContextBot)
            }

          ((h_1, ctx_1), (he + h_e, ctxe + ctx_e))
        }),

      "Global.decodeURIComponent" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val s = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))

          val lset_callee = getArgValue(h, ctx, args, "callee")._2
          val abstraction = (lset_callee.size > 1)
          
          val (value, es): (Value, Set[Exception]) = s.gamma match {
            case Some(vs) => {
              vs.foldLeft((ValueBot, ExceptionBot))((r, string) => {
                val rtn = URIHandling.decode(string, URIHandling.decodeURIComponentString)
                val (v_1, es_1) =
                  if (rtn != null) {
                    (Value(AbsString.alpha(rtn)), ExceptionBot)
                  } else {
                    if (Config.typingInterface != null)
                      if(Shell.params.opt_DeveloperMode || !abstraction)
                        Config.typingInterface.signal(Config.typingInterface.getSpan, URIErrorArg, "Global.decodeURIComponent", string)
                    (ValueBot, Set[Exception](URIError))
                  }

                (r._1 + v_1, r._2 ++ es_1)
              })
            }
            case None => (Value(StrTop), Set[Exception](URIError))
          }
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          val (h_1, ctx_1) =
            if (value </ ValueBot) {
              (Helper.ReturnStore(h, value), ctx)
            } else {
              (HeapBot, ContextBot)
            }

          ((h_1, ctx_1), (he + h_e, ctxe + ctx_e))
        }),

      "Global.encodeURI" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val s = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          
          val lset_callee = getArgValue(h, ctx, args, "callee")._2
          val abstraction = (lset_callee.size > 1)

          val (value, es): (Value, Set[Exception]) = s.gamma match {
            case Some(vs) => {
              vs.foldLeft((ValueBot, ExceptionBot))((r, string) => {
                val rtn = URIHandling.encode(string, URIHandling.encodeURIString)
                val (v_1, es_1) =
                  if (rtn != null) {
                    (Value(AbsString.alpha(rtn)), ExceptionBot)
                  } else {
                    if (Config.typingInterface != null)
                      if(Shell.params.opt_DeveloperMode || !abstraction)
                        Config.typingInterface.signal(Config.typingInterface.getSpan, URIErrorArg, "Global.encodeURI", string)
                    (ValueBot, Set[Exception](URIError))
                  }

                (r._1 + v_1, r._2 ++ es_1)
              })
            }
            case None => (Value(StrTop), Set[Exception](URIError))
          }
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          val (h_1, ctx_1) =
            if (value </ ValueBot) {
              (Helper.ReturnStore(h, value), ctx)
            } else {
              (HeapBot, ContextBot)
            }

          ((h_1, ctx_1), (he + h_e, ctxe + ctx_e))
        }),

      "Global.encodeURIComponent" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val s = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val lset_callee = getArgValue(h, ctx, args, "callee")._2
          val abstraction = (lset_callee.size > 1)

          val (value, es): (Value, Set[Exception]) = s.gamma match {
            case Some(vs) => {
              vs.foldLeft((ValueBot, ExceptionBot))((r, string) => {
                val rtn = URIHandling.encode(string, URIHandling.encodeURIComponentString)
                val (v_1, es_1) =
                  if (rtn != null) {
                    (Value(AbsString.alpha(rtn)), ExceptionBot)
                  } else {
                    if (Config.typingInterface != null)
                      if(Shell.params.opt_DeveloperMode || !abstraction)
                        Config.typingInterface.signal(Config.typingInterface.getSpan, URIErrorArg, "Global.encodeURIComponent", string)
                    (ValueBot, Set[Exception](URIError))
                  }

                (r._1 + v_1, r._2 ++ es_1)
              })
            }
            case None => (Value(StrTop), Set[Exception](URIError))
          }
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          val (h_1, ctx_1) =
            if (value </ ValueBot) {
              (Helper.ReturnStore(h, value), ctx)
            } else {
              (HeapBot, ContextBot)
            }

          ((h_1, ctx_1), (he + h_e, ctxe + ctx_e))
        }),

      ("Global.isNaN" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val n = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val b =
            if (NaN == n)
              BoolTrue
            else if (NaN </ n)
              BoolFalse
            else if (NaN <= n)
              BoolTop
            else
              BoolBot
          ((Helper.ReturnStore(h, Value(b)), ctx), (he, ctxe))
        })
      ),
      ("Global.isFinite" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val n = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val b =
            if (NaN == n || PosInf == n || NegInf == n)
              BoolFalse
            else if (NaN </ n && PosInf </ n && NegInf </ n)
              BoolTrue
            else if (NaN <= n || PosInf <= n || NegInf <= n)
              BoolTop
            else
              BoolBot
          ((Helper.ReturnStore(h, Value(b)), ctx), (he, ctxe))
        })
      ),
      ("Global.alert" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })
      )
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Global.parseInt" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          // 15.1.2.2 parseInt(string, radix)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* string */
          val v_2 = getArgValue_pre(h, ctx, args, "1", PureLocalLoc) /* radix */

          val inputString = PreHelper.toString(PreHelper.toPrimitive(v_1))
          // TODO: Simple implementation. Must be revised. Not the same as the original.
          val r = Operator.ToInt32(v_2)

          val value = Operator.parseInt(inputString, r)
          val rtn = Value(value)

          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctx))
        })
        ),
      "Global.encodeURIComponent" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val value = Value(StrTop)
          val es = Set[Exception](URIError)
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)

          ((PreHelper.ReturnStore(h, PureLocalLoc, value), ctx), (he + h_e, ctxe + ctx_e))
        }),
      "Global.encodeURI" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val value = Value(StrTop)
          val es = Set[Exception](URIError)
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)

          ((PreHelper.ReturnStore(h, PureLocalLoc, value), ctx), (he + h_e, ctxe + ctx_e))
        }),
      "Global.decodeURIComponent" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val value = Value(StrTop)
          val es = Set[Exception](URIError)
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)

          ((PreHelper.ReturnStore(h, PureLocalLoc, value), ctx), (he + h_e, ctxe + ctx_e))
        }),
      "Global.decodeURI" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val value = Value(StrTop)
          val es = Set[Exception](URIError)
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)

          ((PreHelper.ReturnStore(h, PureLocalLoc, value), ctx), (he + h_e, ctxe + ctx_e))
        }),
      ("Global.isNaN" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val n = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val b =
            if (NaN == n)
              BoolTrue
            else if (NaN </ n)
              BoolFalse
            else if (NaN <= n)
              BoolTop
            else
              BoolBot
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(b)), ctx), (he, ctxe))
        })
        ),
      ("Global.isFinite" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val n = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val b =
            if (NaN == n || PosInf == n || NegInf == n)
              BoolFalse
            else if (NaN </ n && PosInf </ n && NegInf </ n)
              BoolTrue
            else if (NaN <= n || PosInf <= n || NegInf <= n)
              BoolTop
            else
              BoolBot
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(b)), ctx), (he, ctxe))
        })
        ),
      ("Global.alert" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        })
        )
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("Global.parseInt" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })
      ),
      "Global.encodeURIComponent" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val es = Set[Exception](URIError)
          val LP1 = AH.RaiseException_def(es)
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        }),
      "Global.encodeURI" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val es = Set[Exception](URIError)
          val LP1 = AH.RaiseException_def(es)
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        }),
      "Global.decodeURIComponent" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val es = Set[Exception](URIError)
          val LP1 = AH.RaiseException_def(es)
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        }),
      "Global.decodeURI" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val es = Set[Exception](URIError)
          val LP1 = AH.RaiseException_def(es)
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        }),
      ("Global.isNaN"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })
      ),
      ("Global.isFinite" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })
      ),
      ("Global.alert"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })
      )
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("Global.parseInt" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") ++
            getArgValue_use(h, ctx, args, "1") + (SinglePureLocalLoc, "@return")
        })
        ),
      "Global.encodeURIComponent" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val es = Set[Exception](URIError)

          val LP_1 = getArgValue_use(h, ctx, args, "0")
          val LP_2 = AH.RaiseException_use(es)
          LP_1 ++ LP_2 +(SinglePureLocalLoc, "@return")
        }),
      "Global.encodeURI" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val es = Set[Exception](URIError)

          val LP_1 = getArgValue_use(h, ctx, args, "0")
          val LP_2 = AH.RaiseException_use(es)
          LP_1 ++ LP_2 +(SinglePureLocalLoc, "@return")
        }),
      "Global.decodeURIComponent" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val es = Set[Exception](URIError)

          val LP_1 = getArgValue_use(h, ctx, args, "0")
          val LP_2 = AH.RaiseException_use(es)
          LP_1 ++ LP_2 +(SinglePureLocalLoc, "@return")
        }),
      "Global.decodeURI" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val es = Set[Exception](URIError)

          val LP_1 = getArgValue_use(h, ctx, args, "0")
          val LP_2 = AH.RaiseException_use(es)
          LP_1 ++ LP_2 +(SinglePureLocalLoc, "@return")
        }),
      ("Global.isNaN"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })
        ),
      ("Global.isFinite" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })
        ),
      ("Global.alert"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })
        )
    )
  }
}
