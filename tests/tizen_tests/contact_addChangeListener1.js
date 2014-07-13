/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;

var watcherId = 0; // watcher identifier

 var watcher = {
   onpersonsadded: function(persons) {
     __result1 = persons[0].id;
   },
   onpersonsupdated: function(persons) {
     __result2 = persons[0].id;
   },
   onpersonsremoved: function(ids) {
     __result3 = ids[0];
   }
 };

 // registers to be notified when the persons' changes
 watcherId = tizen.contact.addChangeListener(watcher);

var __expect1 = "a";
var __expect2 = "a";
var __expect3 = "a";
var __result4 = watcherId;
var __expect4 = 1;