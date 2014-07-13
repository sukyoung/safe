/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.asserts

import kr.ac.kaist.jsaf.nodes_util.{IRFactory => IF}
import kr.ac.kaist.jsaf.nodes_util.EJSOp
import kr.ac.kaist.jsaf.nodes.IROp
import kr.ac.kaist.jsaf.scala_src.nodes.SIROp
import kr.ac.kaist.jsaf.analysis.typing.Helper
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.cfg._
import scala.collection.immutable.HashSet


abstract class PrunExpr {
  def get: CFGExpr
  override def toString : String
}

case class Id(id: CFGVarRef) extends PrunExpr {
  def get: CFGVarRef = id
  override def toString: String = id.toString
}
case class Prop(prop: CFGLoad) extends PrunExpr {
  def get: CFGLoad = prop
  override def toString: String = prop.toString
}

case class RelExpr(first: CFGExpr, op: IROp, second: CFGExpr) {
  override def toString = first.toString+" "+op.getText+" "+second.toString
}

object ASSERTHelper {
  // Check the operator is relational operator or not
  def isConstantPruing(op: IROp, expr: CFGExpr): Boolean = {
    if(op.getKind == EJSOp.BIN_COMP_EQ_NEQUAL || op.getKind == EJSOp.BIN_COMP_EQ_SNEQUAL) {
      expr match {
        case CFGNull() => true
        case CFGVarRef(_, id) => id match {
          case CFGUserId(_, text, _, _, _) if text.equals("undefined") => true
          case _ => false
        }
        case _ => false
      }
    }
    else false
  }
  def isAssertOperator(op: IROp): Boolean = {
    EJSOp.isEquality(op.getKind)
  }
  def isRelationalOperator(op: IROp): Boolean = {
    (op.getKind - EJSOp.BIN_COMP_EQ < 5) && (0 < op.getKind - EJSOp.BIN_COMP_EQ)
  }
  def isObjectOperator(op: IROp): Boolean = {
    (op.getKind == EJSOp.BIN_COMP_REL_IN) || (op.getKind == EJSOp.BIN_COMP_REL_INSTANCEOF) ||
    (op.getKind == EJSOp.BIN_COMP_REL_NOTIN) || (op.getKind == EJSOp.BIN_COMP_REL_NOTINSTANCEOF)
  }
  // Transposition rules for each relational IR Operator
  def transIROp(op: IROp): IROp = {
    op.getKind match {
      case EJSOp.BIN_COMP_REL_LESS => IF.makeOp(">=")	// < --> >=
      case EJSOp.BIN_COMP_REL_GREATER => IF.makeOp("<=")	// > --> <=
      case EJSOp.BIN_COMP_REL_LESSEQUAL => IF.makeOp(">")	// <= --> >
      case EJSOp.BIN_COMP_REL_GREATEREQUAL => IF.makeOp("<")	// >= --> <
      case EJSOp.BIN_COMP_EQ_EQUAL => IF.makeOp("!=")	// == --> !=
      case EJSOp.BIN_COMP_EQ_NEQUAL => IF.makeOp("==")	// != --> ==
      case EJSOp.BIN_COMP_EQ_SEQUAL => IF.makeOp("!==")	// === --> !==
      case EJSOp.BIN_COMP_EQ_SNEQUAL => IF.makeOp("===")	// !== --> ===
      case EJSOp.BIN_COMP_REL_IN => IF.makeOp("notIn")	// in --> notIn
      case EJSOp.BIN_COMP_REL_INSTANCEOF => IF.makeOp("notInstanceof")	// instanceof --> notInstanceof
      case EJSOp.BIN_COMP_REL_NOTIN => IF.makeOp("in")	// notIn --> in
      case EJSOp.BIN_COMP_REL_NOTINSTANCEOF => IF.makeOp("instanceof")	// notInstanceof --> instanceof
      case _ => op
    }
  }
  // Transposition rules for each reflective IR Operator
  def reflectiveIROp(op: IROp): IROp = {
    op.getKind match {
      case EJSOp.BIN_COMP_REL_LESS => IF.makeOp(">")	// < --> >
      case EJSOp.BIN_COMP_REL_GREATER => IF.makeOp("<")	// > --> <
      case EJSOp.BIN_COMP_REL_LESSEQUAL => IF.makeOp(">=")	// <= --> >=
      case EJSOp.BIN_COMP_REL_GREATEREQUAL => IF.makeOp("<=")	// >= --> <=
      case _ => op
    }
  }

  def K(op:IROp, v: Value, top_loc: LocSet): (Value, Absent) = {
    val (pv, loc) = (v._1, v._2)
    val absUndef = if(UndefTop <= pv._1) AbsentTop else AbsentBot
    val absNull = if(NullTop <= pv._2) AbsentTop else AbsentBot
    op.getKind match {
      case EJSOp.BIN_COMP_EQ_SEQUAL => (v, absUndef)
      case EJSOp.BIN_COMP_EQ_SNEQUAL => (Value(PValueTop, loc ++ top_loc), AbsentTop)
      case EJSOp.BIN_COMP_EQ_EQUAL =>
        val pv1 = if(NullTop <= pv._2) UndefTop else UndefBot + pv._1
        val pv2 = if(UndefTop <= pv._1) NullTop else NullBot + pv._2
        val pv3_1 = if(!loc.isEmpty) BoolTop else BoolBot
        val pv3_2 = if(loc.isEmpty && (AbsNumber.alpha(1) <= pv._4 || AbsNumber.alpha(1) <= str2Num(pv._5))) BoolTrue else BoolBot
        val pv3_3 = if(loc.isEmpty && AbsNumber.alpha(0) <= pv._4 || AbsNumber.alpha(0) <= str2Num(pv._5)) BoolFalse else BoolBot
        val pv3 = pv3_1 + pv3_2 + pv3_3 + pv._3
        val pv4_1 = if(BoolTrue <= pv._3) AbsNumber.alpha(1) else NumBot
        val pv4_2 = if(BoolFalse <= pv._3) AbsNumber.alpha(0) else NumBot
        val pv4_3 = str2Num(pv._5)
        val pv4_4 = if(pv._4 <= NaN) NumBot else pv._4
        val pv4 = if(loc.isEmpty) pv4_1 + pv4_2 + pv4_3 + pv4_4 else NumTop
        (Value(pv1) + Value(pv2) + Value(pv3) + Value(pv4) + Value(StrTop) + Value(loc ++ top_loc), absUndef + absNull)
      case EJSOp.BIN_COMP_EQ_NEQUAL => (Value(PValueTop, loc ++ top_loc), AbsentTop)
      case _ => (Value(PValueTop, loc ++ top_loc), AbsentTop)
    }
  }

  def str2Num(s: AbsString): AbsNumber = {
    Helper.toNumber(PValue(s))
  }

  def PruneInstanceof(l_obj: Loc, l_fun: Loc, b: AbsBool, h: Heap): Heap = {
    val L_prototype = h(l_fun)("prototype")._1._1._1._2
    val L_proto = h(l_obj)("@proto")._1._1._1._2
    // inheritProto using filter
    /*
    val L1 = L_prototype.filter(l2 => PValue(b) <= Helper.inherit(h, L_proto.head, l2)._1)
    val L2 = L_proto.filter(l1 => PValue(b) <= Helper.inherit(h, l1, L_prototype.head)._1)
    val H1 = if(L_prototype.size == 1) h.update(l_fun, h(l_fun).update("@proto", PropValue(ObjectValue(Value(PValueBot, L2), BoolFalse, BoolFalse, BoolFalse)))) else h
    val H2 = if(L_proto.size == 1) h.update(l_fun, h(l_fun).update("prototype", PropValue(ObjectValue(Value(PValueBot, L1), BoolFalse, BoolFalse, BoolFalse)))) else h
    */
    val L1 = L_prototype.filter(l2 => PValue(b) <=
                         L_proto.foldLeft(PValueBot)((_b, l) => _b + Helper.inherit(h, l, l2)._1))
    val L2 = L_proto.filter(l1 => PValue(b) <=
                         L_prototype.foldLeft(PValueBot)((_b, l) => _b + Helper.inherit(h, l1, l)._1))
//    val L2 = L_proto.filter(l1 => PValue(b) <= Helper.inherit(h, l1, L_prototype.head)._1)
    val H1 = h.update(l_obj, h(l_obj).update("@proto", PropValue(ObjectValue(Value(PValueBot, L2), BoolFalse, BoolFalse, BoolFalse))))
    val H2 = h.update(l_fun, h(l_fun).update("prototype", PropValue(ObjectValue(Value(PValueBot, L1), BoolFalse, BoolFalse, BoolFalse))))
    H1 <> H2
  }

  def DeleteAll(h: Heap, l: Loc, s: AbsString): Heap = {
    val h2 = Helper.Delete(h, l, s)._1
    val v = h(l)("@proto")._1._1._1
    if (v._2.size == 1 && v._1._2 <= NullBot) {
      DeleteAll(h2, v._2.head, s)
    }
    else h2
  }
}
