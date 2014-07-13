/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var itemId = new tizen.CalendarEventId("aaa");
var itemId2 = "1";
var myCalendar = tizen.calendar.getDefaultCalendar("EVENT");
var item = myCalendar.get(itemId);
var item2 = myCalendar.get(itemId2);

var __result1 = item.isDetached;
var __expect1 = false;
var __result2 = item2.progress;
var __expect2 = 1;