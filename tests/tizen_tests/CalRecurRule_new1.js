/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
 var rule = new tizen.CalendarRecurrenceRule("DAILY");
 var rule2 = new tizen.CalendarRecurrenceRule("DAILY", {interval: 3, occurrenceCount: 1});

 var __result1 = rule.frequency;
 var __expect1 = "DAILY"
 var __result2 = rule.interval;
 var __expect2 = 1
 var __result3 = rule.occurrenceCount;
 var __expect3 = -1
 var __result4 = rule.untilDate;
 var __expect4 = null
 var __result5 = rule.daysOfTheWeek.length;
 var __expect5 = 0
 var __result6 = rule.exceptions.length;
 var __expect6 = 0
 var __result7 = rule.setPositions.length;
 var __expect7 = 0

 var __result8 = rule2.frequency;
 var __expect8 = "DAILY"
 var __result9 = rule2.interval;
 var __expect9 = 3
 var __result10 = rule2.occurrenceCount;
 var __expect10 = 1
 var __result11 = rule2.untilDate;
 var __expect11 = null
 var __result12 = rule2.daysOfTheWeek.length;
 var __expect12 = 0
 var __result13 = rule2.exceptions.length;
 var __expect13 = 0
 var __result14 = rule2.setPositions.length;
 var __expect14 = 0