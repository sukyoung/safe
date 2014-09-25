/*******************************************************************************
    Copyright (c) 2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse=>F, BoolTrue=>T}
import kr.ac.kaist.jsaf.analysis.typing.{Helper, Operator}
import org.w3c.dom.bootstrap.DOMImplementationRegistry
import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSSerializer
import org.w3c.dom.DOMConfiguration
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.Element
import org.w3c.dom.Attr
import org.w3c.dom.html._
import org.apache.html.dom._
import javax.xml.transform._
import javax.xml.transform.dom._
import javax.xml.transform.stream._
import java.io.StringWriter
import kr.ac.kaist.jsaf.analysis.cfg.InternalError
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.DOMNode
import kr.ac.kaist.jsaf.analysis.typing.AddressManager
import scala.collection.immutable.HashMap
import kr.ac.kaist.jsaf.{ShellParameters, Shell}

object DOMTree {
  val quiet = 
    if(Shell.params.command == ShellParameters.CMD_WEBAPP_BUG_DETECTOR)
      true
    else
      false
  
  // Insert a node represented by 'lset_new' before the node represented by 'lset_ref'
  def insertBefore(h: Heap, lset_this: LocSet, lset_new: LocSet, lset_ref: LocSet): Heap = {
    if (!lset_new.isEmpty && !lset_ref.isEmpty) {
      /* location for clone node */
      val h_1 = lset_this.foldLeft(HeapBot)((hh, l_node) => {
        val lset_ns = Helper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
        val hh2 = lset_ns.foldLeft(HeapBot)((hhh, l_ns) => {
          val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
          val hhh3 = AbsNumber.getUIntSingle(n_len) match {
            case Some(n_length) =>
              val n_index = (0 until n_length.toInt).indexWhere((i) => {
                BoolTrue <= Operator.bopSEq(Helper.Proto(h, l_ns, AbsString.alpha(i.toString)), Value(lset_ref))._1._3
              })
              if (n_index < 0)
                h
              else {
                val _hhh = (n_index until n_length.toInt).foldLeft(h)((_h, i) => {
                  val i_rev: Int = n_index + (n_length.toInt - i) - 1 
                  val v_move = Helper.Proto(_h, l_ns,  AbsString.alpha(i_rev.toString))
                  val _h1 = Helper.Delete(_h, l_ns, AbsString.alpha(i_rev.toString))._1
                  Helper.PropStore(_h1, l_ns, AbsString.alpha((i_rev+1).toString), v_move)
                })
                val _hhh_1 = Helper.PropStore(_hhh, l_ns, AbsString.alpha(n_index.toString), Value(lset_new))
                // increase the length of childNodes by 1
                Helper.PropStore(_hhh_1, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n_length.toInt + 1)))
              }
            case _ if AbsNumber.isUIntAll(n_len) =>
              val b_eq = Operator.bopSEq(Helper.Proto(h, l_ns, NumStr), Value(lset_ref))._1._3
              val hhh_1 =
                if (BoolTrue <= b_eq) Helper.PropStore(h, l_ns, NumStr, Value(lset_new))
                else HeapBot
              val hhh_2 =
                if (BoolFalse <= b_eq) h
                else HeapBot
              hhh_1 + hhh_2
            case _ => h /* exception ?? */
          }
          hhh + hhh3
        })
        hh2+hh
      })
      /* 'previousSibling' update of the reference child */
      val (h_2, oldpresibling) = lset_ref.foldLeft[(Heap, Value)]((HeapBot, ValueBot))((d, l) => {
        val oldpres = Helper.Proto(h_1, l, AbsString.alpha("previousSibling"))
        val h_2_1 = Helper.PropStore(h_1, l, AbsString.alpha("previousSibling"), Value(lset_new))
        (h_2_1 + d._1, d._2 + oldpres)
      })
      /* 'parentNode', 'nextSibling', `previousSibling' update of the new child */
      val h_3 = lset_new.foldLeft(HeapBot)((_h, l) => {
        val h_3_1 = Helper.PropStore(h_2, l, AbsString.alpha("parentNode"), Value(lset_this))
        val h_3_2 = Helper.PropStore(h_3_1, l, AbsString.alpha("nextSibling"), Value(lset_ref))
        _h + Helper.PropStore(h_3_2, l, AbsString.alpha("previousSibling"), oldpresibling)
      })
      /* 'nextSibling' update of the old previousSibling of the reference child */
      val h_4 = if(oldpresibling._2.isEmpty) h_3
        else 
          oldpresibling._2.foldLeft(HeapBot)((_h, l) =>
            _h + Helper.PropStore(h_3, l, AbsString.alpha("nextSibling"), Value(lset_new))
          )
      h_4
    }
    else
      HeapBot
  }
  
  // Append a child node represented by 'lset_child' to the children nodes of 'lset_target'
  def appendChild(h: Heap, lset_target: LocSet, lset_child: LocSet): Heap = {
    if (!lset_target.isEmpty && !lset_child.isEmpty) {
      val isElemNode = lset_child.foldLeft(BoolBot)((b,l) =>
        b + Helper.HasOwnProperty(h, l, AbsString.alpha("children"))
      )
      val (h_1, lastC_lset) = lset_target.foldLeft((h, LocSetBot))((d, l_node) => {
        /* lastChild */
        val l_childlset = Helper.Proto(d._1, l_node, AbsString.alpha("lastChild"))._2
        /* current childNodes */
        val lset_ns = Helper.Proto(d._1, l_node, AbsString.alpha("childNodes"))._2
        /* children */
        val lset_children = Helper.Proto(d._1, l_node, AbsString.alpha("children"))._2
        val (h_append, f_childlset) = lset_ns.foldLeft((d._1, LocSetBot))((dd, l_ns) => {
          /* length of current childNodes */
          val n_len = Operator.ToUInt32(Helper.Proto(dd._1, l_ns, AbsString.alpha("length")))
          val hhh_2 = AbsNumber.getUIntSingle(n_len) match {
            case Some(n) =>
              /* childNodes[length] := new_child */
              val hhh_1 = Helper.PropStore(dd._1, l_ns, AbsString.alpha(n.toInt.toString), Value(lset_child))
              /* childNodes["length"] := length + 1 */
              Helper.PropStore(hhh_1, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n+1)))
            case _  if AbsNumber.isUIntAll(n_len) =>
              Helper.PropStore(dd._1, l_ns, NumStr, Value(lset_child))
            case _ => dd._1 /* exception ?? */
          }
          val firstChild_lset =  Helper.Proto(hhh_2, l_ns, AbsString.alpha(0.toInt.toString))._2
          (hhh_2, dd._2 ++ firstChild_lset)
        })
        val h_c = isElemNode.gamma match {
          case Some(b) if b.size == 1 && b.head == true =>
            lset_children.foldLeft(h_append)((dd, l_ns) => {
              /* length of current children */
              val n_len = Operator.ToUInt32(Helper.Proto(dd, l_ns, AbsString.alpha("length")))
              val hhh_2 = AbsNumber.getUIntSingle(n_len) match {
                case Some(n) =>
                  /* children[length] := new_child */
                  val hhh_1 = Helper.PropStore(dd, l_ns, AbsString.alpha(n.toInt.toString), Value(lset_child))
                  /* children["length"] := length + 1 */
                  Helper.PropStore(hhh_1, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n+1)))
                case _  if AbsNumber.isUIntAll(n_len) =>
                  Helper.PropStore(dd, l_ns, NumStr, Value(lset_child))
                case _ => dd /* exception ?? */
              }
              hhh_2
            })
          case Some(b) if b.size == 1 && b.head  == false => h_append
          case Some(b) =>
           lset_children.foldLeft(h_append)((dd, l_ns) => {
              /* length of current children */
              val n_len = Operator.ToUInt32(Helper.Proto(dd, l_ns, AbsString.alpha("length")))
              val hhh_2 = AbsNumber.getUIntSingle(n_len) match {
                case Some(n) =>
                  /* children[length] := new_child */
                  val hhh_1 = Helper.PropStoreWeak(dd, l_ns, AbsString.alpha(n.toInt.toString), Value(lset_child))
                  /* children["length"] := length + 1 */
                  Helper.PropStoreWeak(hhh_1, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n+1)))
                case _  if AbsNumber.isUIntAll(n_len) =>
                  Helper.PropStoreWeak(dd, l_ns, NumStr, Value(lset_child))
                case _ => dd /* exception ?? */
              }
              hhh_2
            })

          case None if BoolTop <= isElemNode =>
            lset_children.foldLeft(h_append)((dd, l_ns) => {
              /* length of current children */
              val n_len = Operator.ToUInt32(Helper.Proto(dd, l_ns, AbsString.alpha("length")))
              val hhh_2 = AbsNumber.getUIntSingle(n_len) match {
                case Some(n) =>
                  /* children[length] := new_child */
                  val hhh_1 = Helper.PropStoreWeak(dd, l_ns, AbsString.alpha(n.toInt.toString), Value(lset_child))
                  /* children["length"] := length + 1 */
                  Helper.PropStoreWeak(hhh_1, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n+1)))
                case _  if AbsNumber.isUIntAll(n_len) =>
                  Helper.PropStoreWeak(dd, l_ns, NumStr, Value(lset_child))
                case _ => dd /* exception ?? */
              }
              hhh_2
            })

          case _ => h_append
        }
        // 'firstChild' update
        val h_append_1 = Helper.PropStore(h_c, l_node, AbsString.alpha("firstChild"),Value(f_childlset))
        // 'lastChild' update
        val h_append_2 = Helper.PropStore(h_append_1, l_node, AbsString.alpha("lastChild"), Value(lset_child))
        (h_append_2, d._2 ++ l_childlset)
      })
      // 'nextSibling' update'
      val h_2 = lastC_lset.foldLeft(h_1)((_h, l) => Helper.PropStore(_h, l, AbsString.alpha("nextSibling"),Value(lset_child)))
      // 'parentNode', 'previousSibling' update of the appended child
      lset_child.foldLeft(h_2)((_h, l) => {
        val _h_1 = Helper.PropStore(_h, l, AbsString.alpha("parentNode"),Value(lset_target))
        if(!lastC_lset.isEmpty)
          Helper.PropStore(_h_1, l, AbsString.alpha("previousSibling"), Value(lastC_lset))
        else
          Helper.PropStore(_h_1, l, AbsString.alpha("previousSibling"), Value(NullTop))

      })
    }
    else
      HeapBot
  }

  // Prepend a child node represented by 'lset_child' to the children nodes of 'lset_target'
  def prependChild(h: Heap, lset_target: LocSet, lset_child: LocSet): Heap = {
    if (!lset_target.isEmpty && !lset_child.isEmpty) {
      val isElemNode = lset_child.foldLeft(BoolBot)((b,l) =>
        b + Helper.HasOwnProperty(h, l, AbsString.alpha("children"))
      )
      val h_1 = lset_target.foldLeft(h)((h1, l_node) => {
        /* current childNodes */
        val lset_ns = Helper.Proto(h1, l_node, AbsString.alpha("childNodes"))._2
        /* children */
        val lset_children = Helper.Proto(h1, l_node, AbsString.alpha("children"))._2
        val h_append = lset_ns.foldLeft(h1)((h2, l_ns) => {
          /* length of current childNodes */
          val n_len = Operator.ToUInt32(Helper.Proto(h2, l_ns, AbsString.alpha("length")))
          AbsNumber.getUIntSingle(n_len) match {
            case Some(n) =>
              /* move 0->1, 1->2, ... n_len-1 -> n_len */
              val h5 = (0 until n.toInt).foldLeft(h2)((h3, i) => {
                val v = Helper.Proto(h3, l_ns, AbsString.alpha((n-i-1).toInt.toString))
                Helper.PropStore(h3, l_ns, AbsString.alpha((n-i).toInt.toString), v)
              })
              /* childNodes[0] := new_child */
              val h6 = Helper.PropStore(h5, l_ns, AbsString.alpha("0"), Value(lset_child))
              /* childNodes["length"] := length + 1 */
              Helper.PropStore(h6, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n+1)))
            case _ if AbsNumber.isUIntAll(n_len) =>
              Helper.PropStore(h2, l_ns, NumStr, Value(lset_child))
            case _ => h2 /* exception ?? */
          }
        })
        val h_c = isElemNode.gamma match {
          case Some(b) if b.size == 1 && b.head == true =>
            lset_children.foldLeft(h_append)((dd, l_ns) => {
              /* length of current children */
              val n_len = Operator.ToUInt32(Helper.Proto(dd, l_ns, AbsString.alpha("length")))
              val hhh_2 = AbsNumber.getUIntSingle(n_len) match {
                case Some(n) =>
                  /* children[length] := new_child */
                  val hhh_1 = Helper.PropStore(dd, l_ns, AbsString.alpha(n.toInt.toString), Value(lset_child))
                  /* children["length"] := length + 1 */
                  Helper.PropStore(hhh_1, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n+1)))
                case _  if AbsNumber.isUIntAll(n_len) =>
                  Helper.PropStore(dd, l_ns, NumStr, Value(lset_child))
                case _ => dd /* exception ?? */
              }
              hhh_2
            })
          case Some(b) if b.size == 1 && b.head == false => h_append
          case Some(b) =>
            lset_children.foldLeft(h_append)((dd, l_ns) => {
              /* length of current children */
              val n_len = Operator.ToUInt32(Helper.Proto(dd, l_ns, AbsString.alpha("length")))
              val hhh_2 = AbsNumber.getUIntSingle(n_len) match {
                case Some(n) =>
                  /* children[length] := new_child */
                  val hhh_1 = Helper.PropStore(dd, l_ns, AbsString.alpha(n.toInt.toString), Value(lset_child))
                  /* children["length"] := length + 1 */
                  Helper.PropStore(hhh_1, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n+1)))
                case _  if AbsNumber.isUIntAll(n_len) =>
                  Helper.PropStore(dd, l_ns, NumStr, Value(lset_child))
                case _ => dd /* exception ?? */
              }
              hhh_2
            })

          case None if BoolTop <= isElemNode =>
            lset_children.foldLeft(h_append)((dd, l_ns) => {
              /* length of current children */
              val n_len = Operator.ToUInt32(Helper.Proto(dd, l_ns, AbsString.alpha("length")))
              val hhh_2 = AbsNumber.getUIntSingle(n_len) match {
                case Some(n) =>
                  /* children[length] := new_child */
                  val hhh_1 = Helper.PropStoreWeak(dd, l_ns, AbsString.alpha(n.toInt.toString), Value(lset_child))
                  /* children["length"] := length + 1 */
                  Helper.PropStoreWeak(hhh_1, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n+1)))
                case _  if AbsNumber.isUIntAll(n_len) =>
                  Helper.PropStoreWeak(dd, l_ns, NumStr, Value(lset_child))
                case _ => dd /* exception ?? */
              }
              hhh_2
            })

          case _ => h_append
        }

        val o_target = h_append(l_node).update("firstChild", PropValue(ObjectValue(Value(lset_child),F,T,F)))
        h_append.update(l_node, o_target)
      })
      h_1
    }
    else
      HeapBot
  }

  // Remove a child node represented by 'lset_child' from the children nodes of 'lset_target'
  def removeChild(h: Heap, lset_parent: LocSet, lset_child: LocSet): Heap = {
    /* arguments */
    if (!lset_child.isEmpty) {
      /* location for clone node */
      val h_1 = lset_parent.foldLeft(h)((h1, l_node) => {
        val lset_ns = Helper.Proto(h, l_node, AbsString.alpha("childNodes"))._2
        val lset_children = Helper.Proto(h, l_node, AbsString.alpha("children"))._2
        val (h1_2, firstChild_lset, lastChild_lset) = lset_ns.foldLeft((h1, LocSetBot, LocSetBot))((hl, l_ns) => {
          val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
          val (h_2, lchild_lset) = AbsNumber.getUIntSingle(n_len) match {
            case Some(n) =>
              val n_index = (0 until n.toInt).indexWhere((i) => {
                BoolTrue <= Operator.bopSEq(Helper.Proto(hl._1, l_ns, AbsString.alpha(i.toString)), Value(lset_child))._1._3
              })
              if (n_index < 0)
                (hl._1, LocSetBot)
              else {
                val hhh_1 = Helper.Delete(hl._1, l_ns, AbsString.alpha(n_index.toString))._1
                val hhh_2 = (n_index+1 until n.toInt).foldLeft(hhh_1)((_h, i) => {
                  val v_next = Helper.Proto(_h, l_ns,  AbsString.alpha(i.toString))
                  val _h1 = Helper.Delete(_h, l_ns, AbsString.alpha(i.toString))._1
                  Helper.PropStore(_h1, l_ns, AbsString.alpha((i-1).toString), v_next)
                })
                // decrease the length of childNodes by 1
                val hhh_3 = Helper.PropStore(hhh_2, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n - 1)))
                val lastC_lset = if(n-1==0) LocSetBot
                                 else Helper.Proto(hhh_3, l_ns, AbsString.alpha((n-2).toString))._2
                (hhh_3, lastC_lset)
              }

            case _ if AbsNumber.isUIntAll(n_len) =>
              val b_eq = Operator.bopSEq(Helper.Proto(hl._1, l_ns, NumStr), Value(lset_child))._1._3
              val h2_1 =
                if (BoolTrue <= b_eq) Helper.Delete(hl._1, l_ns, NumStr)._1
                else HeapBot
              val h2_2 =
                if (BoolFalse <= b_eq) hl._1
                else HeapBot
              val h2_3 = h2_1 + h2_2
              (h2_3, Helper.Proto(h2_3, l_ns, NumStr)._2)
            case _ => (hl._1, LocSetBot) /* exception ?? */
          }
          val fchild_lset = Helper.Proto(h_2, l_ns,  AbsString.alpha("0"))._2
          (h_2, hl._2 ++ fchild_lset, hl._3 ++ lchild_lset)

        })
        val h1_3 = lset_children.foldLeft(h1_2)((hl, l_ns) => {
          val n_len = Operator.ToUInt32(Helper.Proto(h, l_ns, AbsString.alpha("length")))
          val h_2 = AbsNumber.getUIntSingle(n_len) match {
            case Some(n) =>
              val n_index = (0 until n.toInt).indexWhere((i) => {
                BoolTrue <= Operator.bopSEq(Helper.Proto(hl, l_ns, AbsString.alpha(i.toString)), Value(lset_child))._1._3
              })
              if (n_index < 0)
                hl
              else {
                val hhh_1 = Helper.Delete(hl, l_ns, AbsString.alpha(n_index.toString))._1
                val hhh_2 = (n_index+1 until n.toInt).foldLeft(hhh_1)((_h, i) => {
                  val v_next = Helper.Proto(_h, l_ns,  AbsString.alpha(i.toString))
                  val _h1 = Helper.Delete(_h, l_ns, AbsString.alpha(i.toString))._1
                  Helper.PropStore(_h1, l_ns, AbsString.alpha((i-1).toString), v_next)
                })
                // decrease the length of children by 1
                val hhh_3 = Helper.PropStore(hhh_2, l_ns, AbsString.alpha("length"), Value(AbsNumber.alpha(n - 1)))
                hhh_3
              }

            case _ if AbsNumber.isUIntAll(n_len) =>
              val b_eq = Operator.bopSEq(Helper.Proto(hl, l_ns, NumStr), Value(lset_child))._1._3
              val h2_1 =
                if (BoolTrue <= b_eq) Helper.Delete(hl, l_ns, NumStr)._1
                else HeapBot
              val h2_2 =
                if (BoolFalse <= b_eq) hl
                else HeapBot
              val h2_3 = h2_1 + h2_2
              h2_3
            case _ => hl  /* exception ?? */
          }
          h_2 
        })

        /* 'firstChild' and 'lastChild' update of the parentNode */
        val firstChildVal = if(firstChild_lset.isEmpty) Value(NullTop) else Value(firstChild_lset)
        val lastChildVal = if(lastChild_lset.isEmpty) Value(NullTop) else Value(lastChild_lset)
        val h1_4 = Helper.PropStore(h1_3, l_node, AbsString.alpha("firstChild"), firstChildVal)
        Helper.PropStore(h1_4, l_node, AbsString.alpha("lastChild"), lastChildVal)
      })
      /* 'parentNode', 'previousSibling', 'nextSibling' update of the removed child */
      val (h_2, preSib, nextSib) = lset_child.foldLeft((h_1, ValueBot, ValueBot))((d, l) => {
        val preS = Helper.Proto(d._1, l, AbsString.alpha("previousSibling"))
        val nextS = Helper.Proto(d._1, l, AbsString.alpha("nextSibling"))
        val h_2_1 = Helper.PropStore(d._1, l, AbsString.alpha("parentNode"), Value(NullTop))
        val h_2_2 = Helper.PropStore(h_2_1, l, AbsString.alpha("previousSibling"), Value(NullTop))
        val h_3_3 = Helper.PropStore(h_2_2, l, AbsString.alpha("nextSibling"), Value(NullTop))
        (h_3_3, preS + d._2, nextS + d._3)
      })

      /* 'nextSibling' update of the previous sibling of the removed child */
      val h_3 = preSib._2.foldLeft(h_2)((_h, l) =>
        Helper.PropStore(_h, l, AbsString.alpha("nextSibling"), nextSib)
      )
      
      /* 'previousSibling' update of the next sibling of the removed child */
      val h_4 = nextSib._2.foldLeft(h_3)((_h, l) =>
        Helper.PropStore(_h, l, AbsString.alpha("previousSibling"), preSib)
      )

      h_4
    }
    else
      HeapBot
  }
  
  /* API setting for DOM tree serialization */
  /*
  private val registry: DOMImplementationRegistry = DOMImplementationRegistry.newInstance
  private val impl: DOMImplementationLS = registry.getDOMImplementation("LS").asInstanceOf[DOMImplementationLS]
  private val writer: LSSerializer = impl.createLSSerializer
  private val config: DOMConfiguration = writer.getDomConfig
  config.setParameter("xml-declaration", false)
  */
  private var doc: HTMLDocumentImpl = new HTMLDocumentImpl

  private val t = TransformerFactory.newInstance().newTransformer()
  t.setOutputProperty(OutputKeys.METHOD, "html");

  def new_concrete_Document : HTMLDocumentImpl = new HTMLDocumentImpl

  /* Serialize an abstract DOM tree to an abstract string */
  def serializeToStr(h: Heap, l: Loc): AbsString = {
    doc = new HTMLDocumentImpl
    val root_op = toConcreteDOMTree(h, l, doc)
    root_op match {
      case Some((n, m)) =>
        val docfrag = doc.createDocumentFragment()
        for( i <- (0 until n.getChildNodes.getLength)) {
          docfrag.appendChild(n.getFirstChild)
        }
        val strwriter = new StringWriter()
        t.transform(new DOMSource(docfrag), new StreamResult(strwriter))
        AbsString.alpha(strwriter.toString.toLowerCase)
      case None => OtherStr
    }
  }
  
  /* Translate an abstract DOM tree to a concrete DOM tree */
  def toConcreteDOMTree(h: Heap, l: Loc, doc: HTMLDocumentImpl): Option[(Node, HashMap[Node, Loc])] = {
    var newmap = HashMap[Node, Loc]()
    val node_op = toConcreteNode(h, l, doc)
    
    if(!node_op.isEmpty) {
      val node = node_op.get
      newmap += (node -> l)
      val children_set: LocSet = Helper.Proto(h, l, AbsString.alpha("childNodes"))._2
      if(children_set.size == 1) {
        val children_loc = children_set.head
        val a_num_children = Helper.toNumber(Helper.toPrimitive(Helper.Proto(h, children_loc, AbsString.alpha("length"))))
        val (num_children_isconcrete, num_children): (Boolean, Int) = a_num_children.gamma match {
          case Some(n) if n.size ==1 => (true, n.head.toInt)
          case _ => 
            if (!quiet) println("Imprecise DOM : multiple children : " + DomainPrinter.printLoc(l))
            (false, 0)
        }
        if(num_children_isconcrete) {
          // leaf node
          if(num_children == 0) 
            Some(node, newmap)
          else {
            val (children_loc_list, isconcrete) = (0 until num_children).foldLeft[(List[Loc], Boolean)]((List(), true))((ll, i) => {
                val lset = Helper.Proto(h, children_loc, AbsString.alpha(i.toString))._2
                if(lset.size != 1) {
                  if (!quiet) println("Imprecise DOM : multiple( " + lset.size + " ) children : " + DomainPrinter.printLoc(l))
                  (ll._1, false)
                }
                else {
                  ((ll._1 :+ lset.head), ll._2)
                }
              })
            if(isconcrete) {
              val (children, children_isconcrete, finalmap) = children_loc_list.foldLeft[(List[Node], Boolean, HashMap[Node, Loc])]((List(), true, newmap))((nn, l) => {
                val n_op = toConcreteDOMTree(h, l, doc)
                (n_op) match {
                  case Some((n, map)) => (nn._1 :+ n, nn._2, nn._3 ++ map)
                  case None => (nn._1, false, nn._3)
                }
              })
              if(children_isconcrete) {
                children.foreach(n => node.appendChild(n))
                Some(node, finalmap)
              }
              // children : not concrete
              else
                None
            }
            // children : not concrete
            else
              None
          }

        } 
        // children : not concrete
        else 
          None
      }
      // children: not concrete
      else
        None
    }
    
    // root node: not concrete value
    else {
      if (!quiet) println("Imprecise DOM : imprecise node " + DomainPrinter.printLoc(l))
      None
    }

  }

  /* Translate an abstract DOM node to a concrete DOM node */
  def toConcreteNode(h: Heap, l: Loc, doc: HTMLDocumentImpl): Option[Node] = {
    if(AddressManager.isOldLoc(l)) None
    else {
      val a_nodetype = Helper.toNumber(Helper.toPrimitive_better(h, Helper.Proto(h, l, AbsString.alpha("nodeType"))))
      val a_nodename = Helper.toString(Helper.toPrimitive_better(h, Helper.Proto(h, l, AbsString.alpha("nodeName"))))
      val a_attributes = Helper.Proto(h, l, AbsString.alpha("attributes"))._2
      val (nodetype_isconcrete, nodetype): (Boolean, Int) = a_nodetype.gamma match {
        case Some(n) if n.size == 1 => (true, n.head.toInt)
        case _ => 
          if (!quiet) println("Imprecise DOM : multiple nodetype : " + DomainPrinter.printLoc(l))
          (false, 0)
      }

      val (nodename_isconcrete, nodename) = a_nodename.gamma match {
        case Some(ss) if ss.size == 1 => (true, ss.head)
        case _ => 
          if (!quiet) println("Imprecise DOM : multiple nodename : " + DomainPrinter.printLoc(l))
          (false, "")
      }

      val (attributes_isconcrete, attributes) = 
        if(a_attributes.size == 1) (true, a_attributes.head)
        else if(a_attributes.size == 0) (true, -1)
        else {
          if (!quiet) println("Imprecise DOM : multiple attributes : " + DomainPrinter.printLoc(l))
         (false, -1)
       }

      if(nodetype_isconcrete && nodename_isconcrete && attributes_isconcrete) {
        nodetype match {
          case DOMNode.ELEMENT_NODE =>
            val node_op = nodename match {
              case "HTML" =>
                val element = new HTMLHtmlElementImpl(doc, nodename)
                Some(element)
              case "HEAD" =>
                val element = new HTMLHeadElementImpl(doc, nodename)
                Some(element)
              case "LINK" =>
                val element = new HTMLLinkElementImpl(doc, nodename)
                Some(element)
              case "TITLE" =>
                val element = new HTMLTitleElementImpl(doc, nodename)
                Some(element)
              case "META" =>
                val element = new HTMLMetaElementImpl(doc, nodename)
                Some(element)
              case "BASE" =>
                val element = new HTMLBaseElementImpl(doc, nodename)
                Some(element)
              case "ISINDEX" =>
                val element = new HTMLIsIndexElementImpl(doc, nodename)
                Some(element)
              case "STYLE" =>
                val element = new HTMLStyleElementImpl(doc, nodename)
                Some(element)
              case "BODY" =>
                val element = new HTMLBodyElementImpl(doc, nodename)
                Some(element)
              case "FORM" =>
                val element = new HTMLFormElementImpl(doc, nodename)
                Some(element)
              case "SELECT" =>
                val element = new HTMLSelectElementImpl(doc, nodename)
                Some(element)
              case "OPTGROUP" =>
                val element = new HTMLOptGroupElementImpl(doc, nodename)
                Some(element)
              case "OPTION" =>
                val element = new HTMLOptionElementImpl(doc, nodename)
                Some(element)
              case "INPUT" =>
                val element = new HTMLInputElementImpl(doc, nodename)
                Some(element)
              case "TEXTAREA" =>
                val element = new HTMLTextAreaElementImpl(doc, nodename)
                Some(element)
              case "BUTTON" =>
                val element = new HTMLButtonElementImpl(doc, nodename)
                Some(element)
              case "LABEL" =>
                val element = new HTMLLabelElementImpl(doc, nodename)
                Some(element)
              case "FIELDSET" =>
                val element = new HTMLFieldSetElementImpl(doc, nodename)
                Some(element)
              case "LEGEND" =>
                val element = new HTMLLegendElementImpl(doc, nodename)
                Some(element)
              case "UL" =>
                val element = new HTMLUListElementImpl(doc, nodename)
                Some(element)
              case "OL" =>
                val element = new HTMLOListElementImpl(doc, nodename)
                Some(element)
              case "DL" =>
                val element = new HTMLDListElementImpl(doc, nodename)
                Some(element)
              case "DIR" =>
                val element = new HTMLDirectoryElementImpl(doc, nodename)
                Some(element)
              case "MENU" =>
                val element = new HTMLMenuElementImpl(doc, nodename)
                Some(element)
              case "LI" =>
                val element = new HTMLLIElementImpl(doc, nodename)
                Some(element)
              case "DIV" =>
                val element = new HTMLDivElementImpl(doc, "div")
                Some(element)
              case "P" =>
                val element = new HTMLParagraphElementImpl(doc, nodename)
                Some(element)
              case "H1" | "H2" | "H3" | "H4" | "H5" | "H6" =>
                val element = new HTMLHeadingElementImpl(doc, nodename)
                Some(element)
              case "BLACKQUOTE" | "Q" =>
                val element = new HTMLQuoteElementImpl(doc, nodename)
                Some(element)
              case "PRE" =>
                val element = new HTMLPreElementImpl(doc, nodename)
                Some(element)
              case "BR" =>
                val element = new HTMLBRElementImpl(doc, nodename)
                Some(element)
              case "BASEFONT" =>
                val element = new HTMLBaseFontElementImpl(doc, nodename)
                Some(element)
              case "FONT" =>
                val element = new HTMLFontElementImpl(doc, nodename)
                Some(element)
              case "HR" =>
                val element = new HTMLHRElementImpl(doc, nodename)
                Some(element)
              case "INS" | "DEL" =>
                val element = new HTMLModElementImpl(doc, nodename)
                Some(element)
              case "A" =>
                val element = new HTMLAnchorElementImpl(doc, nodename)
                Some(element)
              case "IMG" =>
                val element = new HTMLImageElementImpl(doc, nodename)
                Some(element)
              case "OBJECT" =>
                val element = new HTMLObjectElementImpl(doc, nodename)
                Some(element)
              case "PARAM" =>
                val element = new HTMLParamElementImpl(doc, nodename)
                Some(element)
              case "APPLET" =>
                val element = new HTMLAppletElementImpl(doc, nodename)
                Some(element)
              case "AREA" =>
                val element = new HTMLAreaElementImpl(doc, nodename)
                Some(element)
              case "SCRIPT" =>
                val element = new HTMLScriptElementImpl(doc, nodename)
                Some(element)
              case "TABLE" =>
                val element = new HTMLTableElementImpl(doc, nodename)
                Some(element)
              case "CAPTION" =>
                val element = new HTMLTableCaptionElementImpl(doc, nodename)
                Some(element)
              case "COL" =>
                val element = new HTMLTableColElementImpl(doc, nodename)
                Some(element)
              case "THEAD" | "TFOOT" | "TBODY" =>
                val element = new HTMLTableSectionElementImpl(doc, nodename)
                Some(element)
              case "TR" =>
                val element = new HTMLTableRowElementImpl(doc, nodename)
                Some(element)
              case "TH" | "TD" =>
                val element = new HTMLTableCellElementImpl(doc, nodename)
                Some(element)
              case "FRAMESET" =>
                val element = new HTMLFrameSetElementImpl(doc, nodename)
                Some(element)
              case "FRAME" =>
                val element = new HTMLFrameElementImpl(doc, nodename)
                Some(element)
              case "IFRAME" =>
                val element = new HTMLIFrameElementImpl(doc, nodename)
                Some(element)
              // Special tags
              case "SUB" | "SUP" | "SPAN" | "BDO" | "BDI" =>
                val element = new HTMLElementImpl(doc, nodename)
                Some(element)
              // Font tags
              case "TT" | "I" | "B" | "U" | "S" | "STRIKE" | "BIG" | "SMALL" =>
                val element = new HTMLElementImpl(doc, nodename)
                Some(element)
              // Phrase tags
              case "EM" | "STRONG" | "DFN" | "CODE" | "SAMP" | "KBD" | "VAR" | "CITE" | "ACRONYM" | "ABBR"  =>
                val element = new HTMLElementImpl(doc, nodename)
                Some(element)
              // List tags
              case "DD" | "DT" =>
                val element = new HTMLElementImpl(doc, nodename)
                Some(element)
              // etc
              case "NOFRAMES" | "NOSCRIPT" | "ADDRESS" | "CENTER" =>
                val element = new HTMLElementImpl(doc, nodename)
                Some(element)
              /* HTML 5 */
              case "CANVAS" =>
                val element = new HTMLElementImpl(doc, nodename)
                Some(element)
              case "DATALIST" =>
                val element = new HTMLBaseElementImpl(doc, nodename)
                Some(element)
              case "HEADER" | "FOOTER" | "ARTICLE" | "SECTION" | "NAV" =>
                val element = new HTMLBaseElementImpl(doc, nodename)
                Some(element) 
              case _ => 
                if (!quiet) System.err.println("* Warning: " + nodename + " - not modeled yet.")
                val element = new HTMLBaseElementImpl(doc, nodename)
                Some(element)
            }
            val a_id = Helper.toString(Helper.toPrimitive_better(h, Helper.Proto(h, l, AbsString.alpha("id"))))
            val (id_isconcrete, id) = a_id.gamma match {
              case Some(ss) if ss.size == 1 => (true, ss.head)
               case _ => 
                if (!quiet) println("Imprecise DOM : multiple ids : " + DomainPrinter.printLoc(l))
               (false, "")
            }
            if(!node_op.isEmpty && id_isconcrete) {
              val node = node_op.get
              // 'attributes' translation
              if(attributes != -1){
                val a_len = Helper.toNumber(Helper.toPrimitive(Helper.Proto(h, attributes, AbsString.alpha("length"))))
                val (len_isconcrete, len) = a_len.gamma match {
                  case Some(l) if l.size == 1 => (true, l.head.toInt)
                  case _ => 
                    if (!quiet) println("Imprecise DOM : imprecise length of attributes : " + DomainPrinter.printLoc(l))
                    (false, 0)
                }
                if(len_isconcrete){
                  val (attr_list, attr_isconcrete) = (0 until len).foldLeft[(List[Attr], Boolean)]((List(), true))((aa, i) => {
                    val attr_lset = Helper.Proto(h, attributes, AbsString.alpha(i.toString))._2
                    if(attr_lset.size == 1) {
                      val attr_l = attr_lset.head
                      val attr_op = toConcreteDOMTree(h, attr_l, doc)
                      if(!attr_op.isEmpty)
                        attr_op.get._1 match {
                          case a: Attr => 
                            (aa._1 :+ a, aa._2)
                          case _ =>
                            throw new InternalError("No attribute node")
                        }
                      else
                        (aa._1, false)
                    }
                    else
                      (aa._1, false)
                  })
                  if(attr_isconcrete){
                    attr_list.foreach( a => node.setAttributeNode(a))
                    if(id != "")
                      node.setId(id)
                    Some(node)
                  } 
                  else{
                    if (!quiet) println("Imprecise DOM : imprecise attr node " + DomainPrinter.printLoc(l))
                    None
                  }
                }
                // attributes: not concrete
                else {
                  if (!quiet) println("Imprecise DOM : imprecise attributes node " + DomainPrinter.printLoc(l))
                  None
                }
              }
              else
                Some(node)
            }
            else {
              None
            }
          case DOMNode.ATTRIBUTE_NODE =>
            val attr = doc.createAttribute(nodename)
            Some(attr)
          case DOMNode.TEXT_NODE =>
            val a_data = Helper.toString(Helper.toPrimitive(Helper.Proto(h, l, AbsString.alpha("data"))))
            val (data_isconcrete, data) = a_data.gamma match {
              case Some(ss) if ss.size == 1 => (true, ss.head)
              case _ => 
                if (!quiet) println("Imprecise DOM : imprecise text data : " + DomainPrinter.printLoc(l))
                (false, "")
            }
            if(data_isconcrete){
              val text = doc.createTextNode(data)
              Some(text)
            }
            else
              None
          case DOMNode.CDATA_SECTION_NODE =>
            throw new InternalError("Not yet implemented") 
          case DOMNode.ENTITY_REFERENCE_NODE =>
            throw new InternalError("Not yet implemented") 
          case DOMNode.ENTITY_NODE =>
            throw new InternalError("Not yet implemented") 
          case DOMNode.PROCESSING_INSTRUCTION_NODE =>
            throw new InternalError("Not yet implemented") 
          case DOMNode.COMMENT_NODE =>
            val a_data = Helper.toString(Helper.toPrimitive(Helper.Proto(h, l, AbsString.alpha("data"))))
            val (data_isconcrete, data) = a_data.gamma match {
              case Some(ss) if ss.size == 1 => (true, ss.head)
              case _ => 
                if (!quiet) println("Imprecise DOM : imprecise comment data : " + DomainPrinter.printLoc(l))
                (false, "")
            }
            if(data_isconcrete){
              val comment = doc.createComment(data)
              Some(comment)
            }
            else
              None

          case DOMNode.DOCUMENT_NODE =>
            Some(doc)
          case DOMNode.DOCUMENT_TYPE_NODE =>
            val doctype = doc.createDocumentType("xml", null, null)
            Some(doctype)
          case DOMNode.DOCUMENT_FRAGMENT_NODE =>
            val docfrag = doc.createDocumentFragment
            Some(docfrag)
          case DOMNode.NOTATION_NODE =>
            throw new InternalError("Not yet implemented") 
          case _ => throw new InternalError("No possible node type " + nodetype) 
        }


      }
      // no concrete value
      else
        None
    }
  }
  
  val reg_no = """[^(^\s*)].*[^($\s*)]|.""".r
  // implementation of getElementsByClassName on a concrete tree
  def getElementsByClassName_concrete(root : Node, className: String) : List[Node] = {
    // remove white spaces at front and back
    val className_n = (reg_no.findAllIn(className).toList)(0)
    val list = root match {
      case e: Element => 
         val value = e.getAttribute("class")
         val new_match = ("""(\s|^)""" + className_n + """(\s|$)""").r
         if(new_match.findAllIn(value).toList.size != 0)
            List(root)
         else
            List()
      case _ => List()  
    }
    val childNodes = root.getChildNodes
    (0 until childNodes.getLength).foldLeft(list)((ll, i) => {
      ll ++ getElementsByClassName_concrete(childNodes.item(i), className)
    })
  }

}
