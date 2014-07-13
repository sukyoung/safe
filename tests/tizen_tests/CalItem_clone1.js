/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
// Gets default calendar.
  var calendar = tizen.calendar.getDefaultCalendar();

  var html5seminar = new tizen.CalendarEvent({startDate: new tizen.TZDate(2012, 3, 4),
                                              duration: new tizen.TimeDuration(3, "DAYS"),
                                              summary: "HTML5 Seminar"});

  calendar.add(html5seminar);
  var tizenseminar = html5seminar.clone();


var __result1 = tizenseminar.summary;
var __expect1 = html5seminar.summary;