/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.builtin

import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.bug_detector.JSONStringify
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import net.liftweb.json
import net.liftweb.json.JsonAST._

object BuiltinJSON extends ModelData {

  val ConstLoc = newSystemLoc("JSONConst", Recent)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("JSON")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("constructor",              AbsConstValue(PropValue(ObjectValue(Value(BuiltinObject.ConstLoc), F, F, F)))),
    ("parse",     AbsBuiltinFunc("JSON.parse",     2)), // the value of length property is from Chrome browser.
    ("stringify", AbsBuiltinFunc("JSON.stringify", 3))  // the value of length property is from Chrome browser.
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (ConstLoc, prop_const)
  )

//  class StringifyState(_indent: String, _gap: String, _propertyList: List[String]) {
//    var indent: String = _indent
//    var gap: String = _gap
//    var propertyList: List[String] = _propertyList
//    val stack: MStack[Loc] = MStack.empty
//  }
//
//  def quote(string: String): String = {
//    val product = new StringBuffer(string.length() + 2) // two extra chars for " on either side
//    product.append('"')
//    val length = string.length()
//
//    (0 to length-1).foreach(i => {
//      val c = string.charAt(i)
//      c match {
//        case '"' => product.append("\\\"")
//        case '\\' => product.append("\\\\")
//        case '\b' => product.append("\\b")
//        case '\f' => product.append("\\f")
//        case '\n' => product.append("\\n")
//        case '\r' => product.append("\\r")
//        case '\t' => product.append("\\t")
//        case _ => {
//          if (c < ' ') {
//            product.append("\\u");
//            val hex = String.format("%04x", c.toInt)
//            product.append(hex)
//          } else {
//            product.append(c)
//          }
//        }
//      }
//    })
//    product.append('"').toString
//  }
//
//  abstract class CValue
//  case class CNumber(v: Long) extends CValue {
//    override def toString: String = v.toString
//  }
//  case class CString(v: String) extends CValue {
//    override def toString: String = v
//  }
//  case object CUndefined extends CValue {
//    override def toString: String = "undefined"
//  }
//  case object CObject extends CValue {
//    override def toString: String = throw new InternalError("must not call toString of Obj")
//  }
//
//  def getConcreteValue(h: Heap, pv: (PropValue, Absent)): CValue = {
//    // TODO
//    if (AbsentTop <= pv._2) throw new InternalError("abstracted")
//    CUndefined
//  }
//
//  private def join(objs: List[String], delimiter: String): String = {
//    if (objs == null) ""
//    objs.mkString(delimiter)
//  }
//
//  private def ja(h: Heap, l_array: Loc, state: StringifyState): String = {
//    if (state.stack.search(l_array) != -1)
//      throw new InternalError("detect cycle")
//
//    val o = h(l_array)
//
//    state.stack.push(l_array)
//    val stepback: String = state.indent
//    state.indent = state.indent + state.gap
//    val partial: List[String] = new MLinkedList[String]()
//
//    val pv_len = o("length")
//    val len = getConcreteValue(pv) match {
//      case CNumber(v) => v
//      case _ =>  throw new InternalError("abstracted")
//    }
//    (0 to len - 1).foreach(index => {
//      val strP = str(h, index.toString, o, state)
//      strP match {
//        case CUndefined => partial.add("null")
//        case _ => partial.add(strP.toString)
//      }
//    })
//
//    var finalValue: String = ""
//    if (partial.isEmpty) {
//      finalValue = "[]"
//    } else {
//      if (state.gap.length() == 0) {
//        finalValue = '[' + join(partial, ",") + ']'
//      } else {
//        val sep = ",\n" + state.indent
//        val prop = join(partial, sep)
//        finalValue = "[\n" + state.indent + prop + '\n' + stepback + ']'
//      }
//    }
//
//    state.stack.pop()
//    state.indent = stepback
//    finalValue
//  }
//
//  private def str(h: Heap, key: String, o: Obj, state: StringifyState): CValue = {
//    val cv = getConcreteValue(h, o(key))
//
//  }

  def detectCycle(h: Heap, l: Loc): Boolean = {
    def detectCycle_(l: Loc, visited: LocSet): Unit = {
      val o = h(l)
      val s_set = o.getProps.filter(s => BoolTrue <= o(s)._1._3)
      s_set.foreach(s => {
        val lset = o(s)._1._1._2
        if (!visited.intersect(lset).isEmpty) {
          throw new InternalError("cycle detected")
        }
        lset.foreach(l => detectCycle_(l, visited + l))
      })
    }

    try {
      detectCycle_(l, LocSetBot)
      false
    } catch {
      case _: InternalError => true
    }
  }

  private def SAFEValtoJVal(h: Heap, v: Value): Option[JValue] = {
    // pvalue
    if(v.pvalue </ PValueBot && v.locset == LocSetBot){
      val undefPVal = PValue(v.pvalue.undefval)
      val nullPVal = PValue(v.pvalue.nullval)
      val boolPVal = PValue(v.pvalue.boolval)
      val numPVal = PValue(v.pvalue.numval)
      val strPVal = PValue(v.pvalue.strval)
      // undefined
      if(v.pvalue <= undefPVal)
        Some(JNothing)
      // null
      else if(v.pvalue <= nullPVal)
        Some(JNull)
      // boolean
      else if(v.pvalue <= boolPVal) {
        v.pvalue.boolval.getSingle match {
          case Some(b) => Some(JBool(b))
          case None => None
        }
      }
      // num
      else if (v.pvalue <= numPVal) {
        v.pvalue.numval.getSingle match {
          case Some(n) => 
            val n_int = n.toInt
            if(n_int == n)
              Some(JInt(n_int))
            else Some(JDouble(n))
          case None => None
        }
      }
      // string
      else if(v.pvalue <= strPVal) {
        v.pvalue.strval.getSingle match {
          case Some(s) => 
            Some(JString(s))
          case None => None
        }
      }
      else None
    }
    // object
    else if(v.pvalue <= PValueBot && v.locset != LocSetBot && v.locset.size ==1) {
      val loc = v.locset.head
      try {
        Helper.IsArray(h, loc).getSingle match {
         // non-array object
         case Some(b) if b==false =>
          val props = Helper.CollectOwnProps(h, v.locset)
          val (fieldList, isconcrete) = props.foldLeft((List[JField](), true))((li, prop) => {
            if(li._2) {
              val propv = Helper.Proto(h, loc, AbsString.alpha(prop))
              SAFEValtoJVal(h, propv) match {
                case Some(jv) => (li._1 ++ List(JField(prop, jv)), li._2)
                case None => (li._1, false)
              }
            }
            else li
          })
          if(isconcrete) 
            Some(JObject(fieldList))
          else 
            None
          // array object
         case Some(b) if b==true=>
           val len = Helper.toNumber(Helper.toPrimitive_better(h, Helper.Proto(h, loc, AbsString.alpha("length"))))
           len.getSingle match {
             case Some(n) => 
                val (vallist, isconcrete) = (0 until n.toInt).foldLeft((List[JValue](), true))((li, i) => {
                  if(li._2) {
                    val propv = Helper.Proto(h, loc, AbsString.alpha(i.toInt.toString))
                    SAFEValtoJVal(h, propv) match {
                      case Some(jv) => (li._1 ++ List(jv), li._2)
                      case None => (li._1, false)
                    }
                  }
                  else
                    li
                })
                if(isconcrete)
                  Some(JArray(vallist))
                else
                  None
             case None => None
           }
         case _ => None
       }
      }
      catch {
        case _ => None
      }
    }
    else
      None
  }

  private def JValtoSAFEVal(h: Heap, ctx: Context, cfg: CFG, key: (FunctionId, Address), index : Int, jval : JValue): (Heap, Context, Int, Value) = {
    jval match {
      case JArray(arr) => 
        val (newh, newctx, newindex, vallist) = arr.foldLeft((h, ctx, index, List[Value]()))((ret, elem) => {
          val _ret = JValtoSAFEVal(ret._1, ret._2, cfg, key, ret._3, elem)
          (_ret._1, _ret._2, _ret._3, ret._4 ++ List(_ret._4))
        })
        val addr = cfg.getAPIAddress(key, newindex)
        val new_l = addrToLoc(addr, Recent)
        val (h_1, ctx_1) = Helper.Oldify(newh, newctx, addr)
        val arrobj = Helper.NewArrayObject(AbsNumber.alpha(vallist.size))
        val arrobj2 = (0 until vallist.size).foldLeft(arrobj)((o, i) => o.update(i.toInt.toString, PropValue(ObjectValue(vallist(i), T, T, T))))
        (h_1.update(new_l, arrobj2), ctx_1, newindex+1, Value(new_l))

      case JObject(fields) =>  
        val (newh, newctx, newindex, proplist, vallist) = fields.foldLeft((h, ctx, index, List[String](), List[Value]()))((ret, elem) => {
          val _ret = JValtoSAFEVal(ret._1, ret._2, cfg, key, ret._3, elem.value)
          (_ret._1, _ret._2, _ret._3, ret._4 ++ List(elem.name), ret._5 ++ List(_ret._4))
        })
        val addr = cfg.getAPIAddress(key, newindex)
        val new_l = addrToLoc(addr, Recent)
        val (h_1, ctx_1) = Helper.Oldify(newh, newctx, addr)
        val obj = Helper.NewObject(ObjProtoLoc)
        val newobj = (proplist zip vallist).foldLeft(obj)((o, props) => o.update(props._1, PropValue(ObjectValue(props._2, T, T, T))))
        (h_1.update(new_l, newobj), ctx_1, newindex+1, Value(new_l))
      case JBool(v) => (h, ctx, index, Value(AbsBool.alpha(v)))
      case JDouble(num) => (h, ctx, index, Value(AbsNumber.alpha(num)))
      case JInt(num) => (h, ctx, index, Value(AbsNumber.alpha(num.toDouble)))
      case JString(s) => (h, ctx, index, Value(AbsString.alpha(s)))
      case _ => (h, ctx, index, JSONValueTop)
    }
  }

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "JSON.parse" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // Imprecise Semantics 
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val v_1 = getArgValue(h, ctx, args, "0")
          if(v_1 <= Value(v_1.pvalue.strval)) {
            // String argument
            val strarg =  v_1.pvalue.strval
            strarg.getSingle match {
              case Some(s) =>
                try {
                  val parsedval = json.parse(s)
                  val (newh, newctx, newindex, retval) = JValtoSAFEVal(h, ctx, cfg, addr_env, 0, parsedval)
                  ((Helper.ReturnStore(newh, retval), newctx), (he, ctxe))
                }
                catch {
                  case _ => 
                    ((Helper.ReturnStore(h, JSONValueTop), ctx), (he, ctxe))

                }
              case None => 
                    ((Helper.ReturnStore(h, JSONValueTop), ctx), (he, ctxe))
            } 
          }
          else ((Helper.ReturnStore(h, JSONValueTop), ctx), (he, ctxe))
        }),
      "JSON.stringify" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 = getArgValue(h, ctx, args, "1")
          
          val lset_callee = getArgValue(h, ctx, args, "callee")._2
          val abstraction = lset_callee.size > 1

          val lset_fn = v_2._2.filter(l => BoolTrue <= Helper.IsCallable(h, l))
          if (!lset_fn.isEmpty) System.err.println("* Warning: Semantics of the API function 'JSON.stringify(value, replacer)' is not defined.")

          val lset = v_1._2
          val cycle = lset.toSet.find(l => detectCycle(h, l))

          val value = Value(StrTop) + Value(UndefTop)

          val es = cycle match {
                     case Some(l) =>
                       if (Config.typingInterface != null)
                         if(Shell.params.opt_DeveloperMode || !abstraction)
                           Config.typingInterface.signal(Config.typingInterface.getSpan, JSONStringify, null, null)
                       Set[Exception](TypeError)
                     case _ => ExceptionBot
                   }

          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)

          val newval = cycle match {
            case Some(l) => value
            case None if lset.size != 1=> value
            case None => 
              try {
                SAFEValtoJVal(h, Value(lset)) match {
                  case Some(jv) => 
                    val str = json.compact(json.render(jv))
                    // convert escaping characters 
                    val newstr = str.replace("\\u2028", "\u2028")
                    val newstr2 = newstr.replace("\\u2029", "\u2029")
                    Value(AbsString.alpha(newstr2))
                  case None => value
                }
              }
              catch{
                case _ => value
              }
          }

          ((Helper.ReturnStore(h, newval), ctx), (he + h_e, ctxe + ctx_e))
        })
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("JSON.parse" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val value = JSONValueTop
          ((PreHelper.ReturnStore(h, cfg.getPureLocal(cp), value), ctx), (he, ctxe))
        })),
      "JSON.stringify" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val value = Value(StrTop) + Value(UndefTop)
          val es = Set[Exception](TypeError)

          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)

          ((PreHelper.ReturnStore(h, cfg.getPureLocal(cp), value), ctx), (he + h_e, ctxe + ctx_e))
        })
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("JSON.parse" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      "JSON.stringify" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val es = Set[Exception](TypeError)

          val LP_1 = AH.RaiseException_def(es)
          LP_1 ++ LPSet((SinglePureLocalLoc, "@return"))
        })
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("JSON.parse" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      "JSON.stringify" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 = getArgValue(h, ctx, args, "1")
          val es = Set[Exception](TypeError)

          val LP_1 = getArgValue_use(h, ctx, args, "0")
          val LP_2 = getArgValue_use(h, ctx, args, "0")
          val LP_3 = v_2._2.foldLeft(LPBot)((S, l) => S ++ AH.IsCallable_use(h, l))
          val LP_4 = v_1._2.foldLeft(LPBot)((S, l) => S ++ AH.DetectCycle_use(h, l))
          val LP_5 = AH.RaiseException_use(es)
          LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LPSet((SinglePureLocalLoc, "@return"))
        })
    )
  }
}
