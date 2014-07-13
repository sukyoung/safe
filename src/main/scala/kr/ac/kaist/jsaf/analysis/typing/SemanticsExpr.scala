/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.immutable.HashSet
import scala.collection.mutable.{HashMap => MHashMap}
import scala.collection.mutable.{Map => MMap}
import scala.runtime.RichDouble

import kr.ac.kaist.jsaf.analysis.asserts._
import kr.ac.kaist.jsaf.analysis.asserts.{ASSERTHelper => AH}
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.Operator._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.nodes_util.EJSOp
import kr.ac.kaist.jsaf.nodes_util.IRFactory
import kr.ac.kaist.jsaf.nodes.IROp
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.HTMLTopElement
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHelper

object SemanticsExpr {
  def V(e: CFGExpr, h: Heap, ctx: Context):(Value, Set[Exception]) = {
    e match {
      case CFGVarRef(info, id) =>
        val (v, es) = Helper.Lookup(h, id)
        (v,es)
      case CFGBin(info, first, op, second) =>
        val (v_1,es_1) = V(first,h,ctx)
        val (v_2,es_2)  = V(second,h,ctx)
        if (v_1 == ValueBot) {
          (ValueBot, es_1)
        } else if (v_2 == ValueBot) {
          (ValueBot, es_1 ++ es_2)
        } else {
          op.getText match {
            case "|" => (bopBitOr(v_1,v_2), es_1 ++ es_2)
            case "&" => (bopBitAnd(v_1,v_2), es_1 ++ es_2)
            case "^" => (bopBitXor(v_1,v_2), es_1 ++ es_2)
            case "<<" => (bopLShift(v_1,v_2), es_1 ++ es_2)
            case ">>" => (bopRShift(v_1,v_2), es_1 ++ es_2)
            case ">>>" => (bopURShift(v_1,v_2), es_1 ++ es_2)
            case "+" => (bopPlus(v_1,v_2), es_1 ++ es_2)
            case "-" => (bopMinus(v_1,v_2), es_1 ++ es_2)
            case "*" => (bopMul(v_1,v_2), es_1 ++ es_2)
            case "/" => (bopDiv(v_1,v_2), es_1 ++ es_2)
            case "%" => (bopMod(v_1,v_2), es_1 ++ es_2)
            case "==" => (bopEq(v_1,v_2), es_1 ++ es_2)
            case "!=" => (bopNeq(v_1,v_2), es_1 ++ es_2)
            case "===" => (bopSEq(v_1,v_2), es_1 ++ es_2)
            case "!==" => (bopSNeq(v_1,v_2), es_1 ++ es_2)
            case "<" => (bopLess(v_1,v_2), es_1 ++ es_2)
            case ">" => (bopGreater(v_1,v_2), es_1 ++ es_2)
            case "<=" => (bopLessEq(v_1,v_2), es_1 ++ es_2)
            case ">=" => (bopGreaterEq(v_1,v_2), es_1 ++ es_2)
            case "instanceof" =>
              val lset_1 = v_1._2
              val lset_2 = v_2._2
              val lset_3 = lset_2.filter((l) => BoolTrue <= Helper.HasInstance(h, l))
              val v_proto = lset_3.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h,l,AbsString.alpha("prototype")))
              val lset_4 = v_proto._2
              val lset_5 = lset_2.filter((l) => BoolFalse <= Helper.HasInstance(h, l))
              val b_1 = lset_1.foldLeft[Value](ValueBot)((v_1, l_1) =>
                lset_4.foldLeft[Value](v_1)((v_2, l_2) => v_2 + Helper.inherit(h, l_1, l_2)))
              val b_2 =
                if ((v_1._1 </ PValueBot) && !(lset_4.isEmpty))
                  Value(BoolFalse)
                else
                  Value(BoolBot)
              val es_3 =
                if ((v_2._1 </ PValueBot) || !(lset_5.isEmpty) || (v_proto._1 </ PValueBot))
                  Set[Exception](TypeError)
                else
                  Set[Exception]()
              val b = b_1 + b_2
              val es = es_1 ++ es_2 ++ es_3
              (b, es)
            case "in" =>
              val lset = v_2._2
              val s = Helper.toString(Helper.toPrimitive_better(h, v_1))
              val b = lset.foldLeft[AbsBool](BoolBot)((v, l) => v + Helper.HasProperty(h, l, s))
              val es_3 =
                if (v_2._1 </ PValueBot)
                  Set[Exception](TypeError)
                else
                  Set[Exception]()
              val es = es_1 ++ es_2 ++ es_3
              (Value(b), es)
            }
          }
      case CFGUn(info, op, expr) =>
        val (v,es) = V(expr,h,ctx)
        op.getText match {
          case "void" => (uVoid(v),es)
          case "typeof" => expr match {
            case _: CFGVarRef =>
              val s_1 = Helper.TypeTag(h, v)
              val s_2 = 
                if (es.contains(ReferenceError)) AbsString.alpha("undefined")
                else StrBot
              (Value(s_1 + s_2), ExceptionBot)
            case _ =>
              (Value(Helper.TypeTag(h, v)), es)
          }
          case "+" => (uopPlus(v),es)
          case "-" => (uopMinus(v),es)
          case "~" => (uopBitNeg(v),es)
          case "!" => (uopNeg(v),es)
        }
      case CFGLoad(info, obj, index) => {
        val (v, es) = V(index, h, ctx)
        if (v <= ValueBot) (ValueBot, es)
        else {
          // lset must not be empty because obj is coming through <>toObject.
          val lset = V(obj,h,ctx)._1._2
          
          val sset = Helper.toStringSet(Helper.toPrimitive_better(h, v))
          val v_1 = lset.foldLeft(ValueBot)((vv_1, l) => {
            sset.foldLeft(vv_1)((vv_2, s) => {
              /* DOM property load such as 'innerHTML' */
              if(Config.domPropMode && HTMLTopElement.getInsLoc(h).contains(l))
                vv_2 + DOMHelper.loadDOMProp(h, l, s)
              else 
                vv_2 + Helper.Proto(h, l, s)
            })
          })
          
          // XXX: if the object is Array, the the length must be Number type.
          // val is_array = lset.forall((l) => h(l)(AbsString.alpha("@class"))._1._2 == Value(AbsString.alpha("Array")))
          // val v =
          //   if (s == AbsString.alpha("length") && is_array)
          //     lset.foldLeft(ValueBot)((v_1, l) => v_1 + Value((Helper.Proto(h,l,s))._1._4))
          //   else
          //     lset.foldLeft(ValueBot)((v_1, l) => v_1 + Helper.Proto(h,l,s))
  
          (v_1, es)
        }
      }
      case CFGNumber(text, num) => (Value(PValue(AbsNumber.alpha(num))),ExceptionBot)
      case CFGString(str) => (Value(PValue(AbsString.alpha(str))),ExceptionBot)
      case CFGBool(bool) => (Value(PValue(AbsBool.alpha(bool))),ExceptionBot)
      case CFGNull() => (Value(PValue(AbsNull.alpha)),ExceptionBot)
      case CFGThis(info) => (Value(PValueBot, h(SinglePureLocalLoc)("@this")._1._2._2), ExceptionBot)
    }
  }
}
