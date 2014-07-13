/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;

var addressbook;

// Define the error callback for all the asynchronous calls
function errorCB(err) {
  __result2 = err.name;
}

function contactsRemovedCB() {
  __result1 = 1;
}

function contactsFoundCB(contacts) {
// The contact has been successfully found
    if(contacts.length > 2) {
       addressbook.removeBatch([contacts[0].id, contacts[1].id], contactsRemovedCB, errorCB);
    }
}

// Get default address book.
addressbook = tizen.contact.getDefaultAddressBook();

var filter = new tizen.AttributeFilter('name.firstName', 'CONTAINS', 'Chris');
addressbook.find(contactsFoundCB, errorCB, filter);

var __expect1 = 1;
var __expect2 = "NotFondError";
