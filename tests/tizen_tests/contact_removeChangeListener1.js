/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var watcherId = 0; // watcher identifier

 var watcher = {
   onpersonsadded: function(persons) {
   },
   onpersonsupdated: function(persons) {
   },
   onpersonsremoved: function(ids) {
   }
 };

 // registers to be notified when the persons' changes
 watcherId = tizen.contact.addChangeListener(watcher);
var __result1 = tizen.contact.removeChangeListener(watcherId);
var __expect1 = undefined;
