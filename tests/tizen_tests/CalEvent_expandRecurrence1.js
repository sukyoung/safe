/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
// Defines the error callback.
function errorCallback(response) {
  __result3 = response.name;
}

// Defines the event expanding success callback.
function eventExpandingSuccessCallback(events) {
  __result1 = events[0].startDate.getDay();
  __result2 = events[0].endDate.getDay();
}
var eventId = new tizen.CalendarEventId("a");
var calendar = tizen.calendar.getDefaultCalendar("EVENT");
var event = calendar.get(eventId);

// This is a recurring event. Expand all the instances during August 2011.
event.expandRecurrence(new tizen.TZDate(2011, 7, 1),
                      new tizen.TZDate(2011, 7, 31),
                      eventExpandingSuccessCallback,
                      errorCallback);

var __expect1 = 1;
var __expect2 = 31;
var __expect3 = "UnknownError";