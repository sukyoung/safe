/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models

import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse=>F, BoolTrue=>T}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMEvent._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml5._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMSvg._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMObject._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, IRFactory}
import kr.ac.kaist.jsaf.analysis.typing.models.jquery.JQueryHelper
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

object DOMModel {
  val async_calls : List[String] = List("#LOAD", "#UNLOAD", "#KEYBOARD", "#MOUSE", "#OTHER", "#READY", "#MESSAGE", "#TIME")
  val async_fun_names = List("__LOADEvent__", "__UNLOADEvent__", "__KEYBOARDEvent__", "__MOUSEEvent__", "__OTHEREvent__", "__READYEvent__", "__MESSAGEEvent__")
}

class DOMModel(cfg: CFG) extends Model(cfg) {
  /* DOM list */
  val list_dom = List[ModelData](
    // DOM Core
    DOMAttr, DOMCDATASection, DOMCharacterData, DOMComment, DOMConfiguration, DOMDocument,
    DOMDocumentFragment, DOMDocumentType, DOMElement, DOMEntity, DOMEntityReference, DOMError,
    DOMException, DOMImplementation, DOMImplementationList, DOMImplementationRegistry,
    DOMImplementationSource, DOMLocator, DOMNamedNodeMap, DOMNameList, DOMNode, DOMNodeList,
    DOMNotation, DOMProcessingInstruction, DOMStringList, DOMText, DOMTypeInfo, DOMUserDataHandler,
    // DOM Event
    DocumentEvent, Event, EventException, EventListener, EventTarget, MouseEvent, MutationEvent, UIEvent,
    KeyboardEvent, MessageEvent, TouchEvent,
    // DOM Html
    HTMLAnchorElement, HTMLAppletElement, HTMLAreaElement, HTMLBaseElement, HTMLBaseFontElement, HTMLBodyElement,
    HTMLBRElement, HTMLButtonElement, HTMLCollection, HTMLDirectoryElement, HTMLDivElement, HTMLDListElement,
    HTMLDocument, HTMLElement, HTMLFieldSetElement, HTMLFontElement, HTMLFormElement, HTMLFrameElement,
    HTMLFrameSetElement, HTMLHeadElement, HTMLHeadingElement, HTMLHRElement, HTMLHtmlElement, HTMLIFrameElement,
    HTMLImageElement, HTMLInputElement, HTMLIsIndexElement, HTMLLabelElement, HTMLLegendElement, HTMLLIElement,
    HTMLLinkElement, HTMLMapElement, HTMLMenuElement, HTMLMetaElement, HTMLModElement, HTMLObjectElement,
    HTMLOListElement, HTMLOptGroupElement, HTMLOptionsCollection, HTMLOptionElement, HTMLParagraphElement,
    HTMLParamElement, HTMLPreElement, HTMLQuoteElement, HTMLScriptElement, HTMLSelectElement, HTMLStyleElement,
    HTMLTableCaptionElement, HTMLTableCellElement, HTMLTableColElement, HTMLTableElement, HTMLTableRowElement,
    HTMLTableSectionElement, HTMLTextAreaElement, HTMLTitleElement, HTMLUListElement,
    DOMWindow,
    // HTML Top Element
    HTMLTopElement,
    // DOM Style,
    CSSStyleDeclaration, CSSStyleSheet, StyleSheetList, StyleSheet,
    // HTML 5
    HTMLCanvasElement, HTMLUnknownElement, CanvasRenderingContext2D, Navigator, CanvasGradient, DOMLocation,
    PluginArray, Plugin, History, MimeTypeArray, MimeType, Storage, HTMLDataListElement,
    // AJAX
    XMLHttpRequest,
    // W3C CSSOM View Module
    ClientRect, ClientRectList,
    // DOM SVG
    SVGElement, SVGSVGElement,
    // non-standard
    Console, Screen, HTMLAllCollection
  )

  private var map_fid = Map[FunctionId, String]()
  private var map_semantic = Map[String, SemanticFun]()
  private var map_presemantic =  Map[String, SemanticFun]()
  private var map_def =  Map[String, AccessFun]()
  private var map_use =  Map[String, AccessFun]()


  def initialize(h: Heap): Heap = {
    /* init function map */
    map_semantic = list_dom.foldLeft(map_semantic)((m, data) => m ++ data.getSemanticMap())
    map_presemantic = list_dom.foldLeft(map_presemantic)((m, data) => m ++ data.getPreSemanticMap())
    map_def = list_dom.foldLeft(map_def)((m, data) => m ++ data.getDefMap())
    map_use = list_dom.foldLeft(map_use)((m, data) => m ++ data.getUseMap())

    /* init api objects */
    val h_1 = list_dom.foldLeft(h)((h1, data) =>
      data.getInitList().foldLeft(h1)((h2, lp) => {
        /* List[(String, PropValue, Option[(Loc, Obj)], Option[FunctionId]
        *  property name : String
        *  property value : PropValue
        *  function loc and obj if function : Opt[(Loc, Obj)]
        *  funtion id if function : Opt[FunctionId]
        * */
        val list_props = lp._2.map((x) => prepareForUpdate("DOM", x._1, x._2))
        /* update api function map */
        list_props.foreach((v) =>
          v._4 match {
            case Some((fid, name)) => {map_fid = map_fid + (fid -> name)}
            case None => Unit
          })
        /* api object */
        val obj = h2.map.get(lp._1) match {
          case Some(o) =>
            list_props.foldLeft(o)((oo, pv) => oo.update(pv._1, pv._2))
          case None =>
            list_props.foldLeft(Obj.empty)((o, pvo) => o.update(pvo._1, pvo._2))
        }
        /* added function object to heap if any */
        val heap = list_props.foldLeft(h2)((h3, pvo) => pvo._3 match {
          case Some((l, o)) => Heap(h3.map.updated(l, o))
          case None => h3
        })

        /* added api obejct to heap */
        Heap(heap.map.updated(lp._1, obj))
      })
    )

    val _h =if(Shell.params.opt_Dommodel2) {
      Helper.PropStore(h_1, DOMNodeList.loc_ins2, NumStr, Value(HTMLTopElement.loc_ins_set))
    } else h_1


    // style object
    val StyleObj = Obj.empty.
      update(NumStr, PropValue(StrTop)).
      update(OtherStr, PropValue(StrTop)).
      update("@class", PropValue(AbsString.alpha("Object"))).
      update("@proto", PropValue(ObjectValue(Value(ObjProtoLoc), BoolFalse, BoolFalse, BoolFalse))).
      update("@extensible", PropValue(BoolTrue))

    /* initialize lookup table & event table */
    Heap(_h.map + (IdTableLoc -> Obj.empty) + (NameTableLoc -> Obj.empty) + (TagTableLoc -> Obj.empty) +
      (EventTargetTableLoc -> Obj.empty) + (EventFunctionTableLoc -> Obj.empty) +
      (ClassTableLoc -> Obj.empty) + (TempStyleLoc -> StyleObj) +
      (DOMEventTimeLoc -> Helper.NewDate(Value(UInt))))
  }

  def addAsyncCall(cfg: CFG, loop_head: Node): (List[Node],List[Node]) = {
    val fid_global = cfg.getGlobalFId
    /* dummy info for EventDispatch instruction */
    val dummy_info = IRFactory.makeInfo(IRFactory.dummySpan("DOMEvent"))
    /* dummy var for after call */
    val dummy_id = CFGTempId(NU.ignoreName+"#AsyncCall#", PureLocalVar)
    /* add async call */
    DOMModel.async_calls.foldLeft((List[Node](),List[Node]()))((nodes, ev) => {
      /* event call */
      val event_call = cfg.newBlock(fid_global)
      cfg.addInst(event_call,
        CFGAsyncCall(cfg.newInstId, dummy_info, "DOM", ev, newProgramAddr(), newProgramAddr(), newProgramAddr()))
      /* event after call */
      val event_after = cfg.newAfterCallBlock(fid_global, dummy_id)
      val event_catch = cfg.newAfterCatchBlock(fid_global)
      cfg.addEdge(loop_head, event_call)
      cfg.addCall(event_call, event_after, event_catch)
      cfg.addEdge(event_after, loop_head)
      (event_after::nodes._1,event_catch::nodes._2)
    })
  }

  def isModelFid(fid: FunctionId) = map_fid.contains(fid)
  def getFIdMap(): Map[FunctionId, String] = map_fid
  def getSemanticMap(): Map[String, SemanticFun] = map_semantic
  def getPreSemanticMap(): Map[String, SemanticFun] = map_presemantic
  def getDefMap(): Map[String, AccessFun] = map_def
  def getUseMap(): Map[String, AccessFun] = map_use

  def asyncSemantic(sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG,
                    name: String, list_addr: List[Address]): ((Heap, Context), (Heap, Context)) = {
    val addr1 = list_addr(0)
    val addr2 = list_addr(1)
    val addr3 = list_addr(2)
    val all_event = DOMModel.async_calls
    val all_event_name = DOMModel.async_fun_names
    val fun_table = h(EventFunctionTableLoc)
    val target_table = h(EventTargetTableLoc)
    // lset_fun: function to dispatch
    // lset_target: current target, 'this' in function body
    val (lset_fun, lset_target) = name match {

      case "#ALL" =>
        val (f, t) = all_event.foldLeft((LocSetBot, LocSetBot))((llset, e) =>
          (llset._1 ++ fun_table(e)._2._2, llset._2 ++ target_table(e)._2._2)
        )
        val global_o = h(GlobalLoc)
        val lset_static = global_o.getProps.foldLeft(LocSetBot)((lset, kv) =>
          if (all_event_name.exists((e) => kv.startsWith(e)))
            lset ++ global_o(kv)._1._1._2
          else
            lset
        )
        (f ++ lset_static, t)
      case "#NOT_LOAD_UNLOAD" =>
        val (f, t) = all_event.filterNot(_ == "#LOAD").filterNot(_ == "#UNLOAD").foldLeft((LocSetBot, LocSetBot))((llset, e) =>
          (llset._1 ++ fun_table(e)._2._2, llset._2 ++ target_table(e)._2._2)
        )
        val event_names = all_event_name.filterNot(_ == "__LOADEvent__").filterNot(_ == "__UNLOADEvent__")
        val global_o = h(GlobalLoc)
        val lset_static = global_o.getProps.foldLeft(LocSetBot)((lset, kv) =>
          if (event_names.exists((e) => kv.startsWith(e)))
            lset ++ global_o(kv)._1._1._2
          else
            lset
        )
        (f ++ lset_static, t)

      case _ =>
        val (f,t) =
          if (Config.jqMode) {
            // delegate
            val selector_table = h(EventSelectorTableLoc)
            val s_selector = selector_table(name)._2._1._5
            val lset_target = DOMHelper.querySelectorAll(h, s_selector) ++ target_table(name)._2._2
            (fun_table(name)._2._2 , lset_target)
          }
          else
            (fun_table(name)._2._2 , target_table(name)._2._2)

        val lset_static =
          if (name == "#TIME")
            LocSetBot
          else {
            val event_name = all_event_name(all_event.indexOf(name))
            h(GlobalLoc).getProps.foldLeft(LocSetBot)((lset, kv) =>
              if (kv.startsWith(event_name)) lset ++ h(GlobalLoc)(kv)._1._1._2
              else lset
            )
          }
        val (f_onload, t_onload) =
          if (name == "#LOAD") {
            val lset_window = Helper.Proto(h, GlobalLoc, AbsString.alpha("window"))._2
            val lset_onload = lset_window.foldLeft(LocSetBot)((lset, l) =>
              lset ++ Helper.Proto(h, l, AbsString.alpha("onload"))._2
            )
            (lset_onload, lset_window)
          }
          else
            (LocSetBot, LocSetBot)
        (f ++ lset_static ++ f_onload, t ++ t_onload)
    }
    // event call
    val l_r = addrToLoc(addr1, Recent)
    val l_arg = addrToLoc(addr2, Recent)
    val l_event = addrToLoc(addr3, Recent)
    val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
    val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
    val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)

    // 'this' = current target element
    val lset_this =  Helper.getThis(h_3, Value(lset_target))

    // need arguments obejct, arguments[0] = 'event object'
    val o_event = name match {
      case "#ALL" =>
        // Event object
        val proplist = MouseEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3)) ++ KeyboardEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3)) ++ 
                       MessageEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3)) ++ TouchEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3))
        proplist.foldLeft(Helper.NewObject(LocSet(MouseEvent.loc_proto) + KeyboardEvent.loc_proto + MessageEvent.loc_proto + TouchEvent.loc_proto))((o, pv) =>
          o.update(pv._1, pv._2)
        )
      case "#MOUSE" =>
        // MouseEvent object
        val proplist = MouseEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3))
        proplist.foldLeft(Helper.NewObject(MouseEvent.loc_proto))((o, pv) =>
          o.update(pv._1, pv._2)
        )
      case "#KEYBOARD" =>
        // KeyboardEvent object
        val proplist = KeyboardEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3))
        proplist.foldLeft(Helper.NewObject(KeyboardEvent.loc_proto))((o, pv) =>
          o.update(pv._1, pv._2)
        )
      case "#MESSAGE" =>
        // MessageEvent object
        val proplist = MessageEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3))
        proplist.foldLeft(Helper.NewObject(MessageEvent.loc_proto))((o, pv) =>
          o.update(pv._1, pv._2)
        )

      case "#TOUCH" =>
        // TouchEvent object
        val proplist = TouchEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3))
        proplist.foldLeft(Helper.NewObject(TouchEvent.loc_proto))((o, pv) =>
          o.update(pv._1, pv._2)
        )

      case _ =>
        // Event object
        Event.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3)).foldLeft(Helper.NewObject(Event.loc_proto))((o, pv) =>
          o.update(pv._1, pv._2)
        )
    }


    val h_4 = h_3.update(l_event, o_event).
      update(l_arg, Helper.NewArrayObject(AbsNumber.alpha(1)))
    val h_5 = Helper.PropStore(h_4, l_arg, AbsString.alpha("0"), Value(LocSet(l_event)))
    val v_arg = Value(LocSet(l_arg))
    val o_old = h_5(SinglePureLocalLoc)
    val cc_caller = cp._2
    val n_aftercall = cfg.getAftercallFromCall(cp._1)
    val cp_aftercall = (n_aftercall, cc_caller)
    val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
    val cp_aftercatch = (n_aftercatch, cc_caller)
    lset_fun.foreach {l_f:Loc => {
      val o_f = h_5(l_f)
      val fids = o_f("@function")._3
      fids.foreach {fid => {
        val ccset = cc_caller.NewCallContext(h, cfg, fid, l_r, lset_this)
        ccset.foreach {case (cc_new, o_new) => {
          val value = PropValue(ObjectValue(v_arg, BoolTrue, BoolFalse, BoolFalse))
          val o_new2 =
            o_new.
              update(cfg.getArgumentsName(fid), value).
              update("@scope", o_f("@scope"))
          sem.addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
          sem.addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_3, o_old)
          sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_3, o_old)
        }}
      }}
    }}
    val h_6 = v_arg._2.foldLeft(HeapBot)((hh, l) => {
      val pv = PropValue(ObjectValue(Value(lset_fun), BoolTrue, BoolFalse, BoolTrue))
      hh + h_5.update(l, h_5(l).update("callee", pv))
    })
    ((h_6, ctx_3), (he, ctxe))
  }
  def asyncPreSemantic(sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG,
                       name: String, list_addr: List[Address]): (Heap, Context) = {
    val addr1 = list_addr(0)
    val addr2 = list_addr(1)
    val addr3 = list_addr(2)
    val all_event = DOMModel.async_calls
    val all_event_name = DOMModel.async_fun_names
    val fun_table = h(EventFunctionTableLoc)
    val target_table = h(EventTargetTableLoc)

    // lset_fun: function to dispatch
    // lset_target: current target, 'this' in function body
    val (lset_fun, lset_target) = name match {

      case "#ALL" =>
        val (f, t) = all_event.foldLeft((LocSetBot, LocSetBot))((llset, e) =>
          (llset._1 ++ fun_table(e)._2._2, llset._2 ++ target_table(e)._2._2)
        )
        val lset_static = h(GlobalLoc).getProps.foldLeft(LocSetBot)((lset, kv) =>
          if (all_event_name.exists((e) => kv.startsWith(e)))
            lset ++ h(GlobalLoc)(kv)._1._1._2
          else
            lset
        )
        (f ++ lset_static, t)
      case "#NOT_LOAD_UNLOAD" =>
        val (f, t) = all_event.filterNot(_ == "#LOAD").filterNot(_ == "#UNLOAD").foldLeft((LocSetBot, LocSetBot))((llset, e) =>
          (llset._1 ++ fun_table(e)._2._2, llset._2 ++ target_table(e)._2._2)
        )
        val event_names = all_event_name.filterNot(_ == "__LOADEvent__").filterNot(_ == "__UNLOADEvent__")
        val lset_static = h(GlobalLoc).getProps.foldLeft(LocSetBot)((lset, kv) =>
          if (event_names.exists((e) => kv.startsWith(e)))
            lset ++ h(GlobalLoc)(kv)._1._1._2
          else
            lset
        )
        (f ++ lset_static, t)

      case _ =>
        val (f,t) = (fun_table(name)._2._2 , target_table(name)._2._2)
        val lset_static =
          if (name == "#TIME")
            LocSetBot
          else {
            val event_name = all_event_name(all_event.indexOf(name))
            h(GlobalLoc).getProps.foldLeft(LocSetBot)((lset, kv) =>
              if (kv.startsWith(event_name)) lset ++ h(GlobalLoc)(kv)._1._1._2
              else lset
            )
          }
        (f ++ lset_static, t)
    }
    // event call
    val l_r = addrToLoc(addr1, Recent)
    val l_arg = addrToLoc(addr2, Recent)
    val l_event = addrToLoc(addr3, Recent)
    val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)
    val (h_2, ctx_2) = PreHelper.Oldify(h_1, ctx_1, addr2)
    val (h_3, ctx_3) = PreHelper.Oldify(h_2, ctx_2, addr3)

    // 'this' = current target element
    val lset_this =  PreHelper.getThis(h_3, Value(lset_target))

    // need arguments obejct, arguments[0] = 'event object'
    val o_event = name match {
      case "#ALL" =>
        // Event object
        val proplist = MouseEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3)) ++ KeyboardEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3)) ++ 
                       MessageEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3))
        proplist.foldLeft(PreHelper.NewObject(ObjProtoLoc))((o, pv) =>
          o.update(pv._1, pv._2)
        )
      case "#MOUSE" =>
        // MouseEvent object
        val proplist = MouseEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3))
        proplist.foldLeft(PreHelper.NewObject(ObjProtoLoc))((o, pv) =>
          o.update(pv._1, pv._2)
        )
      case "#KEYBOARD" =>
        // KeyboardEvent object
        val proplist = KeyboardEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3))
        proplist.foldLeft(PreHelper.NewObject(ObjProtoLoc))((o, pv) =>
          o.update(pv._1, pv._2)
        )
      case "#TOUCH" =>
        // KeyboardEvent object
        val proplist = TouchEvent.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3))
        proplist.foldLeft(PreHelper.NewObject(ObjProtoLoc))((o, pv) =>
          o.update(pv._1, pv._2)
        )

      case _ =>
        // Event object
        Event.getInstList(lset_target, HTMLTopElement.getInsLoc(h_3)).foldLeft(PreHelper.NewObject(ObjProtoLoc))((o, pv) =>
          o.update(pv._1, pv._2)
        )
    }

    val h_4 = h_3.update(l_event, o_event).
      update(l_arg, PreHelper.NewArrayObject(AbsNumber.alpha(1)))
    val h_5 = PreHelper.PropStore(h_4, l_arg, AbsString.alpha("0"), Value(LocSet(l_event)))
    val v_arg = Value(LocSet(l_arg))
    val o_old = h_5(SinglePureLocalLoc)
    val cc_caller = cp._2
    val n_aftercall = cfg.getAftercallFromCall(cp._1)
    val cp_aftercall = (n_aftercall, cc_caller)
    val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
    val cp_aftercatch = (n_aftercatch, cc_caller)
    lset_fun.foreach {l_f:Loc => {
      val o_f = h_5(l_f)
      val fids = o_f("@function")._3
      fids.foreach {fid => {
        val ccset = cc_caller.NewCallContext(h, cfg, fid, l_r, lset_this)
        ccset.foreach {case (cc_new, o_new) => {
          val value = PropValue(ObjectValue(v_arg, BoolTrue, BoolFalse, BoolFalse))
          val o_new2 =
            o_new.
              update(cfg.getArgumentsName(fid), value).
              update("@scope", o_f("@scope"))
          sem.addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
          sem.addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_3, o_old)
          sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_3, o_old)
        }}
      }}
    }}
    val h_6 = v_arg._2.foldLeft(HeapBot)((hh, l) => {
      val pv = PropValue(ObjectValue(Value(lset_fun), BoolTrue, BoolFalse, BoolTrue))
      hh + h_5.update(l, h_5(l).update("callee", pv))
    })
    (h_6, ctx_3)

  }
  def asyncDef(h: Heap, ctx: Context, cfg: CFG, name: String, list_addr: List[Address]): LPSet = {
    val addr1 = list_addr(0)
    val addr2 = list_addr(1)
    val addr3 = list_addr(2)
    val l_arg = addrToLoc(addr2, Recent)
    val l_event = addrToLoc(addr3, Recent)
    val lpset_1 = AH.Oldify_def(h, ctx, addr1)
    val lpset_2 = AH.Oldify_def(h, ctx, addr2)
    val lpset_3 = AH.Oldify_def(h, ctx, addr3)
    // event object
    val lpset_4 = (AH.NewObject_def ++ MouseEvent.instProps ++ KeyboardEvent.instProps ++ TouchEvent.instProps).foldLeft(LPBot)((lpset, p) => lpset + ((l_event, p)))
    // arguments object
    val lpset_5 = AH.NewArrayObject_def.foldLeft(LPBot)((lpset, p) => lpset + ((l_arg, p)))
    // arguments[0] = event
    val lpset_6 = AH.PropStore_def(h, l_arg, AbsString.alpha("0"))
    // callee
    val lpset_7 = LPSet((l_arg, "callee"))
    lpset_1 ++ lpset_2 ++ lpset_3 ++ lpset_4 ++ lpset_5 ++ lpset_6 ++ lpset_7
  }
  def asyncUse(h: Heap, ctx: Context, cfg: CFG, name: String, list_addr: List[Address]): LPSet = {
    val addr1 = list_addr(0)
    val addr2 = list_addr(1)
    val addr3 = list_addr(2)
    val all_event = DOMModel.async_calls
    val all_event_name = DOMModel.async_fun_names
    val fun_table = h(EventFunctionTableLoc)
    val target_table = h(EventTargetTableLoc)

    // lset_fun: function to dispatch
    // lset_target: current target, 'this' in function body
    val (lset_fun, lset_target, lpset_1) = name match {

      case "#ALL" =>
        val (f, t, lpset_1) = all_event.foldLeft((LocSetBot, LocSetBot, LPBot))((llpset, e) =>
          (llpset._1 ++ fun_table(e)._2._2, llpset._2 ++ target_table(e)._2._2,
            llpset._3 + (EventFunctionTableLoc, e) + (EventTargetTableLoc, e))
        )
        val (lset_static, lpset_2) = h(GlobalLoc).getProps.foldLeft((LocSetBot, LPBot))((set, kv) =>
          if (all_event_name.exists((e) => kv.startsWith(e)))
            (set._1 ++ h(GlobalLoc)(kv)._1._1._2, set._2 + (GlobalLoc, kv))
          else
            set
        )
        (f ++ lset_static, t, lpset_1 ++ lpset_2)
      case "#NOT_LOAD_UNLOAD" =>
        val (f, t, lpset_1) = all_event.filterNot(_ == "#LOAD").filterNot(_ == "#UNLOAD").foldLeft((LocSetBot, LocSetBot, LPBot))((llpset, e) =>
          (llpset._1 ++ fun_table(e)._2._2, llpset._2 ++ target_table(e)._2._2,
            llpset._3 + (EventFunctionTableLoc, e) + (EventTargetTableLoc, e))
        )
        val event_names = all_event_name.filterNot(_ == "__LOADEvent__").filterNot(_ == "__UNLOADEvent__")
        val (lset_static, lpset_2) = h(GlobalLoc).getProps.foldLeft((LocSetBot, LPBot))((set, kv) =>
          if (event_names.exists((e) => kv.startsWith(e)))
            (set._1 ++ h(GlobalLoc)(kv)._1._1._2, set._2 + (GlobalLoc, kv))
          else
            set
        )
        (f ++ lset_static, t, lpset_1 ++ lpset_2)

      case _ =>
        val (f,t) = (fun_table(name)._2._2 , target_table(name)._2._2)
        val lpset_1 =  LPBot + (EventFunctionTableLoc, name) + (EventTargetTableLoc, name)
        val (lset_static, lpset_2) =
          if (name == "#TIME")
            (LocSetBot, LPBot)
          else {
            val event_name = all_event_name(all_event.indexOf(name))
            h(GlobalLoc).getProps.foldLeft((LocSetBot, LPBot))((set, kv) =>
              if (kv.startsWith(event_name))
                (set._1 ++ h(GlobalLoc)(kv)._1._1._2, set._2 + (GlobalLoc, kv))
              else
                set
            )
          }
        (f ++ lset_static, t, lpset_1 ++ lpset_2)
    }

    val l_arg = addrToLoc(addr2, Recent)
    val l_event = addrToLoc(addr3, Recent)
    val LP_2 = AH.Oldify_use(h, ctx, addr1)
    val LP_3 = AH.Oldify_use(h, ctx, addr2)
    val LP_4 = AH.Oldify_use(h, ctx, addr3)

    // this
    val LP_5 = AH.getThis_use(h, Value(lset_target))

    // event object
    val LP_6 = (AH.NewObject_def ++ MouseEvent.instProps ++ KeyboardEvent.instProps ++ TouchEvent.instProps).foldLeft(LPBot)((lpset, p) => lpset + ((l_event, p)))
    // arguments object
    val LP_7 = AH.NewArrayObject_def.foldLeft(LPBot)((lpset, p) => lpset + ((l_arg, p)))
    // arguments[0] = event
    val LP_8 = AH.PropStore_def(h, l_arg, AbsString.alpha("0"))

    // function
    val LP_9 = lset_fun.foldLeft(LPBot)((S, l_f) => S + ((l_f, "@function")))
    // callee
    val LP_10 = LPSet((l_arg, "callee"))
    // because of PureLocal object is weak updated in edges, all the element are needed
    val LP_11 = h(SinglePureLocalLoc).getProps.foldLeft(LPBot)((S, kv) => S + ((SinglePureLocalLoc, kv)))
    val LP_12 = LPSet(Set((ContextLoc, "3"), (ContextLoc, "4")))
    lpset_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LP_6 ++ LP_7 ++ LP_8 ++ LP_9 ++ LP_10 ++ LP_11 ++ LP_12
  }

  def asyncCallgraph(h: Heap, inst: CFGInst, map: Map[CFGInst, Set[FunctionId]],
                     name: String, list_addr: List[Address]): Map[CFGInst, Set[FunctionId]] = {
    val all_event = DOMModel.async_calls //List("#LOAD", "#UNLOAD", "#KEYBOARD", "#MOUSE", "#OTHER", "#TIME")
    val all_event_name = DOMModel.async_fun_names
    val fun_table = h(EventFunctionTableLoc)
    val target_table = h(EventTargetTableLoc)

    // lset_fun: function to dispatch
    val lset_fun = name match {
      case "#ALL" =>
        val f = all_event.foldLeft(LocSetBot)((llset, e) =>
          llset ++ fun_table(e)._2._2
        )
        val lset_static = h(GlobalLoc).getProps.foldLeft(LocSetBot)((lset, kv) =>
          if (all_event_name.exists((e) => kv.startsWith(e)))
            lset ++ h(GlobalLoc)(kv)._1._1._2
          else
            lset
        )
        f ++ lset_static
      case "#NOT_LOAD_UNLOAD" =>
        val f = all_event.filterNot(_ == "#LOAD").filterNot(_ == "#UNLOAD").foldLeft(LocSetBot)((llset, e) =>
          llset ++ fun_table(e)._2._2
        )
        val event_names = all_event_name.filterNot(_ == "__LOADEvent__").filterNot(_ == "__UNLOADEvent__")
        val lset_static = h(GlobalLoc).getProps.foldLeft(LocSetBot)((lset, kv) =>
          if (event_names.exists((e) => kv.startsWith(e)))
            lset ++ h(GlobalLoc)(kv)._1._1._2
          else
            lset
        )
        f ++ lset_static
      case _ =>
        val f = fun_table(name)._2._2
        val lset_static =
          if (name == "#TIME")
            LocSetBot
          else {
            val event_name = all_event_name(all_event.indexOf(name))
            h(GlobalLoc).getProps.foldLeft(LocSetBot)((lset, kv) =>
              if (kv.startsWith(event_name)) lset ++ h(GlobalLoc)(kv)._1._1._2
              else lset
            )
          }
        f ++ lset_static
    }
    lset_fun.foldLeft(map)((_m, l) => {
      if (BoolTrue <= PreHelper.IsCallable(h,l)) {
        _m.get(inst) match {
          case None => _m + (inst -> h(l)("@function")._3.toSet)
          case Some(set) => _m + (inst -> (set ++ h(l)("@function")._3.toSet))
        }
      } else {
        _m
      }
    })
  }
}
