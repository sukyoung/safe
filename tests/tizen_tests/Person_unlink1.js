/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var myPersonId = '1'; //ID of modified
var __result1;
var person;    // Existing person obtained from addressbook
var newPerson; // New person, which will be created during unlink

var addressbook;

// Define the error callback for all the asynchronous calls
function errorCB(err) {
}


function contactsFoundCB(contacts) {
  newPerson = person.unlink(contacts[0].id);
  __result1 = newPerson.contactCount;
}

// Get person
person = tizen.contact.get(myPersonId);
// Get default address book.
addressbook = tizen.contact.getDefaultAddressBook();

var filter = new tizen.AttributeFilter('name.firstName', 'CONTAINS', 'Chris');
addressbook.find(contactsFoundCB, errorCB, filter);




var __expect1 = 1;
