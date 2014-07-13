/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinDate


object TIZENtizen extends Tizen {
  private val name = "tizen"
  /* predefined property locations */
  val loc_obj = newSystemRecentLoc(name + "Obj")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_alarm = newSystemRecentLoc(name + "alarm")
  val loc_application = newSystemRecentLoc(name + "application")
  val loc_bluetooth = newSystemRecentLoc(name + "bluetooth")
  val loc_bookmark = newSystemRecentLoc(name + "bookmark")
  val loc_calendar = newSystemRecentLoc(name + "calendar")
  val loc_callhistory = newSystemRecentLoc(name + "callhistory")
  val loc_contact = newSystemRecentLoc(name + "contact")
  val loc_content = newSystemRecentLoc(name + "content")
  val loc_datacontrol = newSystemRecentLoc(name + "datacontrol")
  val loc_datasync = newSystemRecentLoc(name + "datasync")
  val loc_download = newSystemRecentLoc(name + "download")
  val loc_filesystem = newSystemRecentLoc(name + "filesystem")
  val loc_messageport = newSystemRecentLoc(name + "messageport")
  val loc_messaging = newSystemRecentLoc(name + "messaging")
  val loc_networkbearerselection = newSystemRecentLoc(name + "networkbearerselection")
  val loc_nfc = newSystemRecentLoc(name + "nfc")
  val loc_notification = newSystemRecentLoc(name + "notification")
  val loc_package = newSystemRecentLoc(name + "package")
  val loc_power = newSystemRecentLoc(name + "power")
  val loc_push = newSystemRecentLoc(name + "push")
  val loc_seService = newSystemRecentLoc(name + "seService")
  val loc_systeminfo = newSystemRecentLoc(name + "systeminfo")
  val loc_systemsetting = newSystemRecentLoc(name + "systemsetting")
  val loc_time = newSystemRecentLoc(name + "time")

  /* predefined error locations */
  val loc_notFounderr: Loc = newSystemLoc("NotFoundError", Old)
  val loc_unknownerr: Loc = newSystemLoc("UnknownError", Old)
  val loc_invalidValueserr: Loc = newSystemLoc("InvalidValuesError", Old)
  val loc_IOerr: Loc = newSystemLoc("IOError", Old)
  val loc_serviceNotAvailableerr: Loc = newSystemLoc("ServiceNotAvailableError", Old)
  val loc_typemismatcherr: Loc = newSystemLoc("TypeMismatchError", Old)
  val loc_networkerr: Loc = newSystemLoc("NetworkError", Old)
  val loc_aborterr: Loc = newSystemLoc("AbortError", Old)
  val loc_securityerr: Loc = newSystemLoc("SecurityError", Old)
  val loc_notSupportederr: Loc = newSystemLoc("NotSupportedError", Old)

  /* predefined instance locations */
  val loc_date: Loc = newSystemLoc("DateObj", Old)

  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class",                      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",                      AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@hasinstance",                AbsConstValue(PropValueNullTop)),
    ("AlarmAbsolute",          AbsConstValue(PropValue(ObjectValue(Value(TIZENAlarmAbsolute.loc_cons), F, F, F)))),
    ("AlarmRelative",          AbsConstValue(PropValue(ObjectValue(Value(TIZENAlarmRelative.loc_cons), F, F, F)))),
    ("ApplicationControl",     AbsConstValue(PropValue(ObjectValue(Value(TIZENApplicationControl.loc_cons), F, F, F)))),
    ("ApplicationControlData", AbsConstValue(PropValue(ObjectValue(Value(TIZENApplicationControlData.loc_cons), F, F, F)))),
    ("AttributeFilter",        AbsConstValue(PropValue(ObjectValue(Value(TIZENAttributeFilter.loc_cons), F, F, F)))),
    ("AttributeRangeFilter",   AbsConstValue(PropValue(ObjectValue(Value(TIZENAttributeRangeFilter.loc_cons), F, F, F)))),
    ("BookmarkFolder",         AbsConstValue(PropValue(ObjectValue(Value(TIZENBookmarkFolder.loc_cons), F, F, F)))),
    ("BookmarkItem",           AbsConstValue(PropValue(ObjectValue(Value(TIZENBookmarkItem.loc_cons), F, F, F)))),
    ("CalendarAlarm",          AbsConstValue(PropValue(ObjectValue(Value(TIZENCalendarAlarm.loc_cons), F, F, F)))),
    ("CalendarAttendee",       AbsConstValue(PropValue(ObjectValue(Value(TIZENCalendarAttendee.loc_cons), F, F, F)))),
    ("CalendarEvent",          AbsConstValue(PropValue(ObjectValue(Value(TIZENCalendarEvent.loc_cons), F, F, F)))),
    ("CalendarEventId",        AbsConstValue(PropValue(ObjectValue(Value(TIZENCalendarEventId.loc_cons), F, F, F)))),
    ("CalendarRecurrenceRule", AbsConstValue(PropValue(ObjectValue(Value(TIZENCalendarRecurrenceRule.loc_cons), F, F, F)))),
    ("CalendarTask",           AbsConstValue(PropValue(ObjectValue(Value(TIZENCalendarTask.loc_cons), F, F, F)))),
    ("CompositeFilter",        AbsConstValue(PropValue(ObjectValue(Value(TIZENCompositeFilter.loc_cons), F, F, F)))),
    ("Contact",                AbsConstValue(PropValue(ObjectValue(Value(TIZENContact.loc_cons), F, F, F)))),
    ("ContactAddress",         AbsConstValue(PropValue(ObjectValue(Value(TIZENContactAddress.loc_cons), F, F, F)))),
    ("ContactAnniversary",     AbsConstValue(PropValue(ObjectValue(Value(TIZENContactAnniversary.loc_cons), F, F, F)))),
    ("ContactEmailAddress",    AbsConstValue(PropValue(ObjectValue(Value(TIZENContactEmailAddress.loc_cons), F, F, F)))),
    ("ContactGroup",           AbsConstValue(PropValue(ObjectValue(Value(TIZENContactGroup.loc_cons), F, F, F)))),
    ("ContactName",            AbsConstValue(PropValue(ObjectValue(Value(TIZENContactName.loc_cons), F, F, F)))),
    ("ContactOrganization",    AbsConstValue(PropValue(ObjectValue(Value(TIZENContactOrganization.loc_cons), F, F, F)))),
    ("ContactPhoneNumber",     AbsConstValue(PropValue(ObjectValue(Value(TIZENContactPhoneNumber.loc_cons), F, F, F)))),
    ("ContactRef",             AbsConstValue(PropValue(ObjectValue(Value(TIZENContactRef.loc_cons), F, F, F)))),
    ("ContactWebSite",         AbsConstValue(PropValue(ObjectValue(Value(TIZENContactWebSite.loc_cons), F, F, F)))),
    ("DownloadRequest",        AbsConstValue(PropValue(ObjectValue(Value(TIZENDownloadRequest.loc_cons), F, F, F)))),
    ("Message",                AbsConstValue(PropValue(ObjectValue(Value(TIZENMessage.loc_cons), F, F, F)))),
    ("MessageAttachment",      AbsConstValue(PropValue(ObjectValue(Value(TIZENMessageAttachment.loc_cons), F, F, F)))),
    ("NDEFMessage",            AbsConstValue(PropValue(ObjectValue(Value(TIZENNDEFMessage.loc_cons), F, F, F)))),
    ("NDEFRecord",             AbsConstValue(PropValue(ObjectValue(Value(TIZENNDEFRecord.loc_cons), F, F, F)))),
    ("NDEFRecordMedia",        AbsConstValue(PropValue(ObjectValue(Value(TIZENNDEFRecordMedia.loc_cons), F, F, F)))),
    ("NDEFRecordText",         AbsConstValue(PropValue(ObjectValue(Value(TIZENNDEFRecordText.loc_cons), F, F, F)))),
    ("NDEFRecordURI",          AbsConstValue(PropValue(ObjectValue(Value(TIZENNDEFRecordURI.loc_cons), F, F, F)))),
    ("NotificationDetailInfo", AbsConstValue(PropValue(ObjectValue(Value(TIZENNotificationDetailInfo.loc_cons), F, F, F)))),
    ("SimpleCoordinates",      AbsConstValue(PropValue(ObjectValue(Value(TIZENSimpleCoordinates.loc_cons), F, F, F)))),
    ("SortMode",               AbsConstValue(PropValue(ObjectValue(Value(TIZENSortMode.loc_cons), F, F, F)))),
    ("StatusNotification",     AbsConstValue(PropValue(ObjectValue(Value(TIZENStatusNotification.loc_cons), F, F, F)))),
    ("SyncInfo",               AbsConstValue(PropValue(ObjectValue(Value(TIZENSyncInfo.loc_cons), F, F, F)))),
    ("SyncProfileInfo",        AbsConstValue(PropValue(ObjectValue(Value(TIZENSyncProfileInfo.loc_cons), F, F, F)))),
    ("SyncServiceInfo",        AbsConstValue(PropValue(ObjectValue(Value(TIZENSyncServiceInfo.loc_cons), F, F, F)))),
    ("TZDate",                 AbsConstValue(PropValue(ObjectValue(Value(TIZENTZDate.loc_cons), F, F, F)))),
    ("TimeDuration",           AbsConstValue(PropValue(ObjectValue(Value(TIZENTimeDuration.loc_cons), F, F, F)))),
    ("alarm",                  AbsConstValue(PropValue(ObjectValue(Value(loc_alarm), F, F, F)))),
    ("application",            AbsConstValue(PropValue(ObjectValue(Value(loc_application), F, F, F)))),
    ("bluetooth",              AbsConstValue(PropValue(ObjectValue(Value(loc_bluetooth), F, F, F)))),
    ("bookmark",               AbsConstValue(PropValue(ObjectValue(Value(loc_bookmark), F, F, F)))),
    ("calendar",               AbsConstValue(PropValue(ObjectValue(Value(loc_calendar), F, F, F)))),
    ("callhistory",            AbsConstValue(PropValue(ObjectValue(Value(loc_callhistory), F, F, F)))),
    ("contact",                AbsConstValue(PropValue(ObjectValue(Value(loc_contact), F, F, F)))),
    ("content",                AbsConstValue(PropValue(ObjectValue(Value(loc_content), F, F, F)))),
    ("datacontrol",            AbsConstValue(PropValue(ObjectValue(Value(loc_datacontrol), F, F, F)))),
    ("datasync",               AbsConstValue(PropValue(ObjectValue(Value(loc_datasync), F, F, F)))),
    ("download",               AbsConstValue(PropValue(ObjectValue(Value(loc_download), F, F, F)))),
    ("filesystem",             AbsConstValue(PropValue(ObjectValue(Value(loc_filesystem), F, F, F)))),
    ("messageport",            AbsConstValue(PropValue(ObjectValue(Value(loc_messageport), F, F, F)))),
    ("messaging",              AbsConstValue(PropValue(ObjectValue(Value(loc_messaging), F, F, F)))),
    ("networkbearerselection", AbsConstValue(PropValue(ObjectValue(Value(loc_networkbearerselection), F, F, F)))),
    ("nfc",                    AbsConstValue(PropValue(ObjectValue(Value(loc_nfc), F, F, F)))),
    ("notification",           AbsConstValue(PropValue(ObjectValue(Value(loc_notification), F, F, F)))),
    ("package",                  AbsConstValue(PropValue(ObjectValue(Value(loc_package), F, F, F)))),
    ("power",                  AbsConstValue(PropValue(ObjectValue(Value(loc_power), F, F, F)))),
    ("push",                  AbsConstValue(PropValue(ObjectValue(Value(loc_push), F, F, F)))),
    ("seService",             AbsConstValue(PropValue(ObjectValue(Value(loc_seService), F, F, F)))),
    ("systeminfo",             AbsConstValue(PropValue(ObjectValue(Value(loc_systeminfo), F, F, F)))),
    ("systemsetting",          AbsConstValue(PropValue(ObjectValue(Value(loc_systemsetting), F, F, F)))),
    ("time",                   AbsConstValue(PropValue(ObjectValue(Value(loc_time), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",                      AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",                      AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T)))
  )
  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name,       AbsConstValue(PropValue(ObjectValue(Value(loc_obj), F, F, F))))
  )

  private val prop_notFounderr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENWebAPIError.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("code",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("NotFoundError")), F, T, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )
  private val prop_unknownerr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENWebAPIError.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("code",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("UnknownError")), F, T, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )
  private val prop_invalidValueserr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENWebAPIError.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("code",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("InvalidValuesError")), F, T, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )
  private val prop_IOerr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENWebAPIError.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("code",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("IOError")), F, T, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )
  private val prop_serviceNotAvailableerr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENWebAPIError.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("code",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("ServiceNotAvailableError")), F, T, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )
  private val prop_networkerr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENWebAPIError.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("code",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("NetworkError")), F, T, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )
  private val prop_typemismatcherr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENWebAPIError.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("code",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("TypeMismatchError")), F, T, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )
  private val prop_aborterr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENWebAPIError.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("code",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("AbortError")), F, T, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )
  private val prop_securityerr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENWebAPIError.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("code",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("SecurityError")), F, T, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )
  private val prop_notSupportederr_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENWebAPIError.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("code",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("NotSupportedError")), F, T, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_date_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(BuiltinDate.ProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (GlobalLoc, prop_global),
    (loc_date, prop_date_ins), (loc_notFounderr, prop_notFounderr_ins), (loc_unknownerr, prop_unknownerr_ins), (loc_invalidValueserr, prop_invalidValueserr_ins),
    (loc_IOerr, prop_IOerr_ins), (loc_serviceNotAvailableerr, prop_serviceNotAvailableerr_ins), (loc_networkerr, prop_networkerr_ins),
    (loc_typemismatcherr, prop_typemismatcherr_ins), (loc_aborterr, prop_aborterr_ins), (loc_securityerr, prop_securityerr_ins),
    (loc_notSupportederr, prop_notSupportederr_ins)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}