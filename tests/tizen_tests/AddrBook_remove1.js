/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
var addressbook;

// Define the error callback
function errorCB(err) {

}

function contactsFoundCB(contacts) {
    // The contact has been successfully found
    if(contacts.length > 0) {
        __result1 = addressbook.remove(contacts[0].id);
    }
}

// Get default address book.
addressbook = tizen.contact.getDefaultAddressBook();

var filter = new tizen.AttributeFilter('name.firstName', 'CONTAINS', 'Chris');
addressbook.find(contactsFoundCB, errorCB, filter);

var __expect1 = undefined;