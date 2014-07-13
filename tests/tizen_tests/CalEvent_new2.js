/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var alarm1 = new tizen.CalendarAlarm(new tizen.TimeDuration(30, "MINS"), "SOUND");
var alarm2 = new tizen.CalendarAlarm(new tizen.TimeDuration(20, "MINS"), "SOUND");
var event1 = new tizen.CalendarEvent({description: "aa", summary: "aa", isAllDay: true, alarms: [alarm1, alarm2]});

var __result1 = event1.isDetached;
var __expect1 = false
var __result2 = event1.endDate;
var __expect2 = undefined
var __result3 = event1.recurrenceRule;
var __expect3 = undefined
var __result4 = event1.alarms.length;
var __expect4 = 2
var __result5 = event1.attendees.length;
var __expect5 = 0
var __result6 = event1.categories.length;
var __expect6 = 0
var __result7 = event1.availability;
var __expect7 = "BUSY"
var __result8 = event1.calendarId;
var __expect8 = null
var __result10 = event1.description;
var __expect10 = "aa"
var __result12 = event1.duration;
var __expect12 = undefined
var __result13 = event1.geolocation;
var __expect13 = undefined
var __result14 = event1.id;
var __expect14 = null
var __result15 = event1.isAllDay;
var __expect15 = true
var __result16 = event1.lastModificationDate;
var __expect16 = null
var __result17 = event1.location;
var __expect17 = ""
var __result18 = event1.organizer;
var __expect18 = ""
var __result19 = event1.priority;
var __expect19 = "LOW"
var __result21 = event1.startDate;
var __expect21 = undefined
var __result22 = event1.status;
var __expect22 = "TENTATIVE"
var __result23 = event1.summary;
var __expect23 = "aa"
var __result24 = event1.visibility;
var __expect24 = "PUBLIC"