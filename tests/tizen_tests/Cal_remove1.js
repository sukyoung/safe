/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var myCalendar;
  // Defines the event search success callback.
  function eventSearchSuccessCallback(events) {
    // Deletes the first two existing events.
    __result1 = myCalendar.remove(events[0].id);
  }
  function errorCallback(e){
    __result2 = e.name;
  }
  // Gets default calendar
  myCalendar = tizen.calendar.getDefaultCalendar("EVENT");

  // Retrieves all events in Calendar
  myCalendar.find(eventSearchSuccessCallback, errorCallback);

  var __expect1 = undefined;
  var __expect2 = "UnknownErrors"