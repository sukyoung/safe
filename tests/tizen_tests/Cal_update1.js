/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


  // Gets the default calendar
  var calendar = tizen.calendar.getDefaultCalendar("EVENT");

  var ev = new tizen.CalendarEvent({description:'HTML5 Introduction',
                                   summary:'HTML5 Webinar ',
                                   startDate: new tizen.TZDate(2011, 3, 30, 10, 0),
                                   duration: new tizen.TimeDuration(1, "HOURS"),
                                   location:'Huesca'});

  var __result1 = calendar.update(ev);
  var __expect1 = undefined;