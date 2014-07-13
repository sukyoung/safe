/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function onSuccess(results) {
  __result1 = results[0].uid; // process the CallHistoryEntry
}

 // Defines error callback
function onError(error) {
    __result2 = error.name;
}

// Defines filter: list CS calls, most recent first
var filter = new tizen.AttributeFilter("type", "EXACTLY", "TEL");

// Defines sort mode: descending on call start time.
var sortMode = new tizen.SortMode("startTime", "DESC");

// Makes the query and wire up the callbacks
tizen.callhistory.find(onSuccess,
                     onError,
                     filter,
                     sortMode);

var __expect1 = "a"
var __expect2 = "UnknownError"