/*******************************************************************************
    Copyright (c) 2013-2014, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models

import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models.Tizen._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, IRFactory}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.cfg.CFGTempId
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.cfg.CFGAsyncCall
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object TizenModel {
  val async_calls : List[String] = List("#NOARGCB", "#STRCB", "#NUMCB", "#ERRCB", "#APPINFOCB", "#BTDEVCB", "#BTDEVARRCB", "#CALITEMARRCB",
    "#CHENTRYARRCB", "#CONTACTARRCB", "#PERSONARRCB", "#CONTENTCB", "#MSGARRCB", "#MSGCONVARRCB", "#MSGFOLDERARRCB",
    "#PKGINFOCB", "#READERCB", "#APPINFOARRSUCCESSCB", "#FINDAPPCTRLSUCCESSCB", "#APPCONTEXTARRAYSUCCESSCB",
    "#APPCTRLDATAARRAYREPLYCB.onsuccess", "#BTSOCKETSUCCESSCB", "#BTSERVSUCCESSCB", "#CALEVENTARRSUCCESSCB", "#CALARRSUCCESSCB",
    "#CALCHANGECB.onitemsremoved", "#ADDRBOOKARRSUCCESSCB", "#ADDRBOOKCHANGECB.oncontactsremoved", "#PERSONSCHANGECB.onpersonsremoved",
    "#CONTENTARRSUCESSCB", "#CONTENTDIRARRSUCCESSCB", "#DATACTRLERRCB", "#DATACTRLINSERTSUCCESSCB", "#DATACTRLSELECTSUCCESSCB",
    "#DATACTRLGETVALSUCCESSCB", "#SYNCPROGRESSCB.onprogress", "#SYNCPROGRESSCB.onfailed", "#DOWNLOADCB.onprogress", "#DOWNLOADCB.oncompleted",
    "#DOWNLOADCB.onfailed", "#FILESUCCESSCB", "#FILESYSSTORARRSUCCESSCB", "#FILESTREAMSUCCESSCB", "#FILEARRSUCCESSCB", "#MSGPORTCB",
    "#MSGRECIPIENTSCB", "#MSGBODYSUCCESSCB", "#MSGATTACHMENTSUCCESSCB", "#MSGSERVARRSUCCESSCB", "#MSGFOLDARRSUCCESSCB", "#NFCTAGDETECTCB.onattach", "#NFCPEERDETECTCB.onattach",
    "#NDEFMSGREADCB", "#BYTEARRSUCCESSCB", "#SETYPECHANGECB", "#PKGINFOARRSUCCESSCB", "#PKGPROGRESSCB.onprogress", "#SCREENSTATECHANGECB",
    "#PUSHNOTICB", "#READERARRSUCCESSCB", "#SESSIONSUCCESSCB", "#CHANNELSUCCESSCB", "#TRANSMITSUCCESSCB", "#SYSINFOPROPSUCCESSCB")
}

class TizenModel(cfg: CFG) extends Model(cfg) {

  private val list_tizen = List[Tizen](
    TIZENtizen, TIZENalarm, TIZENAlarmAbsolute, TIZENAlarmRelative, TIZENApplication, TIZENapplicationObj, TIZENApplicationInformation,
    TIZENApplicationControl, TIZENApplicationControlData, TIZENApplicationContext, TIZENApplicationCertificate, TIZENAttributeFilter, TIZENAttributeRangeFilter, TIZENbluetooth,
    TIZENBluetoothClassDeviceMajor, TIZENBluetoothClassDeviceMinor, TIZENBluetoothClassDeviceService, TIZENBluetoothAdapter,
    TIZENBluetoothDevice, TIZENBluetoothClass, TIZENBluetoothSocket, TIZENBluetoothServiceHandler, TIZENbookmark, TIZENBookmarkFolder, TIZENBookmarkItem, TIZENcalendarObj, TIZENCalendar,
    TIZENCalendarAlarm, TIZENCalendarAttendee, TIZENCalendarItem, TIZENCalendarEvent, TIZENCalendarEventId, TIZENCalendarRecurrenceRule,
    TIZENCalendarTask, TIZENcallhistory, TIZENRemoteParty, TIZENCompositeFilter, TIZENcontactObj, TIZENAddressBook, TIZENContact,
    TIZENContactAddress, TIZENContactAnniversary, TIZENPerson, TIZENRequestedApplicationControl,
    TIZENContactEmailAddress, TIZENContactGroup, TIZENContactName, TIZENContactOrganization, TIZENContactPhoneNumber,
    TIZENContactRef, TIZENContactWebSite, TIZENcontentObj, TIZENdatacontrol, TIZENDataControlConsumerObject,
    TIZENSQLDataControlConsumer, TIZENMappedDataControlConsumer, TIZENdatasync, TIZENdownload, TIZENDownloadRequest,
    TIZENfilesystem, TIZENFile, TIZENFileSystemStorage, TIZENFileFilter, TIZENFileStream, TIZENMessage, TIZENMessageBody, TIZENMessageFolder, TIZENMessageConversation, TIZENMessageAttachment,
    TIZENmessageport, TIZENLocalMessagePort, TIZENRemoteMessagePort, TIZENmessaging,
    TIZENMessageStorage, TIZENMessageService, TIZENNDEFMessage, TIZENNDEFRecord,
    TIZENNDEFRecordMedia, TIZENNDEFRecordText, TIZENNDEFRecordURI, TIZENnetworkbearerselection, TIZENnfc, TIZENNFCAdapter,
    TIZENNFCPeer, TIZENNFCTag, TIZENnotificationObj, TIZENNotification, TIZENNotificationDetailInfo, TIZENpackage, TIZENPackageInformation,
    TIZENpower, TIZENpush, TIZENseService, TIZENReader, TIZENSession, TIZENChannel, TIZENSimpleCoordinates,
    TIZENSortMode, TIZENStatusNotification, TIZENSyncInfo, TIZENSyncProfileInfo,
    TIZENSyncServiceInfo, TIZENsysteminfo, TIZENSystemInfoDeviceCapability, TIZENsystemsetting, TIZENtime, TIZENTimeDuration,
    TIZENTZDate, TIZENWebAPIError
  )

  private var map_fid = Map[FunctionId, String]()
  private var map_semantic = Map[String, SemanticFun]()
  private var map_presemantic =  Map[String, SemanticFun]()
  private var map_def =  Map[String, AccessFun]()
  private var map_use =  Map[String, AccessFun]()


  def initialize(h: Heap): Heap = {
    /* init function map */
    map_semantic = list_tizen.foldLeft(map_semantic)((m, tizen) => m ++ tizen.getSemanticMap())
    map_presemantic = list_tizen.foldLeft(map_presemantic)((m, tizen) => m ++ tizen.getPreSemanticMap())
    map_def = list_tizen.foldLeft(map_def)((m, tizen) => m ++ tizen.getDefMap())
    map_use = list_tizen.foldLeft(map_use)((m, tizen) => m ++ tizen.getUseMap())

    /* init api objects */
    val h_1 = list_tizen.foldLeft(h)((h1, tizen) =>
      tizen.getInitList().foldLeft(h1)((h2, lp) => {
        /* List[(String, PropValue, Option[(Loc, Obj)], Option[FunctionId] */
        val list_props = lp._2.map((x) => prepareForUpdate("Tizen", x._1, x._2))//again
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
        /* added function object to heap if any*/
        val heap = list_props.foldLeft(h2)((h3, pvo) => pvo._3 match {
          case Some((l, o)) => Heap(h3.map.updated(l, o))
          case None => h3
        })

        /* added api obejct to heap */
        Heap(heap.map.updated(lp._1, obj))
      })
    )

    Heap(h_1.map + (TizenCallbackTableLoc -> Obj.empty) + (TizenCallbackArgTableLoc -> Obj.empty))
  }

  def addAsyncCall(cfg: CFG, loop_head: Node): (List[Node],List[Node]) = {
    val fid_global = cfg.getGlobalFId
    /* dummy info for EventDispatch instruction */
    val dummy_info = IRFactory.makeInfo(IRFactory.dummySpan("TizenCallback"))
    /* dummy var for after call */
    val dummy_id = CFGTempId(NU.ignoreName+"#AsyncCall#", PureLocalVar)
    /* add async call */
    TizenModel.async_calls.foldLeft((List[Node](),List[Node]()))((nodes, ev) => {
      /* event call */
      val event_call = cfg.newBlock(fid_global)
      cfg.addInst(event_call,
        CFGAsyncCall(cfg.newInstId, dummy_info, "Tizen", ev, newProgramAddr(), newProgramAddr(), newProgramAddr()))
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
    val callback_table = h(TizenCallbackTableLoc)
    val callbackarg_table = h(TizenCallbackArgTableLoc)

    val lset_fun = callback_table(name)._2._2
    val lset_args = callbackarg_table(name)._2._2

    val l_r = addrToLoc(addr1, Recent)
    val l_arg = addrToLoc(addr2, Recent)
    val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)
    val (h_2, ctx_2) = PreHelper.Oldify(h_1, ctx_1, addr2)

    // 'this' = global this
    val lset_this = LocSet(GlobalLoc)

    val h_3 = name match {
      case "#NOARGCB" =>
        val o_old = h_2(SinglePureLocalLoc)
        val cc_caller = cp._2
        val n_aftercall = cfg.getAftercallFromCall(cp._1)
        val cp_aftercall = (n_aftercall, cc_caller)
        val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
        val cp_aftercatch = (n_aftercatch, cc_caller)
        lset_fun.foreach {l_f:Loc => {
          val o_f = h_2(l_f)
          val fids = o_f("@function")._3
          fids.foreach {fid => {
            val ccset = cc_caller.NewCallContext(h, cfg, fid, l_r, lset_this)
            ccset.foreach {case (cc_new, o_new) => {
              val o_new2 =
                o_new.
                  update("@scope", o_f("@scope"))
              sem.addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
              sem.addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_2, o_old)
              sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_2, o_old)
            }}
          }}
        }}
        h_2
      case _ =>
        val h_3 = lset_args.foldLeft(HeapBot)((_h, l) => {
          _h + h_2.update(l_arg, h_2(l))
        })
         val v_arg = Value(LocSet(l_arg))
        val o_old = h_3(SinglePureLocalLoc)
        val cc_caller = cp._2
        val n_aftercall = cfg.getAftercallFromCall(cp._1)
        val cp_aftercall = (n_aftercall, cc_caller)
        lset_fun.foreach {l_f:Loc => {
          val o_f = h_3(l_f)
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
              sem.addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_2, o_old)
              sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercall, ctx_2, o_old)
            }}
          }}
        }}
        v_arg._2.foldLeft(HeapBot)((hh, l) => {
          val pv = PropValue(ObjectValue(Value(lset_fun), BoolTrue, BoolFalse, BoolTrue))
          hh + h_3.update(l, h_3(l).update("callee", pv))
        })
    }
    ((h_3, ctx_2), (he, ctxe))
  }
  def asyncPreSemantic(sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG,
                       name: String, list_addr: List[Address]): (Heap, Context) = {
    (HeapBot, ContextBot)
  }
  def asyncDef(h: Heap, ctx: Context, cfg: CFG, name: String, list_addr: List[Address]): LPSet = {
    LPBot
  }
  def asyncUse(h: Heap, ctx: Context, cfg: CFG, name: String, list_addr: List[Address]): LPSet = {
    LPBot
  }
  def asyncCallgraph(h: Heap, inst: CFGInst, map: Map[CFGInst, Set[FunctionId]],
                     name: String, list_addr: List[Address]): Map[CFGInst, Set[FunctionId]] = {
    Map()
  }
}