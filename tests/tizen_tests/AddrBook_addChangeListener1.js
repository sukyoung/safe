/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var __result1, __result2, __result3;

var watcherId = 0; // watcher identifier
var addressbook; // This example assumes addressbook is initialized

var watcher = {
    oncontactsadded: function(contacts) {
        __result1 = contacts[0].id;
    },
    oncontactsupdated: function(contacts) {
        __result2 = contacts[0].id;
    },
    oncontactsremoved: function(ids) {
        __result3 = ids[0];
    }
};

// Get default address book.
addressbook = tizen.contact.getDefaultAddressBook();
// Registers to be notified when the address book changes
watcherId = addressbook.addChangeListener(watcher);

var __expect1 = "a";
var __expect2 = "a";
var __expect3 = "a";
var __result4 = watcherId;
var __expect4 = 1;
