/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._

import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinArray
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap

object TIZENcalendarObj extends Tizen {
  val name = "calendar"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_calendar
  val loc_proto = newSystemRecentLoc(name + "Proto")


  val loc_timedur: Loc       = newSystemLoc("TimeDuration", Old)
  val loc_simplecoordi: Loc  = newSystemLoc("SimpleCoordinate", Old)
  val loc_cal: Loc           = newSystemLoc("Calendar", Old)
  val loc_calarr: Loc        = newSystemLoc("CalendarArr", Old)
  val loc_calitem: Loc       = newSystemLoc("CalendarItem", Old)
  val loc_calitemarr: Loc    = newSystemLoc("CalendarItemArr", Old)
  val loc_caltask: Loc       = newSystemLoc("CalendarTask", Old)
  val loc_calevent: Loc       = newSystemLoc("CalendarEvent", Old)
  val loc_caleventarr: Loc       = newSystemLoc("CalendarEventArr", Old)
  val loc_calalarm: Loc      = newSystemLoc("CalendarAlarm", Old)
  val loc_calalarmarr: Loc      = newSystemLoc("CalendarAlarmArr", Old)
  val loc_calattend: Loc     = newSystemLoc("CalendarAttendee", Old)
  val loc_calattendarr: Loc     = newSystemLoc("CalendarAttendeeArr", Old)
  val loc_calrecur: Loc      = newSystemLoc("CalendarRecurrenceRule", Old)
  val loc_contref: Loc       = newSystemLoc("ContactRef", Old)
  val loc_caleventid: Loc    = newSystemLoc("CalendarEventId", Old)
  val loc_calitemidarr: Loc    = newSystemLoc("CalendarItemIdArr", Old)
  val loc_bydayvalarr: Loc   = newSystemLoc("ByDayValueArr", Old)
  val loc_shortarr: Loc      = newSystemLoc("calShortArr", Old)
  val loc_stringarr: Loc      = newSystemLoc("calStringArr", Old)
  val loc_tzdatearr: Loc     = newSystemLoc("TZDateArr", Old)

  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T)))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("getCalendars", AbsBuiltinFunc("tizen.calendar.getCalendars",3)),
    ("getUnifiedCalendar", AbsBuiltinFunc("tizen.calendar.getUnifiedCalendar",1)),
    ("getDefaultCalendar", AbsBuiltinFunc("tizen.calendar.getDefaultCalendar",1)),
    ("getCalendar", AbsBuiltinFunc("tizen.calendar.getCalendar",2))
  )


  private val prop_timedur_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENTimeDuration.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("length",               AbsConstValue(PropValueNumTop)),
    ("unit",                 AbsConstValue(PropValue(Value(AbsString.alpha("MSECS") + AbsString.alpha("SECS") +
                                                          AbsString.alpha("MINS") + AbsString.alpha("HOURS") + AbsString.alpha("DAYS")))))
  )
  private val prop_simplecoordi_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSimpleCoordinates.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("latitude", AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T)))),
    ("longitude", AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T))))
  )

  private val prop_cal_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENCalendar.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )

  private val prop_calarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_cal), T, T, T))))
  )

  private val prop_calitem_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENCalendarItem.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("calendarId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("lastModificationDate", AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T)))),
    ("description", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("summary", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("isAllDay", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("startDate", AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T)))),
    ("duration", AbsConstValue(PropValue(ObjectValue(Value(loc_timedur), T, T, T)))),
    ("location", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("geolocation", AbsConstValue(PropValue(ObjectValue(Value(loc_simplecoordi), T, T, T)))),
    ("organizer", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("visibility", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("PUBLIC") + AbsString.alpha("PRIVATE") +
      AbsString.alpha("CONFIDENTIAL")), T, T, T)))),
    ("status", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("TENTATIVE") + AbsString.alpha("CONFIRMED") +
      AbsString.alpha("CANCELLED") + AbsString.alpha("NEEDS_ACTION") + AbsString.alpha("IN_PROCESS") +
      AbsString.alpha("COMPLETED")), T, T, T)))),
    ("priority", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("HIGH") + AbsString.alpha("MEDIUM") +
      AbsString.alpha("LOW")), T, T, T)))),
    ("alarms", AbsConstValue(PropValue(ObjectValue(Value(loc_calalarmarr), T, T, T)))),
    ("categories", AbsConstValue(PropValue(ObjectValue(Value(loc_stringarr), T, T, T)))),
    ("attendees", AbsConstValue(PropValue(ObjectValue(Value(loc_calattendarr), T, T, T))))
  )

  private val prop_caltask_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENCalendarTask.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("calendarId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("lastModificationDate", AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T)))),
    ("description", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("summary", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("isAllDay", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("startDate", AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T)))),
    ("duration", AbsConstValue(PropValue(ObjectValue(Value(loc_timedur), T, T, T)))),
    ("location", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("geolocation", AbsConstValue(PropValue(ObjectValue(Value(loc_simplecoordi), T, T, T)))),
    ("organizer", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("visibility", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("PUBLIC") + AbsString.alpha("PRIVATE") +
      AbsString.alpha("CONFIDENTIAL")), T, T, T)))),
    ("status", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("TENTATIVE") + AbsString.alpha("CONFIRMED") +
      AbsString.alpha("CANCELLED") + AbsString.alpha("NEEDS_ACTION") + AbsString.alpha("IN_PROCESS") +
      AbsString.alpha("COMPLETED")), T, T, T)))),
    ("priority", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("HIGH") + AbsString.alpha("MEDIUM") +
      AbsString.alpha("LOW")), T, T, T)))),
    ("alarms", AbsConstValue(PropValue(ObjectValue(Value(loc_calalarmarr), T, T, T)))),
    ("categories", AbsConstValue(PropValue(ObjectValue(Value(loc_stringarr), T, T, T)))),
    ("attendees", AbsConstValue(PropValue(ObjectValue(Value(loc_calattendarr), T, T, T)))),
    ("dueDate", AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T)))),
    ("completedDate", AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T)))),
    ("progress", AbsConstValue(PropValue(ObjectValue(Value(UInt), T, T, T))))
  )

  private val prop_calevent_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENCalendarEvent.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("calendarId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("lastModificationDate", AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T)))),
    ("description", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("summary", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("isAllDay", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("startDate", AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T)))),
    ("duration", AbsConstValue(PropValue(ObjectValue(Value(loc_timedur), T, T, T)))),
    ("location", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("geolocation", AbsConstValue(PropValue(ObjectValue(Value(loc_simplecoordi), T, T, T)))),
    ("organizer", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("visibility", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("PUBLIC") + AbsString.alpha("PRIVATE") +
      AbsString.alpha("CONFIDENTIAL")), T, T, T)))),
    ("status", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("TENTATIVE") + AbsString.alpha("CONFIRMED") +
      AbsString.alpha("CANCELLED") + AbsString.alpha("NEEDS_ACTION") + AbsString.alpha("IN_PROCESS") +
      AbsString.alpha("COMPLETED")), T, T, T)))),
    ("priority", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("HIGH") + AbsString.alpha("MEDIUM") +
      AbsString.alpha("LOW")), T, T, T)))),
    ("alarms", AbsConstValue(PropValue(ObjectValue(Value(loc_calalarmarr), T, T, T)))),
    ("categories", AbsConstValue(PropValue(ObjectValue(Value(loc_stringarr), T, T, T)))),
    ("attendees", AbsConstValue(PropValue(ObjectValue(Value(loc_calattendarr), T, T, T)))),
    ("isDetached", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("endDate", AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T)))),
    ("availability", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("BUSY") + AbsString.alpha("FREE")), T, T, T)))),
    ("recurrenceRule", AbsConstValue(PropValue(ObjectValue(Value(loc_calrecur), T, T, T))))
  )

  private val prop_caleventarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_calevent), T, T, T))))
  )

  private val prop_calitemarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_calitem), T, T, T))))
  )

  private val prop_calalarm_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENCalendarAlarm.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("absoluteDate", AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T)))),
    ("before", AbsConstValue(PropValue(ObjectValue(Value(loc_timedur), T, T, T)))),
    ("method", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("SOUND") + AbsString.alpha("DISPLAY")), T, T, T)))),
    ("description", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )

  private val prop_calalarmarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_calalarm), T, T, T))))
  )

  private val prop_calattend_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENCalendarAttendee.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("uri", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("role", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("REQ_PARTICIPANT") + AbsString.alpha("OPT_PARTICIPANT") +
                                          AbsString.alpha("NON_PARTICIPANT") + AbsString.alpha("CHAIR")), T, T, T)))),
    ("status", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("PENDING") + AbsString.alpha("ACCEPTED") +
                                            AbsString.alpha("DECLINED") + AbsString.alpha("TENTATIVE") +
                                            AbsString.alpha("DELEGATED") + AbsString.alpha("COMPLETED") +
                                            AbsString.alpha("IN_PROCESS")), T, T, T)))),
    ("RSVP", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("INDIVIDUAL") + AbsString.alpha("GROUP") +
                                          AbsString.alpha("RESOURCE") + AbsString.alpha("ROOM") +
                                          AbsString.alpha("UNKNOWN")), T, T, T)))),
    ("group", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("delegatorURI", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("delegateURI", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("contactRef", AbsConstValue(PropValue(ObjectValue(Value(loc_contref), T, T, T))))
  )

  private val prop_calattendarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_calattend), T, T, T))))
  )

  private val prop_calrecur_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENCalendarRecurrenceRule.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("frequency", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("DAILY") + AbsString.alpha("WEEKLY") +
                                              AbsString.alpha("MONTHLY") + AbsString.alpha("YEARLY")), T, T, T)))),
    ("interval", AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T)))),
    ("untilDate", AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T)))),
    ("occurrenceCount", AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T)))),
    ("daysOfTheWeek", AbsConstValue(PropValue(ObjectValue(Value(loc_bydayvalarr), T, T, T)))),
    ("setPositions", AbsConstValue(PropValue(ObjectValue(Value(loc_shortarr), T, T, T)))),
    ("exceptions", AbsConstValue(PropValue(ObjectValue(Value(loc_tzdatearr), T, T, T))))
  )
  private val prop_contref_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENContactRef.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("addressBookId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("contactId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )
  private val prop_bydayvalarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("MO") + AbsString.alpha("TU") +
                                                                  AbsString.alpha("WE") + AbsString.alpha("TH") +
                                                                  AbsString.alpha("FR") + AbsString.alpha("SA") +
                                                                  AbsString.alpha("SU")), T, T, T))))
  )

  private val prop_caleventid_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENCalendarEventId.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("rid", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("uid", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )

  private val prop_calitemidarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(PValue(StrTop), LocSet(loc_caleventid)), T, T, T))))
  )

  private val prop_shortarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T))))
  )
  private val prop_stringarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )

  private val prop_tzdatearr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(TIZENtime.loc_tzdate), T, T, T))))
  )
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_timedur, prop_timedur_ins),
    (loc_simplecoordi, prop_simplecoordi_ins), (loc_cal, prop_cal_ins), (loc_calarr, prop_calarr_ins),
    (loc_calalarm, prop_calalarm_ins), (loc_calalarmarr, prop_calalarmarr_ins), (loc_calattend, prop_calattend_ins),
    (loc_calattendarr, prop_calattendarr_ins), (loc_calitem, prop_calitem_ins), (loc_calitemarr, prop_calitemarr_ins),
    (loc_calrecur, prop_calrecur_ins), (loc_contref, prop_contref_ins), (loc_bydayvalarr, prop_bydayvalarr_ins),
    (loc_shortarr, prop_shortarr_ins), (loc_tzdatearr, prop_tzdatearr_ins), (loc_stringarr, prop_stringarr_ins),
    (loc_caleventid, prop_caleventid_ins), (loc_calitemidarr, prop_calitemidarr_ins), (loc_caltask, prop_caltask_ins),
    (loc_calevent, prop_calevent_ins), (loc_caleventarr, prop_caleventarr_ins)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.calendar.getCalendars" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val v_1 = getArgValue(h_2, ctx_2, args, "0")
          val v_2 = getArgValue(h_2, ctx_2, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es_1 =
            if (v_1._1._5 != AbsString.alpha("EVENT") && v_1._1._5 != AbsString.alpha("TASK"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(TIZENcalendarObj.loc_calarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("CalArrSuccessCB"), Value(v_2._2), Value(l_r1))

          val (h_5, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 => (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 3 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es_3)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.calendar.getUnifiedCalendar" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val caltype = getArgValue(h_1, ctx_1, args, "0")
          val es =
            if (caltype._1._5 != AbsString.alpha("EVENT") && caltype._1._5 != AbsString.alpha("TASK"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot

          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENCalendar.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("name", PropValue(ObjectValue(Value(StrTop), F, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, NotSupportedError, UnknownError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.calendar.getDefaultCalendar" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val caltype = getArgValue(h_1, ctx_1, args, "0")
          val es =
            if (caltype._1._5 != AbsString.alpha("EVENT") && caltype._1._5 != AbsString.alpha("TASK"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot

          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENCalendar.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("name", PropValue(ObjectValue(Value(StrTop), F, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, NotSupportedError, UnknownError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.calendar.getCalendar" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val caltype = getArgValue(h_1, ctx_1, args, "0")
          val id = getArgValue(h_1, ctx_1, args, "1")
          val es =
            if (caltype._1._5 != AbsString.alpha("EVENT") && caltype._1._5 != AbsString.alpha("TASK"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot

          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENCalendar.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(Helper.toString(id._1)), F, T, T))).
            update("name", PropValue(ObjectValue(Value(StrTop), F, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, NotSupportedError, UnknownError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((Helper.ReturnStore(h_2, Value(PValue(UndefTop), LocSet(l_r1))), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }
  override def getDefMap(): Map[String, AccessFun] = {
    Map()
  }
  override def getUseMap(): Map[String, AccessFun] = {
    Map()
  }
}

