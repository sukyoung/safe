/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMCore

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import org.w3c.dom.Node
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing._
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object DOMNode extends DOM {
  private val name = "Node"
  // NodeType
  val ELEMENT_NODE = 1
  val ATTRIBUTE_NODE = 2
  val TEXT_NODE = 3
  val CDATA_SECTION_NODE = 4
  val ENTITY_REFERENCE_NODE = 5
  val ENTITY_NODE = 6
  val PROCESSING_INSTRUCTION_NODE = 7
  val COMMENT_NODE = 8
  val DOCUMENT_NODE = 9
  val DOCUMENT_TYPE_NODE = 10
  val DOCUMENT_FRAGMENT_NODE = 11
  val NOTATION_NODE = 12

  /* predefined locatoins */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("ELEMENT_NODE",                AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, T, T)))),
    ("ATTRIBUTE_NODE",              AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(2), F, T, T)))),
    ("TEXT_NODE",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(3), F, T, T)))),
    ("CDATA_SECTION_NODE",          AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(4), F, T, T)))),
    ("ENTITY_REFERENCE_NODE",       AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(5), F, T, T)))),
    ("ENTITY_NODE",                 AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(6), F, T, T)))),
    ("PROCESSING_INSTRUCTION_NODE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(7), F, T, T)))),
    ("COMMENT_NODE",                AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(8), F, T, T)))),
    ("DOCUMENT_NODE",               AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(9), F, T, T)))),
    ("DOCUMENT_TYPE_NODE",          AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(10), F, T, T)))),
    ("DOCUMENT_FRAGMENT_NODE",      AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(11), F, T, T)))),
    ("NOTATION_NODE",               AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(12), F, T, T)))),
    ("DOCUMENT_POSITION_DISCONNECTED",            AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x01), F, T, T)))),
    ("DOCUMENT_POSITION_PRECEDING",               AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x02), F, T, T)))),
    ("DOCUMENT_POSITION_FOLLOWING",               AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x04), F, T, T)))),
    ("DOCUMENT_POSITION_CONTAINS",                AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x08), F, T, T)))),
    ("DOCUMENT_POSITION_CONTAINED_BY",            AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x10), F, T, T)))),
    ("DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x20), F, T, T))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("insertBefore",            AbsBuiltinFunc("DOMNode.insertBefore", 2)),
    ("replaceChild",            AbsBuiltinFunc("DOMNode.replaceChild", 2)),
    ("removeChild",             AbsBuiltinFunc("DOMNode.removeChild", 1)),
    ("appendChild",             AbsBuiltinFunc("DOMNode.appendChild", 1)),
    ("hasChildNodes",           AbsBuiltinFunc("DOMNode.hasChildNodes", 0)),
    ("cloneNode",               AbsBuiltinFunc("DOMNode.cloneNode", 1)),
    ("normalize",               AbsBuiltinFunc("DOMNode.normalize", 0)),
    ("isSupported",             AbsBuiltinFunc("DOMNode.isSupported", 2)),
    ("hasAttributes",           AbsBuiltinFunc("DOMNode.hasAttributes", 0)),
    ("compareDocumentPosition", AbsBuiltinFunc("DOMNode.compareDocumentPosition", 1)),
    ("isSameNode",              AbsBuiltinFunc("DOMNode.isSameNode", 1)),
    ("lookupPrefix",            AbsBuiltinFunc("DOMNode.lookupPrefix", 1)),
    ("isDefaultNamespace",      AbsBuiltinFunc("DOMNode.isDefaultNamespace", 1)),
    ("lookupNamespaceURI",      AbsBuiltinFunc("DOMNode.lookupNamespaceURI", 1)),
    ("isEqualNode",             AbsBuiltinFunc("DOMNode.isEqualNode", 1)),
    ("getFeature",              AbsBuiltinFunc("DOMNode.getFeature", 2)),
    ("setUserData",             AbsBuiltinFunc("DOMNode.setUserData", 3)),
    ("getUserData",             AbsBuiltinFunc("DOMNode.getUserData", 1)),
    // WHATWG DOM
    ("contains",                AbsBuiltinFunc("DOMNode.contains", 1))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      ("DOMNode.insertBefore" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_new = getArgValue(h, ctx, args, "0")._2
          val ref = getArgValue(h, ctx, args, "1")
          val lset_ref = ref._2
          // If refChild is null, insert newChild at the end of the list of children.
          val nullh = if(NullTop <= ref._1._2) {
               DOMTree.appendChild(h, lset_this, lset_new)   
            } else h
          if (!lset_new.isEmpty && !lset_ref.isEmpty) {
            val h_1 = DOMTree.insertBefore(nullh, lset_this, lset_new, lset_ref)
            ((Helper.ReturnStore(h_1, Value(lset_new)), ctx), (he, ctxe))
          }
          else if(NullTop <= ref._1._2)
            ((nullh, ctx), (he, ctxe))
          else 
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.replaceChild" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_new = getArgValue(h, ctx, args, "0")._2
          val lset_old = getArgValue(h, ctx, args, "1")._2
          if (!lset_new.isEmpty && !lset_old.isEmpty) {
            /* location for clone node */
            val h_1 = lset_this.foldLeft(h)((hh, l_node) => {
              val lset_ns = Helper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              lset_ns.foldLeft(hh)((hhh, l_ns) => {
                val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
                n_len.getSingle match {
                  case Some(n) if AbsNumber.isNum(n_len) =>
                    val n_index = (0 until n.toInt).indexWhere((i) => {
                      BoolTrue <= Operator.bopSEq(Helper.Proto(hhh, l_ns, AbsString.alpha(i.toString)), Value(lset_old))._1._3
                    })
                    if (n_index < 0)
                      hhh
                    else {
                      val hhh_1 = Helper.Delete(hhh, l_ns, AbsString.alpha(n_index.toString))._1
                      Helper.PropStore(hhh_1, l_ns, AbsString.alpha(n_index.toString), Value(lset_new))
                    }
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    val b_eq = Operator.bopSEq(Helper.Proto(hhh, l_ns, NumStr), Value(lset_old))._1._3
                    val hhh_1 =
                      if (BoolTrue <= b_eq) {
                        val _hhh = Helper.Delete(hhh, l_ns, NumStr)._1
                        Helper.PropStore(_hhh, l_ns, NumStr, Value(lset_new))
                      }
                      else HeapBot
                    val hhh_2 =
                      if (BoolFalse <= b_eq) hhh
                      else HeapBot
                    hhh_1 + hhh_2
                  case _ => hhh /* exception ?? */
                }
              })
            })
            /* `parentNode', 'previousSibling', 'nextSibling' update of the reference child */
            val (h_2, preSib, nextSib) = lset_old.foldLeft((h_1, ValueBot, ValueBot))((d, l) => {
              val preS = Helper.Proto(d._1, l, AbsString.alpha("previousSibling"))
              val nextS = Helper.Proto(d._1, l, AbsString.alpha("nextSibling"))
              val h_2_1 = Helper.PropStore(d._1, l, AbsString.alpha("parentNode"), Value(NullTop))
              val h_2_2 = Helper.PropStore(h_2_1, l, AbsString.alpha("previousSibling"), Value(NullTop))
              val h_2_3 = Helper.PropStore(h_2_2, l, AbsString.alpha("nextSibling"), Value(NullTop))
              (h_2_3, preS + d._2, nextS + d._3)
            })

            /* 'prarentNode', 'previousSibling', 'nextSibling' update of the new child */
            val h_3 = lset_new.foldLeft(h_2)((_h, l) => {
              val h_3_1 = Helper.PropStore(_h, l, AbsString.alpha("parentNode"), Value(lset_this))
              val h_3_2 = Helper.PropStore(h_3_1, l, AbsString.alpha("previousSibling"), preSib)
              Helper.PropStore(h_3_2, l, AbsString.alpha("nextSibling"), nextSib)
            })

            /* 'nextSibling' update of the previous sibling of the reference child */
            val h_4 = preSib._2.foldLeft(h_3)((_h, l) =>
              Helper.PropStore(_h, l, AbsString.alpha("nextSibling"), Value(lset_new))
            )
            
            /* 'previousSibling' update of the next sibling of the reference child */
            val h_5 = nextSib._2.foldLeft(h_4)((_h, l) =>
              Helper.PropStore(_h, l, AbsString.alpha("previousSibling"), Value(lset_new))
            )
            
            ((Helper.ReturnStore(h_5, Value(lset_old)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.removeChild" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_child = getArgValue(h, ctx, args, "0")._2
          if (!lset_this.isEmpty && !lset_child.isEmpty) {
            val h_1 = DOMTree.removeChild(h, lset_this, lset_child)
            ((Helper.ReturnStore(h_1, Value(lset_child)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.appendChild" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_child = getArgValue(h, ctx, args, "0")._2
          val h_1 = DOMTree.appendChild(h, lset_this, lset_child)
          if (!lset_child.isEmpty && !lset_child.isEmpty)
            ((Helper.ReturnStore(h_1, Value(lset_child)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.hasChildNodes" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val b_return = lset_this.foldLeft[AbsBool](BoolBot)((b, l) => {
            val lset_child = Helper.Proto(h, l, AbsString.alpha("childNodes"))._2
            lset_child.foldLeft(b)((bb, ll) => {
              val absnum = Helper.Proto(h, ll, AbsString.alpha("length"))._1._4
              bb + (absnum.getAbsCase match {
                case AbsBot => BoolBot
                case _ if AbsNumber.isUIntAll(absnum) => BoolTop
                case _ => absnum.getSingle match {
                  case Some(n) if AbsNumber.isNum(absnum) => if (n != 0) BoolTrue else BoolFalse
                  case _ => BoolFalse
              }})
            })
          })
          if (b_return </ BoolBot)
            ((Helper.ReturnStore(h, Value(b_return)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.cloneNode" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val b_deep = Helper.toBoolean(getArgValue(h, ctx, args, "0"))
          if (b_deep </ BoolBot) {
            /* unsound, 'deep' arugment is ingnored */
            /* location for clone node */
            val l_r = addrToLoc(addr1, Recent)
            val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
            /* this node only */
            val o_node = lset_this.foldLeft(ObjBot)((o, l) => o + h_1(l))
            val h_2 = h_1.update(l_r, o_node)
            /* The duplicate node has no parent; (parentNode is null.). */
            val h_3 = Helper.PropStore(h_2, l_r, AbsString.alpha("parentNode"), Value(NullTop))
            ((Helper.ReturnStore(h_3, Value(l_r)), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.normalize" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* unsound, do nothing */
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("DOMNode.isSupported" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_feature = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val s_version = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if (s_feature </ StrBot || s_version </ StrBot)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.hasAttributes" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* imprecise semantic */
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
        })),
      ("DOMNode.compareDocumentPosition" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val v_other = getArgValue(h, ctx, args, "0")
          if (v_other </ ValueBot) {
            /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(UInt)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.isSameNode" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val v_other = getArgValue(h, ctx, args, "0")
          if (v_other </ ValueBot) {
            val v_return = Operator.bopSEq(Value(lset_this), Value(v_other._2))
            ((Helper.ReturnStore(h, v_return), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.lookupPrefix" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_uri = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_uri </ StrBot)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(StrTop) + Value(NullTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.isDefaultNamespace" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_uri = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_uri </ StrBot)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.lookupNamespaceURI" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_prefix = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_prefix </ StrBot)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(StrTop) + Value(NullTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.isEqualNode" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val v_arg = getArgValue(h, ctx, args, "0")
          if (v_arg </ ValueBot)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.getFeature" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_feature = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val s_version = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if (s_feature </ StrBot || s_version </ StrBot)
          /* unsound semantic */
            ((Helper.ReturnStore(h, Value(NullTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.setUserData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_key = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val v_data = getArgValue(h, ctx, args, "1")
          val v_handler = getArgValue(h, ctx, args, "2")
          if (s_key </ StrBot || v_data </ ValueBot || v_handler </ ValueBot)
          /* unsound semantic */
            ((Helper.ReturnStore(h, Value(NullTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.getUserData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_key = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_key </ StrBot)
          /* unsound semantic */
            ((Helper.ReturnStore(h, Value(NullTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNode.contains" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val other = getArgValue(h, ctx, args, "0") 
          val nullargcheck = if(other._1._2 </ NullBot) Value(BoolFalse) else ValueBot

          val lset_other = getArgValue(h, ctx, args, "0")._2
          if(!lset_other.isEmpty){
            val returnval = lset_this.foldLeft(Value(BoolBot))((_val, l_this) => {
              lset_other.foldLeft(_val)((__val, l_other) => {
                if(DOMHelper.contains(h, LocSetBot, l_this, l_other) == true)
                  __val + Value(BoolTrue)
                else
                  __val + Value(BoolFalse)
              })
            })
            ((Helper.ReturnStore(h, returnval + nullargcheck), ctx), (he, ctxe))
          }
          else if(nullargcheck </ ValueBot)
            ((Helper.ReturnStore(h, nullargcheck), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("DOMNode.insertBefore" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_new = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)._2
          val lset_ref = getArgValue_pre(h, ctx, args, "1", PureLocalLoc)._2
          if (!lset_new.isEmpty && !lset_ref.isEmpty) {
            /* location for clone node */
            val h_1 = lset_this.foldLeft(h)((hh, l_node) => {
              val lset_ns = PreHelper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              lset_ns.foldLeft(hh)((hhh, l_ns) => {
                val n_len = Operator.ToUInt32(PreHelper.Proto(h, l_ns, AbsString.alpha("length")))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n_length) =>
                    val n_index = (0 until n_length.toInt).indexWhere((i) => {
                      BoolTrue <= Operator.bopSEq(PreHelper.Proto(hhh, l_ns, AbsString.alpha(i.toString)), Value(lset_ref))._1._3
                    })
                    if (n_index < 0)
                      hhh
                    else {
                      val _hhh = (n_index+1 until n_length.toInt).foldLeft(hhh)((_h, i) => {
                        val i_rev = n_length - i + 1
                        val v_move = PreHelper.Proto(_h, l_ns,  AbsString.alpha(i_rev.toString))
                        val _h1 = PreHelper.Delete(_h, l_ns, AbsString.alpha(i_rev.toString))._1
                        PreHelper.PropStore(_h1, l_ns, AbsString.alpha((i_rev+1).toString), v_move)
                      })
                      val _hhh_1 = PreHelper.PropStore(_hhh, l_ns, AbsString.alpha(n_index.toString), Value(lset_new))
                      // increase the length of childNodes by 1
                      PreHelper.PropStore(_hhh_1, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n_length + 1)))
                    }
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    val b_eq = Operator.bopSEq(PreHelper.Proto(hhh, l_ns, NumStr), Value(lset_ref))._1._3
                    val hhh_1 =
                      if (BoolTrue <= b_eq) PreHelper.PropStore(hhh, l_ns, NumStr, Value(lset_new))
                      else hhh
                    val hhh_2 =
                      if (BoolFalse <= b_eq) hhh
                      else hhh
                    hhh_1 + hhh_2
                  case _ => hhh /* exception ?? */
                }
              })
            })
            ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(lset_new)), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.replaceChild" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_new = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)._2
          val lset_old = getArgValue_pre(h, ctx, args, "1", PureLocalLoc)._2
          if (!lset_new.isEmpty && !lset_old.isEmpty) {
            /* location for clone node */
            val h_1 = lset_this.foldLeft(h)((hh, l_node) => {
              val lset_ns = PreHelper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              lset_ns.foldLeft(hh)((hhh, l_ns) => {
                val n_len = Operator.ToUInt32(PreHelper.Proto(h, l_ns, AbsString.alpha("length")))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n) =>
                    val n_index = (0 until n.toInt).indexWhere((i) => {
                      BoolTrue <= Operator.bopSEq(PreHelper.Proto(hhh, l_ns, AbsString.alpha(i.toString)), Value(lset_old))._1._3
                    })
                    if (n_index < 0)
                      hhh
                    else {
                      val hhh_1 = PreHelper.Delete(hhh, l_ns, AbsString.alpha(n_index.toString))._1
                      PreHelper.PropStore(hhh_1, l_ns, AbsString.alpha(n_index.toString), Value(lset_new))
                    }
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    val b_eq = Operator.bopSEq(PreHelper.Proto(hhh, l_ns, NumStr), Value(lset_old))._1._3
                    val hhh_1 =
                      if (BoolTrue <= b_eq) {
                        val _hhh = PreHelper.Delete(hhh, l_ns, NumStr)._1
                        PreHelper.PropStore(_hhh, l_ns, NumStr, Value(lset_new))
                      }
                      else hhh
                    val hhh_2 =
                      if (BoolFalse <= b_eq) hhh
                      else hhh
                    hhh_1 + hhh_2
                  case _ => hhh /* exception ?? */
                }
              })
            })
            ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(lset_old)), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.removeChild" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_child = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)._2
          if (!lset_child.isEmpty) {
            /* location for clone node */
            val h_1 = lset_this.foldLeft(h)((hh, l_node) => {
              val lset_ns = PreHelper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              lset_ns.foldLeft(hh)((hhh, l_ns) => {
                val n_len = Operator.ToUInt32(PreHelper.Proto(h, l_ns, AbsString.alpha("length")))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n) =>
                    val n_index = (0 until n.toInt).indexWhere((i) => {
                      BoolTrue <= Operator.bopSEq(PreHelper.Proto(hhh, l_ns, AbsString.alpha(i.toString)), Value(lset_child))._1._3
                    })
                    if (n_index < 0)
                      hhh
                    else {
                      val hhh_1 = PreHelper.Delete(hhh, l_ns, AbsString.alpha(n_index.toString))._1
                      val hhh_2 = (n_index+1 until n.toInt).foldLeft(hhh_1)((_h, i) => {
                        val v_next = PreHelper.Proto(_h, l_ns,  AbsString.alpha(i.toString))
                        val _h1 = PreHelper.Delete(_h, l_ns, AbsString.alpha(i.toString))._1
                        PreHelper.PropStore(_h1, l_ns, AbsString.alpha((i-1).toString), v_next)
                      })
                      // decrease the length of childNodes by 1
                      PreHelper.PropStore(hhh_2, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n - 1)))
                    }

                  case _ if AbsNumber.isUIntAll(n_len) =>
                    val b_eq = Operator.bopSEq(PreHelper.Proto(hhh, l_ns, NumStr), Value(lset_child))._1._3
                    val hhh_1 =
                      if (BoolTrue <= b_eq) PreHelper.Delete(hhh, l_ns, NumStr)._1
                      else hhh
                    val hhh_2 =
                      if (BoolFalse <= b_eq) hhh
                      else hhh
                    hhh_1 + hhh_2
                  case _ => hhh /* exception ?? */
                }
              })
            })
            ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(lset_child)), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.appendChild" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_child = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)._2
          if (!lset_child.isEmpty) {
            /* location for clone node */
            val h_1 = lset_this.foldLeft(h)((hh, l_node) => {
              val lset_ns = PreHelper.Proto(hh, l_node, AbsString.alpha("childNodes"))._2
              lset_ns.foldLeft(hh)((hhh, l_ns) => {
                val n_len = Operator.ToUInt32(PreHelper.Proto(hhh, l_ns, AbsString.alpha("length")))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n) =>
                    val hhh_1 = PreHelper.PropStore(hhh, l_ns, AbsString.alpha(n.toInt.toString), Value(lset_child))
                    PreHelper.PropStore(hhh_1, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n+1)))
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    PreHelper.PropStore(hhh, l_ns, NumStr, Value(lset_child))
                  case _ => hhh /* exception ?? */
                }
              })
            })
            ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(lset_child)), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.hasChildNodes" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          val b_return = lset_this.foldLeft[AbsBool](BoolBot)((b, l) => {
            val lset_child = PreHelper.Proto(h, l, AbsString.alpha("childNodes"))._2
            lset_child.foldLeft(b)((bb, ll) => {
              val absnum = PreHelper.Proto(h, ll, AbsString.alpha("length"))._1._4
              bb + (absnum.getAbsCase match {
                case AbsBot => BoolBot
                case _ if AbsNumber.isUIntAll(absnum) => BoolTop
                case _ => absnum.getSingle match {
                  case Some(n) if AbsNumber.isNum(absnum) => if (n != 0) BoolTrue else BoolFalse
                  case _ => BoolFalse
              }})
            })
          })
          if (b_return </ BoolBot)
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(b_return)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.cloneNode" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val b_deep = PreHelper.toBoolean(getArgValue_pre(h, ctx, args, "0", PureLocalLoc))
          if (b_deep </ BoolBot) {
            /* unsound, 'deep' arugment is ingnored */
            /* location for clone node */
            val l_r = addrToLoc(addr1, Recent)
            val (h_1, ctx_1)  = PreHelper.Oldify(h, ctx, addr1)
            /* this node only */
            val o_node = lset_this.foldLeft(ObjBot)((o, l) => o + h_1(l))
            val h_2 = h_1.update(l_r, o_node)
            ((PreHelper.ReturnStore(h_2, PureLocalLoc, Value(l_r)), ctx_1), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.normalize" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* unsound, do nothing */
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("DOMNode.isSupported" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_feature = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val s_version = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          if (s_feature </ StrBot || s_version </ StrBot)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.hasAttributes" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* imprecise semantic */
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(BoolTop)), ctx), (he, ctxe))
        })),
      ("DOMNode.compareDocumentPosition" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val v_other = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          if (v_other </ ValueBot) {
            /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UInt)), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.isSameNode" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val v_other = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          if (v_other </ ValueBot) {
            val v_return = Operator.bopSEq(Value(lset_this), Value(v_other._2))
            ((PreHelper.ReturnStore(h, PureLocalLoc, v_return), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.lookupPrefix" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_uri = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (s_uri </ StrBot)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(StrTop) + Value(NullTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.isDefaultNamespace" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_uri = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (s_uri </ StrBot)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.lookupNamespaceURI" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_prefix = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (s_prefix </ StrBot)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(StrTop) + Value(NullTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.isEqualNode" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val v_arg = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          if (v_arg </ ValueBot)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.getFeature" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_feature = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val s_version = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          if (s_feature </ StrBot || s_version </ StrBot)
          /* unsound semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(NullTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.setUserData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_key = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val v_data = getArgValue_pre(h, ctx, args, "1", PureLocalLoc)
          val v_handler = getArgValue_pre(h, ctx, args, "2", PureLocalLoc)
          if (s_key </ StrBot || v_data </ ValueBot || v_handler </ ValueBot)
          /* unsound semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(NullTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNode.getUserData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_key = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (s_key </ StrBot)
          /* unsound semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(NullTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      ("DOMNode.insertBefore" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_new = getArgValue(h, ctx, args, "0")._2
          val lset_ref = getArgValue(h, ctx, args, "1")._2
          if (!lset_new.isEmpty && !lset_ref.isEmpty) {
            /* location for clone node */
            val LP1 = lset_this.foldLeft(LPBot)((lpset, l_node) => {
              val lset_ns = Helper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              lset_ns.foldLeft(lpset)((lpset1, l_ns) => {
                val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n_length) =>
                    val n_index = (0 until n_length.toInt).indexWhere((i) => {
                      BoolTrue <= Operator.bopSEq(Helper.Proto(h, l_ns, AbsString.alpha(i.toString)), Value(lset_ref))._1._3
                    })
                    if (n_index < 0)
                      lpset1
                    else {
                      val LP1_1 = (n_index+1 until n_length.toInt).foldLeft(lpset1)((lpset2, i) => {
                        val i_rev = n_length - i + 1
                        val LP1_1_1 = AccessHelper.Delete_def(h, l_ns, AbsString.alpha(i_rev.toString))
                        lpset2++LP1_1_1 ++ AccessHelper.PropStore_def(h, l_ns, AbsString.alpha((i_rev+1).toString))
                      })
                      val LP1_2 = AccessHelper.PropStore_def(h, l_ns, AbsString.alpha(n_index.toString))
                      // increase the length of childNodes by 1
                      lpset1 ++ LP1_1 ++ LP1_2 ++
                        AccessHelper.PropStore_def(h, l_ns, AbsString.alpha("length"))
                    }
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    val b_eq = Operator.bopSEq(Helper.Proto(h, l_ns, NumStr), Value(lset_ref))._1._3
                    val LP1_1 =
                      if (BoolTrue <= b_eq) AccessHelper.PropStore_def(h, l_ns, NumStr)
                      else LPBot
                    val LP1_2 =
                      if (BoolFalse <= b_eq) lpset1
                      else LPBot
                    lpset1 ++ LP1_1 ++ LP1_2
                  case _ => lpset1 /* exception ?? */
                }
              })
            })
            LP1 + (SinglePureLocalLoc, "@return")
          }
          else
            LPBot
        })),
      ("DOMNode.replaceChild" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_new = getArgValue(h, ctx, args, "0")._2
          val lset_old = getArgValue(h, ctx, args, "1")._2
          if (!lset_new.isEmpty && !lset_old.isEmpty) {
            /* location for clone node */
            val LP1 = lset_this.foldLeft(LPBot)((lpset, l_node) => {
              val lset_ns = Helper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              lset_ns.foldLeft(lpset)((lpset1, l_ns) => {
                val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n) =>
                    val n_index = (0 until n.toInt).indexWhere((i) => {
                      BoolTrue <= Operator.bopSEq(Helper.Proto(h, l_ns, AbsString.alpha(i.toString)), Value(lset_old))._1._3
                    })
                    if (n_index < 0)
                      lpset1
                    else {
                      val LP1_1 = AccessHelper.Delete_def(h, l_ns, AbsString.alpha(n_index.toString))
                      lpset1 ++ LP1_1 ++ AccessHelper.PropStore_def(h, l_ns, AbsString.alpha(n_index.toString))
                    }
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    val b_eq = Operator.bopSEq(Helper.Proto(h, l_ns, NumStr), Value(lset_old))._1._3
                    val LP1_1 =
                      if (BoolTrue <= b_eq) {
                        val LP1_1_1 = AccessHelper.Delete_def(h, l_ns, NumStr)
                        LP1_1_1 ++ AccessHelper.PropStore_def(h, l_ns, NumStr)
                      }
                      else LPBot
                    lpset1 ++ LP1_1
                  case _ => lpset1 /* exception ?? */
                }
              })
            })
            LP1 + (SinglePureLocalLoc, "@return")
          }
          else
            LPBot
        })),
      ("DOMNode.removeChild" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_child = getArgValue(h, ctx, args, "0")._2
          if (!lset_child.isEmpty) {
            /* location for clone node */
            val LP1 = lset_this.foldLeft(LPBot)((lpset, l_node) => {
              val lset_ns = Helper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              lset_ns.foldLeft(lpset)((lpset1, l_ns) => {
                val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n) =>
                    val n_index = (0 until n.toInt).indexWhere((i) => {
                      BoolTrue <= Operator.bopSEq(Helper.Proto(h, l_ns, AbsString.alpha(i.toString)), Value(lset_child))._1._3
                    })
                    if (n_index < 0)
                      lpset1
                    else {
                      val LP1_1 = AccessHelper.Delete_def(h, l_ns, AbsString.alpha(n_index.toString))
                      val LP1_2 = (n_index+1 until n.toInt).foldLeft(LPBot)((lpset2, i) => {
                        lpset2 ++ AccessHelper.Delete_def(h, l_ns, AbsString.alpha(i.toString)) ++
                          AccessHelper.PropStore_def(h, l_ns, AbsString.alpha((i-1).toString))
                      })
                      lpset1 ++ LP1_2 ++ LP1_2 ++
                        AccessHelper.PropStore_def(h, l_ns, AbsString.alpha("length"))
                    }
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    val b_eq = Operator.bopSEq(Helper.Proto(h, l_ns, NumStr), Value(lset_child))._1._3
                    val LP1_1 =
                      if (BoolTrue <= b_eq) AccessHelper.Delete_def(h, l_ns, NumStr)
                      else LPBot
                    lpset1 ++ LP1_1
                  case _ => lpset1 /* exception ?? */
                }
              })
            })
            LP1 + (SinglePureLocalLoc, "@return")
          }
          else
            LPBot
        })),
      ("DOMNode.appendChild" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_child = getArgValue(h, ctx, args, "0")._2
          if (!lset_child.isEmpty) {
            val LP1 = lset_this.foldLeft(LPBot)((lpset, l_node) => {
              val lset_ns = Helper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              lset_ns.foldLeft(lpset)((lpset1, l_ns) => {
                val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n) =>
                    lpset1 ++ AccessHelper.PropStore_def(h, l_ns, AbsString.alpha(n.toInt.toString)) ++
                      AccessHelper.PropStore_def(h, l_ns, AbsString.alpha("length"))
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    lpset1 ++ AccessHelper.PropStore_def(h, l_ns, NumStr)
                  case _ => lpset1 /* exception ?? */
                }
              })
            })
            LP1 + (SinglePureLocalLoc, "@return")
          }
          else
            LPBot
        })),
      ("DOMNode.hasChildNodes" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.cloneNode" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //val l_r = addrToLoc(addr1, Recent)
          //val LP1 = AccessHelper.Oldify_def(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          val LP2 = lset_this.foldLeft(LPBot)((lpset, l) => {
            val prop_set = h(l).map.keySet
            prop_set.foldLeft(lpset)((lpset1, prop) =>
              lpset1  ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0),Recent), prop)))
          })
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNode.normalize" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.isSupported" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.hasAttributes" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.compareDocumentPosition" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.isSameNode" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.lookupPrefix" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.isDefaultNamespace" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.lookupNamespaceURI" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.isEqualNode" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.getFeature" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.setUserData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.getUserData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      ("DOMNode.insertBefore" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_new = getArgValue(h, ctx, args, "0")._2
          val lset_ref = getArgValue(h, ctx, args, "1")._2
          val LP1 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          if (!lset_new.isEmpty && !lset_ref.isEmpty) {
            /* location for clone node */
            val LP2 = lset_this.foldLeft(LPBot)((lpset, l_node) => {
              val lset_ns = Helper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              val LP2_1 = AccessHelper.Proto_use(h, l_node, AbsString.alpha("childNodes"))
              lset_ns.foldLeft(lpset++LP2_1)((lpset1, l_ns) => {
                val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
                val LP2_2 = AccessHelper.Proto_use(h, l_ns, AbsString.alpha("length"))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n_length) =>
                    val n_index = (0 until n_length.toInt).indexWhere((i) => {
                      BoolTrue <= Operator.bopSEq(Helper.Proto(h, l_ns, AbsString.alpha(i.toString)), Value(lset_ref))._1._3
                    })
                    val LP2_3 = (0 until n_length.toInt).foldLeft(LPBot)((lpset, i) =>
                      lpset ++ AccessHelper.Proto_use(h, l_ns, AbsString.alpha(i.toString)))
                    if (n_index < 0)
                      lpset1 ++ LP2_2 ++ LP2_3
                    else {
                      val LP2_4 = (n_index+1 until n_length.toInt).foldLeft(LPBot)((lpset2, i) => {
                        val i_rev = n_length - i + 1
                        val LP2_4_1 = AccessHelper.Proto_use(h, l_ns,  AbsString.alpha(i_rev.toString))
                        val LP2_4_2 = AccessHelper.Delete_use(h, l_ns, AbsString.alpha(i_rev.toString))
                        lpset2 ++ LP2_4_1 ++ LP2_4_2 ++
                          AccessHelper.PropStore_use(h, l_ns, AbsString.alpha((i_rev+1).toString))
                      })
                      val LP2_5 = AccessHelper.PropStore_use(h, l_ns, AbsString.alpha(n_index.toString))
                      lpset1 ++ LP2_2 ++ LP2_3 ++ LP2_4 ++ LP2_5 ++
                        AccessHelper.PropStore_use(h, l_ns, AbsString.alpha("length"))
                    }
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    val b_eq = Operator.bopSEq(Helper.Proto(h, l_ns, NumStr), Value(lset_ref))._1._3
                    val LP2_3 = AccessHelper.Proto_use(h, l_ns, NumStr)
                    val LP2_4 =
                      if (BoolTrue <= b_eq) AccessHelper.PropStore_use(h, l_ns, NumStr)
                      else LPBot
                    val hhh_2 =
                      if (BoolFalse <= b_eq) lpset1
                      else LPBot
                    lpset1 ++ LP2_2 ++ LP2_3 ++ LP2_4
                  case _ => lpset1 /* exception ?? */
                }
              })
            })
            LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
          }
          else
            LP1
        })),
      ("DOMNode.replaceChild" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_new = getArgValue(h, ctx, args, "0")._2
          val lset_old = getArgValue(h, ctx, args, "1")._2
          val LP1 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          if (!lset_new.isEmpty && !lset_old.isEmpty) {
            /* location for clone node */
            val LP2 = lset_this.foldLeft(LPBot)((lpset, l_node) => {
              val lset_ns = Helper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              val LP2_1 = AccessHelper.Proto_use(h, l_node, AbsString.alpha("childNodes"))
              lset_ns.foldLeft(lpset++LP2_1)((lpset1, l_ns) => {
                val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
                val LP2_2 = AccessHelper.Proto_use(h, l_ns, AbsString.alpha("length"))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n) =>
                    val n_index = (0 until n.toInt).indexWhere((i) => {
                      BoolTrue <= Operator.bopSEq(Helper.Proto(h, l_ns, AbsString.alpha(i.toString)), Value(lset_old))._1._3
                    })
                    val LP2_3 = (0 until n.toInt).foldLeft(LPBot)((lpset, i) =>
                      lpset ++ AccessHelper.Proto_use(h, l_ns, AbsString.alpha(i.toString)))
                    if (n_index < 0)
                      lpset1 ++ LP2_2 ++ LP2_3
                    else {
                      val LP2_4 = AccessHelper.Delete_use(h, l_ns, AbsString.alpha(n_index.toString))
                      lpset1 ++ LP2_2 ++ LP2_3 ++
                        AccessHelper.PropStore_use(h, l_ns, AbsString.alpha(n_index.toString))
                    }
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    val b_eq = Operator.bopSEq(Helper.Proto(h, l_ns, NumStr), Value(lset_old))._1._3
                    val LP2_3 = AccessHelper.Proto_use(h, l_ns, NumStr)
                    val LP2_4 =
                      if (BoolTrue <= b_eq) {
                        AccessHelper.Delete_use(h, l_ns, NumStr) ++
                          AccessHelper.PropStore_use(h, l_ns, NumStr)
                      }
                      else LPBot
                    lpset1 ++ LP2_2 ++ LP2_3 ++ LP2_4
                  case _ => lpset1 /* exception ?? */
                }
              })
            })
            LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
          }
          else
            LP1
        })),
      ("DOMNode.removeChild" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_child = getArgValue(h, ctx, args, "0")._2
          val LP1 = getArgValue_use(h, ctx, args, "0")
          if (!lset_child.isEmpty) {
            /* location for clone node */
            val LP2 = lset_this.foldLeft(LPBot)((lpset, l_node) => {
              val lset_ns = Helper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              val LP2_1 = AccessHelper.Proto_use(h, l_node, AbsString.alpha("childNodes"))
              lset_ns.foldLeft(lpset++LP2_1)((lpset1, l_ns) => {
                val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
                val LP2_2 = AccessHelper.Proto_use(h, l_ns, AbsString.alpha("length"))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n) =>
                    val n_index = (0 until n.toInt).indexWhere((i) => {
                      BoolTrue <= Operator.bopSEq(Helper.Proto(h, l_ns, AbsString.alpha(i.toString)), Value(lset_child))._1._3
                    })
                    val LP2_3 = (0 until n.toInt).foldLeft(LPBot)((lpset, i) =>
                      lpset ++ AccessHelper.Proto_use(h, l_ns, AbsString.alpha(i.toString)))
                    if (n_index < 0)
                      lpset1 ++ LP2_2 ++ LP2_3
                    else {
                      val LP2_4 = AccessHelper.Delete_def(h, l_ns, AbsString.alpha(n_index.toString))
                      val LP2_5 = (n_index+1 until n.toInt).foldLeft(LPBot)((lpset2, i) => {
                        val LP2_5_1 = AccessHelper.Proto_use(h, l_ns,  AbsString.alpha(i.toString))
                        LP2_5_1 ++ AccessHelper.Delete_use(h, l_ns, AbsString.alpha(i.toString)) ++
                          AccessHelper.PropStore_use(h, l_ns, AbsString.alpha((i-1).toString))
                      })
                      lpset1 ++ LP2_2 ++ LP2_3 ++ LP2_4 ++ LP2_5 ++
                        AccessHelper.PropStore_use(h, l_ns, AbsString.alpha("length"))
                    }
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    val b_eq = Operator.bopSEq(Helper.Proto(h, l_ns, NumStr), Value(lset_child))._1._3
                    val LP2_3 = AccessHelper.Proto_use(h, l_ns, NumStr)
                    val LP2_4 =
                      if (BoolTrue <= b_eq) AccessHelper.Delete_use(h, l_ns, NumStr)
                      else LPBot
                    lpset1 ++ LP2_2 ++ LP2_3 ++ LP2_4
                  case _ => lpset1 /* exception ?? */
                }
              })
            })
            LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
          }
          else
            LP1
        })),
      ("DOMNode.appendChild" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val lset_child = getArgValue(h, ctx, args, "0")._2
          val LP1 = getArgValue_use(h, ctx, args, "0")
          if (!lset_child.isEmpty) {
            val LP2 = lset_this.foldLeft(LPBot)((lpset, l_node) => {
              val lset_ns = Helper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
              val LP2_1 = AccessHelper.Proto_use(h, l_node, AbsString.alpha("childNodes"))
              lset_ns.foldLeft(lpset++LP2_1)((lpset1, l_ns) => {
                val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
                val LP2_2 = AccessHelper.Proto_use(h, l_ns, AbsString.alpha("length"))
                AbsNumber.getUIntSingle(n_len) match {
                  case Some(n) =>
                    lpset1 ++ LP2_2 ++
                      AccessHelper.PropStore_use(h, l_ns, AbsString.alpha(n.toInt.toString)) ++
                      AccessHelper.PropStore_use(h, l_ns, AbsString.alpha("length"))
                  case _ if AbsNumber.isUIntAll(n_len) =>
                    lpset1 ++ LP2_2 ++ AccessHelper.PropStore_use(h, l_ns, NumStr)
                  case _ => lpset1 /* exception ?? */
                }
              })
            })
            LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
          }
          else
            LP1
        })),
      ("DOMNode.hasChildNodes" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => {
            val lset_child = Helper.Proto(h, l, AbsString.alpha("childNodes"))._2
            val LP1_1 = AccessHelper.Proto_use(h, l, AbsString.alpha("childNodes"))
            lset_child.foldLeft(lpset++LP1_1)((lpset1, ll) => {
              lpset1 ++ AccessHelper.Proto_use(h, ll, AbsString.alpha("length"))
            })
          })
          LP1 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        })),
      ("DOMNode.cloneNode" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //val l_r = addrToLoc(addr1, Recent)
          //val LP1 = AccessHelper.Oldify_def(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          val LP2 = lset_this.foldLeft(LPBot)((lpset, l) => {
            val prop_set = h(l).map.keySet
            prop_set.foldLeft(lpset)((lpset1, prop) => lpset1 + (l, prop))
          })
          LP1 ++ LP2 ++ getArgValue_use(h, ctx, args, "0") +
            (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        })),
      ("DOMNode.normalize" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.isSupported" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNode.hasAttributes" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNode.compareDocumentPosition" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNode.isSameNode" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        })),
      ("DOMNode.lookupPrefix" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNode.isDefaultNamespace" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNode.lookupNamespaceURI" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNode.isEqualNode" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNode.getFeature" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNode.setUserData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1") ++ getArgValue_use(h, ctx, args, "2") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNode.getUserData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        }))
    )
  }

  /* instance */
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  
  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = {
    val nodeName = node.getNodeName
    val nodeValue = node.getNodeValue
    val namespaceURI = node.getNamespaceURI
    val prefix = node.getPrefix
    val localName = node.getLocalName
    val baseURI = node.getBaseURI
    val textContent = node.getTextContent
    List(
      // DOM Level 1
      ("nodeName",   PropValue(ObjectValue(AbsString.alpha(if(nodeName!=null) nodeName else ""), F, T, T))),
      ("nodeValue",   PropValue(ObjectValue(AbsString.alpha(if(nodeValue!=null) nodeValue else ""), T, T, T))),
      ("nodeType",   PropValue(ObjectValue(AbsNumber.alpha(node.getNodeType), F, T, T))),
      // Introduced in DOM Level 2
      ("namespaceURI",   PropValue(ObjectValue(AbsString.alpha(if(namespaceURI != null) namespaceURI else ""), F, T, T))),
      ("prefix",   PropValue(ObjectValue(AbsString.alpha(if(prefix!=null) prefix else ""), T, T, T))),
      ("localName",   PropValue(ObjectValue(AbsString.alpha(if(localName != null) localName else ""), F, T, T))),
      // Introduced in DOM Level 3
      //    ("baseURI",   PropValue(ObjectValue(AbsString.alpha(if(baseURI!=null) baseURI else ""), F, T, T))),
      ("textContent",   PropValue(ObjectValue(AbsString.alpha(if(textContent!=null) textContent else ""), T, T, T))))
    // TODO: 'OwnerDocument' in DOM Level 2, 'baseURI' in DOM Level 3
  }

  def getInsList(node: Node, ownerDocument: PropValue): List[(String, PropValue)] = getInsList(node) :+
    ("ownerDocument", ownerDocument)

  def getInsList(nodeName: PropValue, nodeValue: PropValue, nodeType: PropValue, parentNode: PropValue, childNodes: PropValue,
                 firstChild: PropValue, lastChild: PropValue, previousSibling: PropValue, nextSibling: PropValue , ownerDocument: PropValue,
                 namespaceURI: PropValue, prefix: PropValue, localName: PropValue, textContent: PropValue) : List[(String, PropValue)] = List(
    ("nodeName", nodeName),
    ("nodeValue", nodeValue),
    ("nodeType", nodeType),
    ("parentNode", parentNode),
    ("childNodes", childNodes),
    ("firstChild", firstChild),
    ("lastChild", lastChild),
    ("previousSibling", previousSibling),
    ("nextSibling", nextSibling),
    ("ownerDocument", ownerDocument),
    ("namespaceURI", namespaceURI),
    ("prefix", prefix),
    ("localName", localName),
    ("textContent", textContent))
  // TODO: 'baseURI' in DOM Level 3


  def getInsList(nodeName: PropValue, nodeValue: PropValue, nodeType: PropValue, parentNode: PropValue, childNodes: PropValue,
                 firstChild: PropValue, lastChild: PropValue, previousSibling: PropValue, nextSibling: PropValue , ownerDocument: PropValue,
                 namespaceURI: PropValue, prefix: PropValue, localName: PropValue, textContent: PropValue, attributes: PropValue) : List[(String, PropValue)] = List(
    ("nodeName", nodeName),
    ("nodeValue", nodeValue),
    ("nodeType", nodeType),
    ("parentNode", parentNode),
    ("childNodes", childNodes),
    ("firstChild", firstChild),
    ("lastChild", lastChild),
    ("previousSibling", previousSibling),
    ("nextSibling", nextSibling),
    ("ownerDocument", ownerDocument),
    ("namespaceURI", namespaceURI),
    ("prefix", prefix),
    ("localName", localName),
    ("textContent", textContent),
    ("attributes", attributes)
  )
  // TODO: 'baseURI' in DOM Level 3


}
