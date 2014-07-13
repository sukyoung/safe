/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var alarm1 = new tizen.CalendarAlarm(new tizen.TimeDuration(30, "MINS"), "SOUND");
var alarm2 = new tizen.CalendarAlarm(new tizen.TimeDuration(20, "MINS"), "SOUND");
var task1 = new tizen.CalendarTask({progress: 2, alarms: [alarm1, alarm2]});

var __result1 = task1.dueDate;
var __expect1 = undefined
var __result2 = task1.completedDate;
var __expect2 = undefined
var __result3 = task1.progress;
var __expect3 = 2
var __result4 = task1.alarms.length;
var __expect4 = 2
var __result5 = task1.attendees.length;
var __expect5 = 0
var __result6 = task1.categories.length;
var __expect6 = 0
var __result8 = task1.calendarId;
var __expect8 = null
var __result10 = task1.description;
var __expect10 = ""
var __result12 = task1.duration;
var __expect12 = undefined
var __result13 = task1.geolocation;
var __expect13 = undefined
var __result14 = task1.id;
var __expect14 = null
var __result15 = task1.isAllDay;
var __expect15 = false
var __result16 = task1.lastModificationDate;
var __expect16 = null
var __result17 = task1.location;
var __expect17 = ""
var __result18 = task1.organizer;
var __expect18 = ""
var __result19 = task1.priority;
var __expect19 = "LOW"
var __result21 = task1.startDate;
var __expect21 = undefined
var __result22 = task1.status;
var __expect22 = "TENTATIVE"
var __result23 = task1.summary;
var __expect23 = ""
var __result24 = task1.visibility;
var __expect24 = "PUBLIC"