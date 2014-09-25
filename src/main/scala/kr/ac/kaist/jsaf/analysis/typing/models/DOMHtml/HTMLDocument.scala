/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.ControlPoint
import org.w3c.dom.Node
import org.w3c.dom.Element
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.{DOMDocument, DOMNodeList}
import org.w3c.dom.html.{HTMLDocument => HTMLDoc}
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc

import kr.ac.kaist.jsaf.Shell

object HTMLDocument extends DOM {
  private val name = "HTMLDocument"

  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_ins = newSystemRecentLoc(name + "Ins")
  val loc_ins2 = newSystemRecentLoc(name + "2Ins")
  val GlobalDocumentLoc = if(Shell.params.opt_Dommodel2) loc_ins
                          else newSystemRecentLoc(name + "Global")

  /* constructor */
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )
  
  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(DOMDocument.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("open",              AbsBuiltinFunc("HTMLDocument.open", 0)),
    ("close",             AbsBuiltinFunc("HTMLDocument.close", 0)),
    ("write",             AbsBuiltinFunc("HTMLDocument.write", 1)),
    ("writeln",           AbsBuiltinFunc("HTMLDocument.writeln", 1)),
    ("getElementsByName", AbsBuiltinFunc("HTMLDocument.getElementsByName", 1))
  )
 
  /* instance */
  private val prop_ins: List[(String, AbsProperty)] = 
       DOMDocument.getInsList2() ++ List(
      ("@class",    AbsConstValue(PropValue(AbsString.alpha("Object")))),
      ("@proto",    AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
      ("@extensible", AbsConstValue(PropValue(BoolTrue))),
      // DOM Level 1
      ("title", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("referer", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("domain", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("images", AbsConstValue(PropValue(ObjectValue(Value(HTMLCollection.loc_ins), F, T, T)))),
      ("applets", AbsConstValue(PropValue(ObjectValue(Value(HTMLCollection.loc_ins), F, T, T)))),
      ("links", AbsConstValue(PropValue(ObjectValue(Value(HTMLCollection.loc_ins), F, T, T)))),
      ("forms", AbsConstValue(PropValue(ObjectValue(Value(HTMLCollection.loc_ins), F, T, T)))),
      ("anchors", AbsConstValue(PropValue(ObjectValue(Value(HTMLCollection.loc_ins), F, T, T)))),
      ("documentElement", AbsConstValue(PropValue(ObjectValue(Value(HTMLHtmlElement.loc_ins), F, T, T)))),
      ("body", AbsConstValue(PropValue(ObjectValue(Value(HTMLBodyElement.loc_ins), F, T, T)))),
      ("cookie", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("width", AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
      ("height", AbsConstValue(PropValue(ObjectValue(UInt, T, T, T))))
    )
  /* list of instance properties */
  private val prop_ins2: List[(String, AbsProperty)] = 
    List(
      ("@class",    AbsConstValue(PropValue(AbsString.alpha("Object")))),
      ("@proto",    AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
      ("@extensible", AbsConstValue(PropValue(BoolTrue)))
    )
  

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = if(Shell.params.opt_Dommodel2) List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global), (loc_ins, prop_ins), (loc_ins2, prop_ins2)


  ) else List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)  ) 
   
  def  getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLDocument.open" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.close" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.write" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.writeln" => ((h,ctx),(he,ctxe))
      "HTMLDocument.getElementsByName" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* arguments */
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_name </ StrBot) {
 
            if(Shell.params.opt_Dommodel2){
              val lset_env = h(SinglePureLocalLoc)("@env")._2._2
              val l_r = addrToLoc(addr1, Recent)
              val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
              val lset = Helper.Proto(h, NameTableLoc, s_name)._2
              val proplist = HTMLCollection.getInsList(0) 
              val obj = proplist.foldLeft(Obj.empty)((o, p) => o.update(p._1, p._2))
              val new_obj = if(lset.size > 0) 
                                    obj.update("length", PropValue(ObjectValue(Value(UInt), F, T, T))).update(
                                                NumStr, PropValue(ObjectValue(Value(lset), T, T, T)))
                            else obj
              val h_2 = h_1.update(l_r, new_obj)
              ((Helper.ReturnStore(h_2,  Value(l_r)), ctx_1), (he, ctxe))
            }
            else {
              val obj_table = h(NameTableLoc)
              val propv_element = obj_table(s_name)
              val abs_element = obj_table.domIn(s_name)
              val (h_1, ctx_1, v_empty) =
                if (BoolFalse <= abs_element) {
                  val l_r = addrToLoc(addr1, Recent)
                  val (_h, _ctx) = Helper.Oldify(h, ctx, addr1)
                  /* empty NodeList */
                  val o_empty = DOMNodeList.getInsList(0).foldLeft(Obj.empty)((o, pv) =>
                    o.update(pv._1, pv._2))
                  val _h1 = _h.update(l_r, o_empty)
                  (_h1, _ctx, Value(l_r))
                } else (h, ctx, ValueBot)
              /* imprecise semantic */
              ((Helper.ReturnStore(h_1, propv_element._1._1 + v_empty), ctx_1), (he, ctxe))
            }
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLDocument.open" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.close" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.write" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.writeln" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.getElementsByName" => ((h,ctx),(he,ctxe))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLDocument.open" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.close" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.write" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.writeln" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.getElementsByName" => ((h,ctx),(he,ctxe))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLDocument.open" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.close" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.write" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.writeln" => ((h,ctx),(he,ctxe))
      //case "HTMLDocument.getElementsByName" => ((h,ctx),(he,ctxe))
    )
  }

  /* instance */
  // only one 'document' can be present in the heap
  var loc_ins_status: Option[Loc]  = None
  override def getInstance(cfg: CFG): Option[Loc] = { 
    val loc_ins = GlobalDocumentLoc //addrToLoc(cfg.newProgramAddr, Recent)
    loc_ins_status=Some(loc_ins)
    loc_ins_status
  }
  def getInstance(): Option[Loc] = loc_ins_status
 
  
  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case d: HTMLDoc =>
      val referrer = d.getReferrer
      val domain = d.getDomain
      val URL = d.getURL
      // This instance object has all properties of the Document object
      DOMDocument.getInsList(node) ++ List(
      ("@class",    PropValue(AbsString.alpha("Object"))),
      ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
      ("@extensible", PropValue(BoolTrue)),
      // DOM Level 1
      ("title",   PropValue(ObjectValue(StrTop, T, T, T))),
      ("referrer",   PropValue(ObjectValue(AbsString.alpha(if(referrer!=null) referrer else ""), F, T, T))),
      ("domain",   PropValue(ObjectValue(AbsString.alpha(if(domain!=null) domain else ""), F, T, T))),
      ("URL",   PropValue(ObjectValue(AbsString.alpha(if(URL!=null) URL else ""), F, T, T))),
      ("cookie",   PropValue(ObjectValue(AbsString.alpha(""), T, T, T))),
      ("body",   PropValue(ObjectValue(Value(NullTop), T, T, T))),
      // 'compatMode' in WHATWG DOM Living Standard 
      ("compatMode",   PropValue(ObjectValue(OtherStr, T, T, T)))
      // 'all', 'forms' , 'images', 'body'  property is updated in DOMBuilder 
     )
      // TODO: 'applets', 'links', 'anchors' in DOM Level 1
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot be an instance of HTMLDocument.")
      List()
    }
  }

  /* list of instance properties */
  def getInsList(): List[(String, PropValue)] = {
    List(
      ("@class",    PropValue(AbsString.alpha("Object"))),
      ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
      ("@extensible", PropValue(BoolTrue))
    )
  }

}
