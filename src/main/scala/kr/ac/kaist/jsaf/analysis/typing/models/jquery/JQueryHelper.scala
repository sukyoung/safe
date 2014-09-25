/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.models.jquery

import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolTrue => T, BoolFalse => F, _}
import kr.ac.kaist.jsaf.analysis.typing.Helper
import kr.ac.kaist.jsaf.analysis.typing.models.{DOMHelper, JQueryModel}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.{HTMLDocument, HTMLTopElement}

object JQueryHelper {

  def NewJQueryObject(): Obj =
    Helper.NewObject(JQuery.ProtoLoc)
      .update("length", PropValue(ObjectValue(AbsNumber.alpha(0), T, T, T)))

  def NewJQueryObject(len : Double): Obj =
    Helper.NewObject(JQuery.ProtoLoc)
      .update("length", PropValue(ObjectValue(AbsNumber.alpha(len), T, T, T)))

  def NewJQueryObject(n_len : AbsNumber): Obj =
    Helper.NewObject(JQuery.ProtoLoc)
      .update("length", PropValue(ObjectValue(n_len, T, T, T)))

  def MakeArray(h: Heap, arr: Value): Obj = {
    MakeArray(h, arr, Helper.NewArrayObject(AbsNumber.alpha(0)))
  }

  def MakeArray(h: Heap, v_arr: Value, o_results: Obj): Obj = {
    val n_len = o_results("length")._1._1._1._4 // number
    n_len.getSingle match {
      case Some(n) =>
        val o_1 =
          if (v_arr._2.isEmpty)
            o_results.
              update(n.toString, PropValue(ObjectValue(v_arr._1, T, T, T))).
              update("length", PropValue(ObjectValue(AbsNumber.alpha(n+1), T, T, T)))
          else
            Obj.bottom
        val o_2 =
          if (!v_arr._2.isEmpty) {
            v_arr._2.foldLeft(Obj.bottom)((_o, l) => {
              // window object
              if(l == GlobalLoc) { 
                val obj1 = o_results.update(n.toInt.toString, PropValue(ObjectValue(Value(l), T, T, T))).update(
                                            "length", PropValue(ObjectValue(AbsNumber.alpha(n+1), T, T, T)))
                _o + obj1                            
              }
              else {
                val n_arrlen = Helper.Proto(h, l, AbsString.alpha("length"))._1._4
                n_arrlen.getSingle match {
                  case Some(n_arr) =>
                    val oo = (0 until n_arr.toInt).foldLeft(_o)((_o1, i) =>
                      o_results.update((n+i).toInt.toString,
                        PropValue(ObjectValue(Helper.Proto(h,l,AbsString.alpha(i.toString)), T, T, T))))
                    _o +oo.update("length", PropValue(ObjectValue(AbsNumber.alpha(n+n_arr), T, T, T)))
                  case None =>
                    if (n_arrlen <= NumBot) { 
                      _o
                    }
                    else
                      _o + o_results.update(NumStr, PropValue(ObjectValue(Helper.Proto(h,l,NumStr), T, T, T)))
                }
              }
            })
          }
          else Obj.bottom
        o_1 + o_2
      case None =>
        if (n_len <= NumBot)
          Obj.bottom
        else {
          val o_1 =
            if (v_arr._2.isEmpty)
              o_results.update(Helper.toString(PValue(n_len)), PropValue(ObjectValue(v_arr._1, T, T, T)))
            else
              Obj.bottom
          val o_2 =
            if (!v_arr._2.isEmpty) {
              v_arr._2.foldLeft(Obj.bottom)((_o, l) => {
                // window object
                if(l == GlobalLoc) {
                  val newo = o_results.update(NumStr, PropValue(ObjectValue(Value(l), T, T, T)))
                  newo + _o
                }
                else {
                  _o + o_results.update(NumStr, PropValue(ObjectValue(Helper.Proto(h,l,NumStr), T, T, T)))
                }
              })
            }
            else Obj.bottom
          o_1 + o_2
        }
    }
  }
  def addEvent(h: Heap, ctx: Context, he: Heap, ctxe: Context,
                       v_data: Value, v_fn: Value, event_type: String): ((Heap, Context), (Heap, Context)) = {
    val lset_this = h(SinglePureLocalLoc)("@this")._2._2
    val (v_fun1, v_dat1) =
      if (v_fn._1._1 </ UndefBot || v_fn._1._2 </ NullBot)
        (Value(v_data._2), ValueBot)
      else
        (ValueBot, ValueBot)
    val (v_fun2, v_dat2) =
      if (v_fn._1._1 <= UndefBot && v_fn._1._2 <= NullBot)
        (Value(v_fn._2), v_data)
      else
        (ValueBot, ValueBot)
    val v_fun = v_fun1 + v_fun2
    val v_dat = v_dat1 + v_dat2
    val lset_target = lset_this.foldLeft(LocSetBot)((lset, l) =>
      lset ++ h(l)(NumStr)._1._1._2
    )
    if (v_fun </ ValueBot && !lset_target.isEmpty) {
      val h1 = JQueryHelper.addJQueryEvent(h, Value(lset_target), AbsString.alpha(event_type), v_fun, v_dat, ValueBot)
      ((Helper.ReturnStore(h1, Value(lset_this)), ctx), (he, ctxe))
    }
    else {
      val h1 = if(!lset_target.isEmpty) JQueryHelper.addJQueryEvent(h, Value(lset_target), AbsString.alpha(event_type), v_fun, v_dat, ValueBot)
               else h
      ((Helper.ReturnStore(h1, Value(lset_this)), ctx), (he, ctxe))
    }
  }

  def addJQueryEvent(h: Heap, v_elem: Value, s_types: AbsString, v_handler: Value, v_data: Value, v_selector: Value) = {

    val fun_table = h(EventFunctionTableLoc)
    val target_table = h(EventTargetTableLoc)
    val selector_table = h(EventSelectorTableLoc)
    val propv_fun = PropValue(v_handler)
    val propv_target = PropValue(v_elem)
    val propv_selector = PropValue(v_selector)
    val event_list = s_types.getAbsCase match {
      case _ if s_types.isAllNums => /* Error ?*/ List()
      case AbsBot => List()
      case AbsTop => JQueryModel.aysnc_calls
      case _ => s_types.gamma match {
        case Some(s_typesSet) =>
          s_typesSet.foldLeft(List[String]())((list, s_ev) => {
            if (DOMHelper.isLoadEventAttribute(s_ev) || DOMHelper.isLoadEventProperty(s_ev)) "#LOAD" :: list
            else if (DOMHelper.isUnloadEventAttribute(s_ev) || DOMHelper.isUnloadEventProperty(s_ev)) "#UNLOAD" :: list
            else if (DOMHelper.isKeyboardEventAttribute(s_ev) || DOMHelper.isKeyboardEventProperty(s_ev)) "#KEYBOARD" :: list
            else if (DOMHelper.isMouseEventAttribute(s_ev) || DOMHelper.isMouseEventProperty(s_ev)) "#MOUSE" :: list
            else if (DOMHelper.isOtherEventAttribute(s_ev) || DOMHelper.isOtherEventProperty(s_ev)) "#OTHER" :: list
            else if (DOMHelper.isReadyEventProperty(s_ev)) "#READY" :: list
            else {
//              if(!DOMHelper.isMobileEventProperty(s_ev)) {
//                System.err.println("* Warning: the event type, " + s_ev + ", is not modeled but added to the OTHER type.")
//                "#OTHER" :: list
//              }
//              else list
              list
            }
          })
        case None =>
          JQueryModel.aysnc_calls
      }
    }
    val o_fun = event_list.foldLeft(fun_table)((o, s_ev) =>
      o.update(s_ev, o(s_ev) + propv_fun)
    )
    val o_target = event_list.foldLeft(target_table)((o, s_ev) =>
      o.update(s_ev, o(s_ev) + propv_target)
    )
    val o_selector = event_list.foldLeft(selector_table)((o, s_ev) =>
      o.update(s_ev, o(s_ev) + propv_selector)
    )
    h.update(EventFunctionTableLoc, o_fun).update(EventTargetTableLoc, o_target).update(EventSelectorTableLoc, o_selector)
  }


  private val reg_quick = """^(?:[^#<]*(<[\w\W]+>)[^>]*$|#([\w\-]*)$)""".r
  private val reg_id = """([\w]+)""".r

  def init(h: Heap, v_selector: Value, v_context: Value,
           l_jq: Loc, l_tag: Loc, l_child: Loc, l_attributes: Loc): (Heap, Value) = {
    //val h_start = h
    //val ctx_start = ctx_3

    val lset_this = h(SinglePureLocalLoc)("@this")._2._2

    // 1) Handle $(""), $(null), $(undefined), $(false)
    val (h_ret1, v_ret1) =
      if (UndefTop <= v_selector._1._1 || NullTop <= v_selector._1._2 ||
        F <= v_selector._1._3 || AbsString.alpha("") <= v_selector._1._5) {
        // empty jQuery object
        (h.update(l_jq, NewJQueryObject), Value(l_jq))
      }
      else
        (HeapBot, ValueBot)

    // 2) Handle $(DOMElement)
    val (h_ret2, v_ret2) =
      if (!v_selector._2.isEmpty) {
        v_selector._2.foldLeft((h, ValueBot))((hv, l) => {
          val v_nodeType = Helper.Proto(h, l, AbsString.alpha("nodeType"))
          if (T <= Helper.toBoolean(v_nodeType)) {
            // jQuery object
            val o_jq = NewJQueryObject(1)
              .update("context", PropValue(ObjectValue(v_selector, T, T, T)))
              .update("0",       PropValue(ObjectValue(v_selector, T, T, T)))
            val _h1 = h.update(l_jq, o_jq)
            (hv._1 + _h1, hv._2 + Value(lset_this))
          }
          else
            hv
        })
      }
      else
        (HeapBot, ValueBot)

    // 3) Handle HTML strings
    val absstr = v_selector._1._5
    val (h_ret3, v_ret3) = absstr.getAbsCase match {
      case _ if absstr.isAllNums =>
        // jQuery object
        val o = NewJQueryObject(0)
          .update("selector", PropValue(ObjectValue(v_selector, T, T, T)))
          .update("prevObject", PropValue(ObjectValue(Value(JQuery.RootJQLoc) + Value(l_jq), T, T, T)))
          .update("context", PropValue(ObjectValue(HTMLDocument.GlobalDocumentLoc, T, T, T)))
        (h.update(l_jq, o), Value(l_jq))
      case AbsBot =>
        (HeapBot, ValueBot)
      case _ => absstr.gamma match {
        case Some(absStrSet) => // OtherStrSingle(s) =>
          absStrSet.foldLeft[(Heap, Value)]((h, ValueBot))((hv, s) => {
            val (h, v) = hv
            if(AbsString.isNum(s)) {
              val o = NewJQueryObject(0)
                .update("selector", PropValue(ObjectValue(v_selector, T, T, T)))
                .update("prevObject", PropValue(ObjectValue(Value(JQuery.RootJQLoc) + Value(l_jq), T, T, T)))
                .update("context", PropValue(ObjectValue(HTMLDocument.GlobalDocumentLoc, T, T, T)))
              (h.update(l_jq, o), v + Value(l_jq))
            }
            else {
              val matches = reg_quick.unapplySeq(s)
              val (h3, v3) = matches match {
                case Some(mlist) =>
                  val tag_name = mlist(0)
                  // HANDLE: $(html) -> $(array)
                  // unsoud, support only tag name
                  if (tag_name != null) {
                    //val s_tag = tag_name.filter((c) => c != '<' && c != '>').toUpperCase
                    val s_tag = reg_id.findFirstIn(tag_name).get
                    // jQuery object
                    val o_jq =NewJQueryObject(UInt)
                      .update("selector",   PropValue(ObjectValue(v_selector, T, T, T)))
                      .update("prevObject", PropValue(ObjectValue(Value(JQuery.RootJQLoc), T, T, T)))
                      .update("0",          PropValue(ObjectValue(Value(l_tag), T, T, T)))
                    val _h1 = DOMHelper.addTag(h, s_tag, l_tag, l_child, l_attributes).update(l_jq, o_jq)
                    (_h1, Value(l_jq))
                  }
                  // HANDLE: $(#id)
                  else {
                    val s_id = mlist(1)
                    // getElementById
                    val lset_id = DOMHelper.findById(h, AbsString.alpha(s_id))
                    // jQuery object
                    val o_jq = NewJQueryObject(lset_id.size)
                      .update("selector", PropValue(ObjectValue(v_selector, T, T, T)))
                    val o_jq1 =
                      if (lset_id.isEmpty)
                        o_jq
                      else
                        o_jq.update("0", PropValue(ObjectValue(Value(lset_id), T, T, T)))
                    (h.update(l_jq, o_jq1), Value(l_jq))
                  }
                case None =>
                  // HANDLE: $(expr, $(...))
                  // else if ( !context || context.jquery ) {
                  val (h1, v1) =
                  //              if (v_context._1._1 </ UndefBot) {
                    if (v_context._2 == LocSetBot) {
                      // prev = rootjQuery
                      val lset_find = DOMHelper.querySelectorAll(h, s)
                      // jQuery object
                      val o_jq =NewJQueryObject(lset_find.size)
                        .update("selector",   PropValue(ObjectValue(v_selector, T, T, T)))
                        .update("prevObject", PropValue(ObjectValue(Value(JQuery.RootJQLoc), T, T, T)))
                      val o_jq1 =
                        if (lset_find.isEmpty)
                          o_jq
                        else
                          o_jq.update(NumStr, PropValue(ObjectValue(Value(lset_find), T, T, T)))
                      (h.update(l_jq, o_jq1), Value(l_jq))
                    }
                    else {
                      // TODO : we should find elements using selector in the context
                      // prev = rootjQuery
                      val lset_find = DOMHelper.querySelectorAll(h, s)
                      // jQuery object
                      val o_jq =NewJQueryObject(lset_find.size)
                        .update("selector",   PropValue(ObjectValue(v_selector, T, T, T)))
                        .update("prevObject", PropValue(ObjectValue(Value(JQuery.RootJQLoc), T, T, T)))
                      val o_jq1 =
                        if (lset_find.isEmpty)
                          o_jq
                        else
                          o_jq.update(NumStr, PropValue(ObjectValue(Value(lset_find), T, T, T)))
                      (h.update(l_jq, o_jq1), Value(l_jq))
                    }
                  //               (HeapBot, ValueBot)

                  val v_jquery = v_context._2.foldLeft(ValueBot)((v,l) =>
                    v + Helper.Proto(h, l, AbsString.alpha("jquery"))
                  )
                  val (h2, v2) =
                    if (UndefTop <= v_context._1._1 && v_jquery._1._1 </ UndefBot) {
                      // prev = context
                      val lset_context = v_context._2.foldLeft(LocSetBot)((lset, l) => lset ++ h(l)(NumStr)._1._1._2)
                      val lset_find = lset_context.foldLeft(LocSetBot)((lset, l) => lset ++ DOMHelper.querySelectorAll(h, s))
                      // jQuery object
                      val o_jq = NewJQueryObject(lset_find.size)
                        .update("selector", PropValue(ObjectValue(v_selector, T, T, T)))
                        .update("prevObject", PropValue(ObjectValue(v_context, T, T, T)))
                      val o_jq1 =
                        if (lset_find.isEmpty)
                          o_jq
                        else
                          o_jq.update(NumStr, PropValue(ObjectValue(Value(lset_find), T, T, T)))
                      (h.update(l_jq, o_jq1), Value(l_jq))
                    }
                    else
                      (HeapBot, ValueBot)

                  // TODO: HANDLE: $(expr, context)
                  // (which is just equivalent to: $(context).find(expr)
                  // return this.constructor( context ).find( selector );

                  (h1 + h2, v1 + v2)
              }
              (h + h3, v + v3)
            }
          })
        case None => // OtherStr | StrTop =>
          // top element
          val _h1 = DOMHelper.addTagTop(h, l_tag, l_jq)
          // jQuery object
          val o_jq = NewJQueryObject(UInt)
            .update("selector", PropValue(ObjectValue(v_selector, T, T, T)))
            .update("prevObject", PropValue(ObjectValue(Value(JQuery.RootJQLoc) + Value(l_jq), T, T, T)))
            .update(NumStr, PropValue(ObjectValue(Value(l_tag) + Value(HTMLTopElement.getInsLoc(_h1)), T, T, T)))
          (_h1.update(l_jq, o_jq), Value(l_jq))
      }
    }

    // 4) HANDLE: $(function), Shortcut for document ready event
    val lset_f = v_selector._2.filter(l => T <= Helper.IsCallable(h, l))
    val (h_ret4, v_ret4) =
      if (!lset_f.isEmpty) {
        val h1 = addJQueryEvent(h, Value(HTMLDocument.GlobalDocumentLoc),
          AbsString.alpha("DOMContentLoaded"), Value(lset_f), ValueBot, ValueBot)
        (h1, Value(JQuery.RootJQLoc))
      }
      else
        (HeapBot, ValueBot)

    // Handle: else
    val (h_ret5, v_ret5) = v_selector._2.foldLeft((HeapBot, ValueBot))((hv, l) => {
      // jquery  object
      val o_1 =
        if (Helper.Proto(h, l, AbsString.alpha("selector"))._1._1 </ UndefBot) {
          NewJQueryObject().
            update("selector", PropValue(ObjectValue(Helper.Proto(h, l, AbsString.alpha("selector")), T, T, T))).
            update("context", PropValue(ObjectValue(Helper.Proto(h, l, AbsString.alpha("context")), T, T, T)))
        }
        else
          NewJQueryObject()
      // make array
      val o_2 = MakeArray(h, v_selector, o_1)
      val _h1 = h.update(l_jq, o_2)
      (hv._1 + _h1, hv._2 + Value(l_jq))
    })

    val h_ret = h_ret1 + h_ret2 + h_ret3 + h_ret4 + h_ret5
    val v_ret = v_ret1 + v_ret2 + v_ret3 + v_ret4 + v_ret5
    (h_ret, v_ret)
  }


  def extend(h: Heap, args: List[Value]): (Heap, Value) = {
    val len = args.length
    if (len <= 0) {
      (HeapBot, ValueBot)
    }
    else if (len == 1) {
      // target = this
      val lset_this = h(SinglePureLocalLoc)("@this")._2._2
      val lset_arg1 = args(0)._2
      val h_ret = lset_this.foldLeft(h)((h1, l1) =>
        lset_arg1.foldLeft(h1)((h2, l2) => {
          val props = h2(l2).getProps
          val h2_1 = props.foldLeft(h2)((h3, p) =>
            Helper.PropStore(h3, l1, AbsString.alpha(p), Helper.Proto(h3, l2, AbsString.alpha(p)))
          )
          val o_arg1 = h2_1(l2)
          val o_target = h2_1(l1)
          val o_target_new = o_target
            .update(NumStr, o_arg1(NumStr) + o_target(NumStr))
            .update(OtherStr, o_arg1(OtherStr) + o_target(OtherStr))
          h2_1.update(l1, o_target_new)
        })
      )
      (h_ret, Value(lset_this))
    }
    else {
      val v_arg1 = args(0)
      val (target, list_obj) =
        if (v_arg1._1._3 </ BoolBot)
          (args(1), args.tail.tail)
        else
          (v_arg1, args.tail)
      val lset_target = target._2
      val lset_obj = list_obj.foldLeft(LocSetBot)((lset, v) => lset ++ v._2)
      val h_ret = lset_target.foldLeft(h)((h1, l1) =>
        lset_obj.foldLeft(h1)((h2, l2) => {
          val props = h2(l2).getProps
          val h2_1 = props.foldLeft(h2)((h3, p) =>
            Helper.PropStore(h3, l1, AbsString.alpha(p), Helper.Proto(h3, l2, AbsString.alpha(p)))
          )
          val o_arg1 = h2_1(l2)
          val o_target = h2_1(l1)
          val o_target_new = o_target
            .update(NumStr, o_arg1(NumStr) + o_target(NumStr))
            .update(OtherStr, o_arg1(OtherStr) + o_target(OtherStr))
          h2_1.update(l1, o_target_new)
        })
      )
      (h_ret, Value(lset_target))
    }
  }

  def pushStack(h: Heap, lset_prev: LocSet, lset_next: LocSet): Heap = {
    val v_context = lset_prev.foldLeft(ValueBot)((v, l) => v+ Helper.Proto(h, l, AbsString.alpha("context")))
    lset_next.foldLeft(h)((h1, l1) => {
      val h1_1 = Helper.PropStore(h1, l1, AbsString.alpha("context"), v_context)
      Helper.PropStore(h1_1, l1, AbsString.alpha("prevObject"), Value(lset_prev))
    })
  }

  def isArraylike(h: Heap, l: Loc): AbsBool = {
    val n_len = Helper.Proto(h, l, AbsString.alpha("length"))._1._4
    val s_class = h(l)("@class")._2._1._5
    val n_nodeType = Helper.Proto(h, l, AbsString.alpha("nodeType"))._1._4
    val b1 =
      if (n_len </ NumBot && AbsString.alpha("Function") </ s_class)
        T
      else
        BoolBot
    val b2 =
      if (n_len <= NumBot || AbsString.alpha("Function") <= s_class)
        F
      else
        BoolBot
    // if(obj.nodeType === 1 && length) return true
    val b3 = 
      if(AbsNumber.alpha(1) <= n_nodeType || n_len </ NumBot)
        T
      else
        BoolBot
    // if(jQuery.isWindow(obj)) return false
    if(l == GlobalLoc) F
    else
      b1 + b2 + b3
  }
}
