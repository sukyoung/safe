/*******************************************************************************
    Copyright (c) 2013-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml

import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import org.w3c.dom.Node
import org.w3c.dom.Element
import kr.ac.kaist.jsaf.analysis.typing.Helper
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.{DOMElement, DOMNodeList, DOMNamedNodeMap}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMObject.CSSStyleDeclaration
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

object HTMLImageElement extends DOM {
  private val name = "HTMLImageElement"

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
    // HTML 5 HTMLElement.Image() constructor
    ("@construct", AbsInternalFunc("HTMLImageElement.Image")),
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
      ("lowSrc", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("name", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("align", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("alt", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("border", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("isMap", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("longDesc", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("src", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("useMap", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("height", AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
      ("hspace", AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
      ("vspace", AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
      ("width", AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
      ("naturalWidth", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T)))),
      ("naturalHeight", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T))))
    )
  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(HTMLElement.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
      (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T)))),
      // HTML 5 HTMLElement.Image() constructor
      ("Image", AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
    )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = if(Shell.params.opt_Dommodel2) List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global), (loc_ins, prop_ins)

  ) else List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)  ) 

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      // HTML 5 HTMLElement.Image() constructor
      // WHATWG HTML Living Standard - Section 4.8.1 The Img Element
      // http://www.whatwg.org/specs/web-apps/current-work/multipage/embedded-content-1.html#dom-image
      ("HTMLImageElement.Image" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          if(Shell.params.opt_Dommodel2) {
            ((Helper.ReturnStore(h, Value(loc_ins)), ctx), (he, ctxe)) 
          }
          else {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)

          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          
          // locations for 'childNodes', 'attributes', and 'style' property of a new created element
          val l_childNodes = addrToLoc(addr1, Recent)
          val l_attributes = addrToLoc(addr2, Recent)
          val l_style = addrToLoc(addr3, Recent)
          val h1 = HTMLTopElement.setInsLoc(h, lset_this)
          val (h_1, ctx_1) = Helper.Oldify(h1, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          
          /* arguments */
          // argument length
          val arglen = Operator.ToUInt32(getArgValue(h_3, ctx_3, args, "length"))
          // optional arguments for width and height
          val (width, height) = arglen.getAbsCase match {
            case AbsBot => (NumBot, NumBot)
            case _ => AbsNumber.getUIntSingle(arglen) match {
              // no arguments 
              case Some(n) if n == 0 =>
                (AbsNumber.alpha(0), AbsNumber.alpha(0))
              // one argument for width 
              case Some(n) if n ==1 =>
                (Helper.toNumber(Helper.toPrimitive_better(h_3, getArgValue(h_3, ctx_3, args, "0"))), AbsNumber.alpha(0))
              // two arguments for width and height 
              case Some(n) if n > 1 =>
                (Helper.toNumber(Helper.toPrimitive_better(h_3, getArgValue(h_3, ctx_3, args, "0"))), 
                 Helper.toNumber(Helper.toPrimitive_better(h_3, getArgValue(h_3, ctx_3, args, "1"))))
              case _ => (NumTop, NumTop)
            }
          }

          // create a new HTMLImageElement
          if(width </ NumBot && height </ NumBot) {
            val h_4 = lset_this.foldLeft(h_3)((_h, l) => {
              val newimgobj_list = default_getInsList:::DOMElement.getInsList(PropValue(ObjectValue(AbsString.alpha("IMG"), F, T, T)))
              val newimgobj = newimgobj_list.foldLeft(Obj.empty)((obj, prop) =>
                if(prop._1=="width") 
                  obj.update("width", PropValue(ObjectValue(width, T, T, T)))
                else if(prop._1=="height")
                  obj.update("height", PropValue(ObjectValue(height, T, T, T)))
                else
                  obj.update(prop._1, prop._2)
              )
              // 'childNodes' update
              val childNodes_list = DOMNodeList.getInsList(0)
              val childNodes = childNodes_list.foldLeft(Obj.empty)((x, y) => x.update(y._1, y._2))
              // 'attibutes' update
              val attributes_list = DOMNamedNodeMap.getInsList(0)
              val attributes = attributes_list.foldLeft(Obj.empty)((x, y) => x.update(y._1, y._2))
              // 'style' update
              val style_list = CSSStyleDeclaration.getInsList()
              val style = style_list.foldLeft(Obj.empty)((x, y) => x.update(y._1, y._2))
              val newimgobj_up = newimgobj.update("childNodes", PropValue(ObjectValue(l_childNodes, F, T, T))).update(
                                                  "attributes", PropValue(ObjectValue(l_attributes, F, T, T))).update(
                                                  "style", PropValue(ObjectValue(l_style, T, T, T)))

              _h.update(l_childNodes, childNodes).update(l_attributes, attributes).update(l_style, style).update(l, newimgobj_up)
            })
            ((Helper.ReturnStore(h_4, Value(lset_this)), ctx_3), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
         }
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map()
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map()
  }

  /* semantics */
  // no function
     
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
      ("name", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("name")), T, T, T))),
      ("align", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("align")), T, T, T))),
      ("alt", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("alt")), T, T, T))),
      ("border", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("border")), T, T, T))),
      ("isMap",   PropValue(ObjectValue((if(e.getAttribute("isMap")=="true") T else F), T, T, T))),
      ("longDesc", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("longDesc")), T, T, T))),
      ("src", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("src")), T, T, T))),
      ("useMap", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("useMap")), T, T, T))),
      // Modified in DOM Level 2
      ("height",     PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("height")))), T, T, T))),
      ("hspace",     PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("hspace")))), T, T, T))),
      ("vspace",     PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("vspace")))), T, T, T))),
      ("width",     PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("width")))), T, T, T))))
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }
   
  def getInsList(name: PropValue, align: PropValue, alt: PropValue, border: PropValue, isMap: PropValue, 
                 longDesc: PropValue, src: PropValue, useMap: PropValue, height: PropValue, hspace: PropValue, 
                 vspace: PropValue, width: PropValue, xpath: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("name", name),
    ("align", align),
    ("alt", alt),
    ("border", border),
    ("isMap", isMap),
    ("longDesc", longDesc),
    ("src", src),
    ("useMap", useMap),
    // Modified in DOM Level 2
    ("height", height),
    ("hspace", hspace),
    ("vspace", vspace),
    ("width",  width),
    ("xpath", xpath)
  )
  
  override def default_getInsList(): List[(String, PropValue)] = {    
    val name = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val align = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val alt = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val border = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val isMap = PropValue(ObjectValue(BoolFalse, T, T, T))
    val longDesc = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val src = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val useMap = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val height = PropValue(ObjectValue(AbsNumber.alpha(0), T, T, T))
    val hspace = PropValue(ObjectValue(NumTop, T, T, T))
    val vspace = PropValue(ObjectValue(NumTop, T, T, T))
    val width = PropValue(ObjectValue(AbsNumber.alpha(0), T, T, T))
    val xpath = PropValue(ObjectValue(AbsString.alpha(""), F, F, F))
    // This object has all properties of the HTMLElement object 
    HTMLElement.default_getInsList ::: 
      getInsList(name, align, alt, border, isMap, longDesc, src, useMap, height, hspace, vspace, width, xpath)
  }

}
