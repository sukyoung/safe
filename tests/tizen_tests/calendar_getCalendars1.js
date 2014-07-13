/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
// Defines the error callback for all the asynchronous calls
function errorCallback(response) {
   __result2 = response.name;
}

// Defines the success callback for retrieving the list of calendars
function calendarListCallback(calendars) {
   __result1 = calendars[0].name;
}

// Gets a list of available calendars
tizen.calendar.getCalendars("EVENT", calendarListCallback, errorCallback);

var __expect1 = "aa"
var __expect2 = "Error"