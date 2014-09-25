/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import org.w3c.dom.Node
import org.w3c.dom.Element
import kr.ac.kaist.jsaf.analysis.typing.Helper
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.analysis.typing.{Semantics, ControlPoint, Helper, PreHelper}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.{DOMElement, DOMException, DOMNodeList, DOMNamedNodeMap}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMObject.CSSStyleDeclaration
import kr.ac.kaist.jsaf.Shell

object HTMLTableRowElement extends DOM {
  private val name = "HTMLTableRowElement"

  /* predefined locatoins */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_ins = newSystemRecentLoc(name + "Ins")

  /* constructor */
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )
     /* instance */
  private val prop_ins: List[(String, AbsProperty)] = 
       HTMLElement.getInsList2() ++ List(
      ("@class",    AbsConstValue(PropValue(AbsString.alpha("Object")))),
      ("@proto",    AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
      ("@extensible", AbsConstValue(PropValue(BoolTrue))),
      // DOM Level 1
      ("align", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("bgColor", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("ch", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("chOff", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("vAlign", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("vAlign", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("rowIndex", AbsConstValue(PropValue(ObjectValue(NumTop, F, T, T)))),
      ("sectionRowIndex", AbsConstValue(PropValue(ObjectValue(NumTop, F, T, T)))),
      ("cells", AbsConstValue(PropValue(ObjectValue(Value(HTMLCollection.loc_ins), T, T, T))))
    )


  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(HTMLElement.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("insertCell", AbsBuiltinFunc("HTMLTableRowElement.insertCell", 1)),
    ("deleteCell", AbsBuiltinFunc("HTMLTableRowElement.deleteCell", 1))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )


  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = if(Shell.params.opt_Dommodel2) List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global), (loc_ins, prop_ins)

  ) else List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)  ) 
  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      // Modeling Based on WHATWG Living Standard 
      // Section 4.9.8 The tr elemeent
      // HTMLElement insertCell(optional long index = -1)
      ("HTMLTableRowElement.insertCell" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
        val lset_env = h(SinglePureLocalLoc)("@env")._2._2
        val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
        if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
        val addr_env = (cp._1._1, set_addr.head)
        val addr1 = cfg.getAPIAddress(addr_env, 0)
        val addr2 = cfg.getAPIAddress(addr_env, 1)
        val addr3 = cfg.getAPIAddress(addr_env, 2)
        val addr4 = cfg.getAPIAddress(addr_env, 3)
        val l_r = addrToLoc(addr1, Recent)
        val l_nodes = addrToLoc(addr2, Recent)
        val l_attributes = addrToLoc(addr3, Recent)
        val l_style = addrToLoc(addr4, Recent)

        val lset_this = h(SinglePureLocalLoc)("@this")._2._2
        
        // DOMException object with the IndexSizeError exception
        val es = Set(DOMException.INDEX_SIZE_ERR)
        val (he_1, ctxe_1) = DOMHelper.RaiseDOMException(h, ctx, es)
        
        val h1 = HTMLTopElement.setInsLoc(h, l_r)
        val (h_1, ctx_1) = Helper.Oldify(h1, ctx, addr1)
        val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
        val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
        val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)
        
        // object for 'childNodes' property
        val childNodes_list = DOMNodeList.getInsList(0)
        val childNodes = childNodes_list.foldLeft(Obj.empty)((x, y) => x.update(y._1, y._2))
        
        // object for 'attributes' property
        val attributes_list = DOMNamedNodeMap.getInsList(0)
        val attributes = attributes_list.foldLeft(Obj.empty)((x, y) => x.update(y._1, y._2))

        // object for 'style' property
        val style_list = CSSStyleDeclaration.getInsList()
        val style = style_list.foldLeft(Obj.empty)((x, y) => x.update(y._1, y._2))

        // object for HTMLTableCellElement
        val td_obj_proplist = DOMElement.getInsList(PropValue(ObjectValue(AbsString.alpha("TD"),F, T, T))):::
            HTMLTableCellElement.default_getInsList:::List(
              ("childNodes", PropValue(ObjectValue(l_nodes, F, T, T))),
              ("attributes", PropValue(ObjectValue(l_attributes, F, T, T))),
              ("style", PropValue(ObjectValue(l_style, T, T, T))))
        val td_obj = td_obj_proplist.foldLeft(Obj.empty)((x, y) => x.update(y._1, y._2))
        val h_5 = h_4.update(l_nodes, childNodes).update(l_attributes, attributes).update(l_style, style).update(l_r, td_obj)

        /* argument */
        val arg_val = getArgValue(h_5, ctx_4, args, "0")
        val default_val = if(Value(UndefTop) <= arg_val) AbsNumber.alpha(-1) else NumBot
        val new_argval = Value(PValue(UndefBot, arg_val._1._2, arg_val._1._3, arg_val._1._4, arg_val._1._5), arg_val._2)
        val index = Helper.toNumber(Helper.toPrimitive_better(h_5, new_argval)) + default_val
        
        
        index.getAbsCase match { 
            case AbsSingle =>
              val in: Int = index.getSingle.get.toInt
              /* if index is less than -1, the method must throw an IndexSizeError exception */
              if(in < -1)
                ((HeapBot, ContextBot), (he + he_1, ctxe + ctxe_1))
              /* if index is equal to -1, the method must create a td element, append it to the tr element,
                 and return the newly created td element */
              else if(in == -1) {
                val h_6 = lset_this.foldLeft(h_5)((_h, l_this) => {
                  val _h_2 = DOMTree.appendChild(_h, LocSet(l_this), LocSet(l_r))
                  // cells collection
                  val cells = Helper.Proto(_h_2, l_this, AbsString.alpha("cells"))._2
                  // the number of items in cells collection
                  val _h_3 = cells.foldLeft(_h_2)((hh, l) => {
                    val length = Helper.toNumber(Helper.toPrimitive_better(hh, Helper.Proto(hh, l, AbsString.alpha("length"))))
                    length.getAbsCase match {
                      case AbsSingle =>
                        val len: Int = length.getSingle.get.toInt
                        val hh_2 = Helper.PropStore(hh, l, AbsString.alpha(len.toString), Value(l_r))
                        Helper.PropStore(hh_2, l, AbsString.alpha("length"), Value(AbsNumber.alpha(len+1)))
                      case AbsMulti => 
                        val hh_2 = Helper.PropStore(hh, l, NumStr, Value(l_r))
                        Helper.PropStore(hh_2, l, AbsString.alpha("length"), Value(UInt))
                      case AbsTop => 
                        val hh_2 = Helper.PropStore(hh, l, NumStr, Value(l_r))
                        Helper.PropStore(hh_2, l, AbsString.alpha("length"), Value(UInt))
                      case AbsBot => hh
                    }
                  })
                  _h_3
                })
               ((Helper.ReturnStore(h_6, Value(l_r)), ctx_4), (he, ctxe))
              }
              else {
                val (is_exception, h_2) = lset_this.foldLeft((BoolBot, h_5))((bh, l_this) => {
                  // cells collection
                    val cells = Helper.Proto(bh._2, l_this, AbsString.alpha("cells"))._2
                    // the number of items in cells collection
                    cells.foldLeft(bh)((bbh, l) => {
                      val cells_len = Helper.toNumber(Helper.toPrimitive_better(bbh._2, Helper.Proto(bbh._2, l, AbsString.alpha("length")))) 
                      cells_len.getAbsCase match {
                        case AbsSingle =>
                          val len: Int = cells_len.getSingle.get.toInt
                          /* if index is greater than the number of elements in the cells collection, 
                             the method must throw an IndexSizeError exception */ 
                          if(in > len) 
                            (bbh._1 + BoolTrue, bbh._2)
                          /* if index is equal to the number of elements in the cells collection, 
                             the method must create a td element, append it to the tr element, 
                             and return the newly created td element. */ 
                          else if(in == len) {
                            val hh_1 = DOMTree.appendChild(bbh._2, LocSet(l_this), LocSet(l_r))
                            val hh_2 = Helper.PropStore(hh_1, l, AbsString.alpha(len.toString), Value(l_r))
                            val _h_3 = Helper.PropStore(hh_2, l, AbsString.alpha("length"), Value(AbsNumber.alpha(len+1)))
                            (bbh._1 + BoolFalse, _h_3)
                          }
                          /* Otherwise, the method must create a td element, insert it as a child of the tr element,
                             immediately before the indexth td or th element in the cells collection,
                             and finally must return the newly create td element. */
                          else {
                            // indexth element
                            val elem = Helper.Proto(bbh._2, l, AbsString.alpha(in.toString))._2
                            val _h_2 = DOMTree.insertBefore(bbh._2, LocSet(l_this), LocSet(l_r), elem)
                            val _h_3 = (in until len).foldLeft(_h_2)((__h, i) => {
                              val i_rev = len - i 
                              val v_move = Helper.Proto(__h, l, AbsString.alpha(i_rev.toString))
                              val __h2 = Helper.Delete(__h, l, AbsString.alpha(i_rev.toString))._1
                              Helper.PropStore(__h2, l, AbsString.alpha((i_rev+1).toString), v_move)
                            })
                            val _h_4 = Helper.PropStore(_h_3, l, AbsString.alpha(in.toString), Value(l_r))
                            val _h_5 = Helper.PropStore(_h_4, l, AbsString.alpha("length"), Value(AbsNumber.alpha(len+1)))
                            (bbh._1 + BoolFalse, _h_5) 
                          }
                        case AbsMulti =>
                          val _h_2 = Helper.PropStore(bbh._2, l, AbsString.alpha("length"), Value(UInt))
                          val _h_3 = Helper.PropStore(_h_2, l, NumStr, Value(l_r))
                          (BoolTop, _h_3)
                        case AbsTop =>
                          val _h_2 = Helper.PropStore(bbh._2, l, AbsString.alpha("length"), Value(UInt))
                          val _h_3 = Helper.PropStore(_h_2, l, NumStr, Value(l_r))
                          (BoolTop, _h_3)
                        case AbsBot =>
                          (bbh._1, HeapBot)
                      }
                    })
                  })
                is_exception.gamma match {
                  case Some(s) if s.size == 1 => 
                    // exception
                    if(s.head) ((HeapBot, ContextBot), (he + he_1, ctxe + ctxe_1))                    
                    else ((Helper.ReturnStore(h_2, Value(l_r)), ctx_4), (he, ctxe))
                  case Some(s) if s.size > 1 =>
                    ((Helper.ReturnStore(h_2, Value(l_r)), ctx_4), (he + he_1, ctxe + ctxe_1))
                  case None if BoolTop <= is_exception =>
                    ((Helper.ReturnStore(h_2, Value(l_r)), ctx_4), (he + he_1, ctxe + ctxe_1))
                  case None =>
                    ((HeapBot, ContextBot), (he, ctxe))
                }
              }
            case AbsBot =>
              ((HeapBot, ContextBot), (he, ctxe))
            case _ => /* AbsMulti, AbsTop */
              val h_2 = lset_this.foldLeft(h)((bh, l_this) => {
                // cells collection
                val cells = Helper.Proto(bh, l_this, AbsString.alpha("cells"))._2
                // the number of items in cells collection
                cells.foldLeft(bh)((bbh, l) => {
                  val _h_2 = Helper.PropStore(bbh, l, AbsString.alpha("length"), Value(UInt))
                  val _h_3 = Helper.PropStore(_h_2, l, NumStr, Value(l_r))
                  _h_3
                })
               })
               ((Helper.ReturnStore(h_2, Value(l_r)), ctx_4), (he + he_1, ctxe + ctxe_1))
        }
      }))
      //case "HTMLTableRowElement.deleteCell" => ((h,ctx),(he,ctxe))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLTableRowElement.insertCell" => ((h,ctx),(he,ctxe))
      //case "HTMLTableRowElement.deleteCell" => ((h,ctx),(he,ctxe))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLTableRowElement.insertCell" => ((h,ctx),(he,ctxe))
      //case "HTMLTableRowElement.deleteCell" => ((h,ctx),(he,ctxe))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLTableRowElement.insertCell" => ((h,ctx),(he,ctxe))
      //case "HTMLTableRowElement.deleteCell" => ((h,ctx),(he,ctxe))
    )
  }

  /* instance */
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case e: Element => 
      // This object has all properties of the HTMLElement object 
      HTMLElement.getInsList(node) ++ List(
      ("@class",    PropValue(AbsString.alpha("Object"))),
      ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
      ("@extensible", PropValue(BoolTrue)),
      // DOM Level 1
      ("align", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("align")), T, T, T))),
      ("bgColor", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("bgColor")), T, T, T))),
      ("ch", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("ch")), T, T, T))),
      ("chOff", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("chOff")), T, T, T))),
      ("vAlign", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("vAlign")), T, T, T))),
      // Modified in DOM Level 2
      ("rowIndex",     PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("rowIndex")))), F, T, T))),
      ("sectionRowIndex",     PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("sectionRowIndex")))), F, T, T))))
      // 'cells' property in DOM Level 1 is updated in DOMHelper.modelNode
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }
   
  def getInsList(align: PropValue, bgColor: PropValue, ch: PropValue, chOff: PropValue, vAlign: PropValue,
                 rowIndex: PropValue, sectionRowIndex: PropValue, xpath: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("align", align),
    ("bgColor", bgColor),
    ("ch", ch),
    ("chOff", chOff),
    ("vAlign", vAlign),
    ("rowIndex", rowIndex),
    ("sectionRowIndex", sectionRowIndex),
    ("xpath", xpath)
  )
  
  override def default_getInsList(): List[(String, PropValue)] = {    
    val align = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val bgColor = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val ch = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val chOff = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val vAlign = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val rowIndex = PropValue(ObjectValue(NumTop, T, T, T))
    val sectionRowIndex = PropValue(ObjectValue(NumTop, T, T, T))
    val xpath = PropValue(ObjectValue(AbsString.alpha(""), F, F, F))
    // This object has all properties of the HTMLElement object 
    HTMLElement.default_getInsList ::: 
      getInsList(align, bgColor, ch, chOff, vAlign, rowIndex, sectionRowIndex, xpath)
  }


}
