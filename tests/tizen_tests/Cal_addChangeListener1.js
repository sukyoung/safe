/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
var watcher = {
    onitemsadded: function(items) {
      __result1 = items.length;
    },
    onitemsupdated: function(items) {
      __result2 = items.length;
    },
    onitemsremoved: function(ids) {
      __result3 = ids.length;
    }
  };

  // Gets default calendar
  myCalendar = tizen.calendar.getDefaultCalendar("EVENT");

  // Registers to be notified when the calendar changes
  myCalendar.addChangeListener(watcher);

  var __expect1 = 1;
  var __expect2 = 1;
  var __expect3 = 1;