/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var __result1;

var watcherId = 0; // watcher identifier
var addressbook; // This example assumes addressbook is initialized

var watcher = {
    oncontactsadded: function(contacts) {

    },
    oncontactsupdated: function(contacts) {

    },
    oncontactsremoved: function(ids) {

    }
};

// Get default address book.
addressbook = tizen.contact.getDefaultAddressBook();
// Registers to be notified when the address book changes
watcherId = addressbook.addChangeListener(watcher);
__result1 = addressbook.removeChangeListener(watcherId);

var __expect1 = undefined;
