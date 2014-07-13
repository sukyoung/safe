/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var event = new tizen.CalendarEvent({description: "a"});
var str = event.convertToString("ICALENDAR_20");

var __result1 = str;
var __expect1 = "bbb"