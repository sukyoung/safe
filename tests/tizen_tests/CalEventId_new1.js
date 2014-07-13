/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var e1 = new tizen.CalendarEventId();
var e2 = new tizen.CalendarEventId("a");
var e3 = new tizen.CalendarEventId("a", null);
var e4 = new tizen.CalendarEventId("a", "b");

var __result1 = e1.uid;
var __expect1 = ""
var __result2 = e1.rid;
var __expect2 = "0"
var __result3 = e2.uid;
var __expect3 = "a"
var __result4 = e2.rid;
var __expect4 = "0"
var __result5 = e3.uid;
var __expect5 = "a"
var __result6 = e3.rid;
var __expect6 = "null"
var __result7 = e4.uid;
var __expect7 = "a"
var __result8 = e4.rid;
var __expect8 = "b"