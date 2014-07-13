/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var myCalendar;
var watcher = {
    onitemsadded: function(items) {
    },
    onitemsupdated: function(items) {
    },
    onitemsremoved: function(ids) {
    }
  };

  // Gets default calendar
  myCalendar = tizen.calendar.getDefaultCalendar("EVENT");

  // Registers to be notified when the calendar changes
  var watchId = myCalendar.addChangeListener(watcher);
  var __result1 = myCalendar.removeChangeListener(watchId);
  var __expect1 = undefined;
