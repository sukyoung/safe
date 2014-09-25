/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import scala.collection.immutable.HashSet
import kr.ac.kaist.jsaf.analysis.typing.Helper
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}

object TizenHelper {
  def instanceOf(h: Heap, v_1: Value, v_2: Value): (Value, Set[WebAPIException]) = {
    val lset_1 = v_1._2
    val lset_2 = v_2._2
    val lset_3 = lset_2.filter((l) => T <= Helper.HasInstance(h, l))
    val v_proto = lset_3.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h,l,AbsString.alpha("prototype")))
    val lset_4 = v_proto._2
    val lset_5 = lset_2.filter((l) => F <= Helper.HasInstance(h, l))
    val b_1 = lset_1.foldLeft[Value](ValueBot)((v_1, l_1) =>
      lset_4.foldLeft[Value](v_1)((v_2, l_2) => v_2 + Helper.inherit(h, l_1, l_2)))
    val b_2 =
      if ((v_1._1 </ PValueBot) && !(lset_4.isEmpty))
        Value(F)
      else
        Value(BoolBot)
    val es =
      if ((v_2._1 </ PValueBot) || !(lset_5.isEmpty) || (v_proto._1 </ PValueBot))
        Set[WebAPIException](TypeMismatchError)
      else
        TizenHelper.TizenExceptionBot
    val b = b_1 + b_2
    (b, es)
  }
  def addCallbackHandler(h: Heap, s: AbsString, v_fun: Value, args: Value): Heap = {
    val callback_table = h(TizenCallbackTableLoc)
    val callbackarg_table = h(TizenCallbackArgTableLoc)
    val propv_fun = PropValue(v_fun)
    val propv_args = PropValue(args)
    val callback_list = s.getAbsCase match {
      case AbsBot => List()
      case _ if s.isAllNums => /* Error ?*/ List()
      case _ => s.gamma match {
        case None =>
          List("#NOARGCB", "#STRCB", "#NUMCB", "#ERRCB", "#APPINFOCB", "#BTDEVCB", "#BTDEVARRCB", "#CALITEMARRCB",
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
        case Some(vs) =>
          vs.foldLeft(List[String]())((list, s_ev) => {
            if (isNoArgCBAttribute(s_ev)) "#NOARGCB" :: list
            else if (isStrCBAttribute(s_ev)) "#STRCB" :: list
            else if (isNumCBAttribute(s_ev)) "#NUMCB" :: list
            else if (isAppInfoCBAttribute(s_ev)) "#APPINFOCB" :: list
            else if (isBTDevCBAttribute(s_ev)) "#BTDEVCB" :: list
            else if (isBTDevArrCBAttribute(s_ev)) "#BTDEVARRCB" :: list
            else if (isCalItemArrCBAttribute(s_ev)) "#CALITEMARRCB" :: list
            else if (isCHEntryArrCBAttribute(s_ev)) "#CHENTRYARRCB" :: list
            else if (isContactArrCBAttribute(s_ev)) "#CONTACTARRCB" :: list
            else if (isPersonArrCBAttribute(s_ev)) "#PERSONARRCB" :: list
            else if (isContentCBAttribute(s_ev)) "#CONTENTCB" :: list
            else if (isMsgArrCBAttribute(s_ev)) "#MSGARRCB" :: list
            else if (isMsgConvArrCBAttribute(s_ev)) "#MSGCONVARRCB" :: list
            else if (isMsgFolderArrCBAttribute(s_ev)) "#MSGFOLDERARRCB" :: list
            else if (isPkgInfoCBAttribute(s_ev)) "#PKGINFOCB" :: list
            else if (isReaderCBAttribute(s_ev)) "#READERCB" :: list
            else if (s_ev == "errorCB") "#ERRCB" :: list
            else if (s_ev == "AppInfoArraySuccessCB") "#APPINFOARRSUCCESSCB" :: list
            else if (s_ev == "FindAppCtrlSuccessCB") "#FINDAPPCTRLSUCCESSCB" :: list
            else if (s_ev == "AppContextArraySuccessCB") "#APPCONTEXTARRAYSUCCESSCB" :: list
            else if (s_ev == "AppCtrlDataArrayReplyCB.onsuccess") "#APPCTRLDATAARRAYREPLYCB.onsuccess" :: list
            else if (s_ev == "BTSocketSuccessCB") "#BTSOCKETSUCCESSCB" :: list
            else if (s_ev == "BTServiceSuccessCB") "#BTSERVSUCCESSCB" :: list
            else if (s_ev == "CalEventArrSuccessCB") "#CALEVENTARRSUCCESSCB" :: list
            else if (s_ev == "CalArrSuccessCB") "#CALARRSUCCESSCB" :: list
            else if (s_ev == "CalChangeCB.onitemsremoved") "#CALCHANGECB.onitemsremoved" :: list
            else if (s_ev == "AddrBookArrSuccessCB") "#ADDRBOOKARRSUCCESSCB" :: list
            else if (s_ev == "AddrBookChangeCB.oncontactsremoved") "#ADDRBOOKCHANGECB.oncontactsremoved" :: list
            else if (s_ev == "PersonsChangeCB.onpersonsremoved") "#PERSONSCHANGECB.onpersonsremoved" :: list
            else if (s_ev == "ContentArrSuccessCB") "#CONTENTARRSUCESSCB" :: list
            else if (s_ev == "ContentDirArraySuccessCB") "#CONTENTDIRARRSUCCESSCB" :: list
            else if (s_ev == "DataCtrlErrCB") "#DATACTRLERRCB" :: list
            else if (s_ev == "DataCtrlInsertSuccessCB") "#DATACTRLINSERTSUCCESSCB" :: list
            else if (s_ev == "DataCtrlSelectSuccessCB") "#DATACTRLSELECTSUCCESSCB" :: list
            else if (s_ev == "DataCtrlGetValSuccessCB") "#DATACTRLGETVALSUCCESSCB" :: list
            else if (s_ev == "SyncProgressCB.onprogress") "#SYNCPROGRESSCB.onprogress" :: list
            else if (s_ev == "SyncProgressCB.onfailed") "#SYNCPROGRESSCB.onfailed" :: list
            else if (s_ev == "DownloadCB.onprogress") "#DOWNLOADCB.onprogress" :: list
            else if (s_ev == "DownloadCB.oncompleted") "#DOWNLOADCB.oncompleted" :: list
            else if (s_ev == "DownloadCB.onfailed") "#DOWNLOADCB.onfailed" :: list
            else if (s_ev == "FileSuccessCB") "#FILESUCCESSCB" :: list
            else if (s_ev == "FileSystemStorArrSuccessCB") "#FILESYSSTORARRSUCCESSCB" :: list
            else if (s_ev == "FileSystemStorSuccessCB") "#APPINFOARRSUCCESSCB" :: list
            else if (s_ev == "FileStreamSuccessCB") "#FILESTREAMSUCCESSCB" :: list
            else if (s_ev == "FileArrSuccessCB") "#FILEARRSUCCESSCB" :: list
            else if (s_ev == "MsgPortCB") "#MSGPORTCB" :: list
            else if (s_ev == "MsgRecipientsCB") "#MSGRECIPIENTSCB" :: list
            else if (s_ev == "MsgBodySuccessCB") "#MSGBODYSUCCESSCB" :: list
            else if (s_ev == "MsgAttachmentSuccessCB") "#MSGATTACHMENTSUCCESSCB" :: list
            else if (s_ev == "MsgServiceArrSuccessCB") "#MSGSERVARRSUCCESSCB" :: list
            else if (s_ev == "MsgFolderArrSuccessCB") "#MSGFOLDARRSUCCESSCB" :: list
            else if (s_ev == "NFCTagDetectCB.onattach") "#NFCTAGDETECTCB.onattach" :: list
            else if (s_ev == "NFCPeerDetectCB.onattach") "#NFCPEERDETECTCB.onattach" :: list
            else if (s_ev == "NDEFMessageReadCB") "#NDEFMSGREADCB" :: list
            else if (s_ev == "ByteArrSuccessCB") "#BYTEARRSUCCESSCB" :: list
            else if (s_ev == "SETypeChangeCB") "#SETYPECHANGECB" :: list
            else if (s_ev == "PkgInfoArrSuccessCB") "#PKGINFOARRSUCCESSCB" :: list
            else if (s_ev == "PkgProgressCB.onprogress") "#PKGPROGRESSCB.onprogress" :: list
            else if (s_ev == "ScreenStateChangeCB") "#SCREENSTATECHANGECB" :: list
            else if (s_ev == "PushNotiCB") "#PUSHNOTICB" :: list
            else if (s_ev == "ReaderArrSuccessCB") "#READERARRSUCCESSCB" :: list
            else if (s_ev == "SessionSuccessCB") "#SESSIONSUCCESSCB" :: list
            else if (s_ev == "ChannelSuccessCB") "#CHANNELSUCCESSCB" :: list
            else if (s_ev == "TransmitSuccessCB") "#TRANSMITSUCCESSCB" :: list
            else if (s_ev == "SystemInfoPropSuccessCB") "#SYSINFOPROPSUCCESSCB" :: list
            else list
          })
      }
    }
    val (o_fun, o_args) = callback_list.foldLeft((callback_table, callbackarg_table))((o, s_ev) =>
      (o._1.update(s_ev, o._1(s_ev) + propv_fun), o._2.update(s_ev, o._2(s_ev) + propv_args))
    )
    h.update(TizenCallbackTableLoc, o_fun).update(TizenCallbackArgTableLoc, o_args)
  }
  def TizenRaiseException(h:Heap, ctx:Context, es:Set[WebAPIException]): (Heap,Context) = {
    if (es.isEmpty)
      (HeapBot, ContextBot)
    else {
      val v_old = h(SinglePureLocalLoc)("@exception_all")._2
      val v_e = Value(PValueBot,
        es.foldLeft(LocSetBot)((lset,exc)=> lset + TizenNewExceptionLoc(exc)))
      val h_1 = h.update(SinglePureLocalLoc,
        h(SinglePureLocalLoc).update("@exception", PropValue(v_e)).
          update("@exception_all", PropValue(v_e + v_old)))
      (h_1,ctx)
    }
  }

  def TizenNewExceptionLoc(exc: WebAPIException): Loc = {
    exc match {
      case UnknownError => TIZENtizen.loc_unknownerr
      case TypeMismatchError => TIZENtizen.loc_typemismatcherr
      case InvalidValuesError => TIZENtizen.loc_invalidValueserr
      case IOError => TIZENtizen.loc_IOerr
      case ServiceNotAvailableError => TIZENtizen.loc_serviceNotAvailableerr
      case NetworkError => TIZENtizen.loc_networkerr
      case NotFoundError => TIZENtizen.loc_notFounderr
      case AbortError => TIZENtizen.loc_aborterr
      case SecurityError => TIZENtizen.loc_securityerr
      case NotSupportedError => TIZENtizen.loc_notSupportederr
    }
  }

  val TizenExceptionBot = HashSet[WebAPIException]()

  def isNoArgCBAttribute(attr: String): Boolean = {
    attr=="successCB" || attr=="NFCPeerDetectCB.ondetach" || attr=="NFCTagDetectCB.ondetach" || attr=="NetworkSuccessCB.onsuccess" ||
    attr=="NetworkSuccessCB.ondisconnected" || attr=="BluetoothDiscvDevsSuccessCB.onstarted" || attr=="AppCtrlDataArrayReplyCB.onfailure"
  }

  def isStrCBAttribute(attr: String): Boolean = {
    attr=="AppInfoEventCB.onuninstalled" || attr=="BluetoothDiscvDevsSuccessCB.ondevicedisappeared" || attr=="ContentScanSuccessCB" ||
    attr=="ContentChangeCB.oncontentremoved" || attr=="SyncProgressCB.oncompleted" || attr=="SyncProgressCB.onstopped" ||
    attr=="FileStringSuccessCB" || attr=="PkgProgressCB.oncomplete" || attr=="PkgInfoEventCB.onuninstalled" ||
    attr=="PushRegisterSuccessCB" || attr=="SystemSettingSuccessCB"
  }

  def isNumCBAttribute(attr: String): Boolean = {
    attr=="DataControlSuccessCB" || attr=="DownloadCB.onpaused" || attr=="DownloadCB.oncanceled"
  }
  def isAppInfoCBAttribute(attr: String): Boolean = {
    attr=="AppInfoEventCB.oninstalled" || attr=="AppInfoEventCB.onupdated"
  }
  def isBTDevCBAttribute(attr: String): Boolean = {
    attr=="BluetoothDevSuccessCB" || attr=="BluetoothDiscvDevsSuccessCB.ondevicefound"
  }
  def isBTDevArrCBAttribute(attr: String): Boolean = {
    attr=="BluetoothDevArraySuccessCB" || attr=="BluetoothDiscvDevsSuccessCB.onfinished"
  }
  def isCalItemArrCBAttribute(attr: String): Boolean = {
    attr=="CalendarItemArraySuccessCB" || attr=="CalChangeCB.onitemsadded" || attr=="CalChangeCB.onitemsupdated"
  }
  def isCHEntryArrCBAttribute(attr: String): Boolean = {
    attr=="CHEntryArraySuccessCB" || attr=="CHChangeCB.onadded" || attr=="CHChangeCB.onchanged"
  }
  def isContactArrCBAttribute(attr: String): Boolean = {
    attr=="ContactArraySuccessCB" || attr=="AddrBookChangeCB.oncontactsadded" || attr=="AddrBookChangeCB.oncontactsupdated"
  }
  def isPersonArrCBAttribute(attr: String): Boolean = {
    attr=="PersonArraySuccessCB" || attr=="PersonsChangeCB.onpersonsadded" || attr=="PersonsChangeCB.onpersonsupdated"
  }
  def isContentCBAttribute(attr: String): Boolean = {
    attr=="ContentChangeCB.oncontentadded" || attr=="ContentChangeCB.oncontentupdated"
  }
  def isMsgArrCBAttribute(attr: String): Boolean = {
    attr=="MsgArraySuccessCB" || attr=="MsgsChangeCB.messagesadded" || attr=="MsgsChangeCB.messagesupdated" ||
    attr=="MsgsChangeCB.messagesremoved"
  }
  def isMsgConvArrCBAttribute(attr: String): Boolean = {
    attr=="MsgConvArraySuccessCB" || attr=="MsgConvsChangeCB.conversationsadded" || attr=="MsgConvsChangeCB.conversationsupdated" ||
    attr=="MsgConvsChangeCB.conversationsremoved"
  }
  def isMsgFolderArrCBAttribute(attr: String): Boolean = {
    attr=="MsgFolderArrSuccessCB" || attr=="MsgFoldersChangeCB.foldersadded" || attr=="MsgFoldersChangeCB.foldersupdated" ||
    attr=="MsgFoldersChangeCB.foldersremoved"
  }
  def isPkgInfoCBAttribute(attr: String): Boolean = {
    attr=="PkgInfoEventCB.oninstalled" || attr=="PkgInfoEventCB.onupdated"
  }
  def isReaderCBAttribute(attr: String): Boolean = {
    attr=="SEChangeListener.onSEReady" || attr=="SEChangeListener.onSENotReady"
  }
}