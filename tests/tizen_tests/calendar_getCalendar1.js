/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var defcal = tizen.calendar.getDefaultCalendar("EVENT");
var calendar = tizen.calendar.getCalendar("EVENT", defcal.id);

var __result1 = calendar.id;
var __expect1 = defcal.id
var __result2 = calendar.name;
var __expect2 = defcal.name