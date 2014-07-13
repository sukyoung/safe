/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var calendar = tizen.calendar.getCalendar("EVENT", "m");
var event1 = new tizen.CalendarEvent({description: "HTML5", summary: "HTML5 webinar"});
var event2 = new tizen.CalendarTask({description: "HTML5", summary: "HTML5 webinar"});
calendar.add(event1);
calendar.add(event2);

var __result1 = event1.id.uid;
var __expect1 = "aaaaa"
var __result2 = event2.id;
var __expect2 = "aaaaa"
