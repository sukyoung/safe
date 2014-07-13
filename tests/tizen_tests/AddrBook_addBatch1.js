/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4, __result5, __result6;

var addressbook;

// Define the error callback
function errorCB(err) {
  __result6 = err.name;
}

// Define the add contact success callback
function contactsAddedCB(contacts) {
  __result1 = contacts.length;
  __result2 = contacts[0].id;
  __result3 = contacts[0].name.firstName;
  __result4 = contacts[1].id;
  __result5 = contacts[1].name.firstName;
};

// Get default address book
addressbook = tizen.contact.getDefaultAddressBook();

var c1 = new tizen.Contact({name: new tizen.ContactName({firstName:'Jeffrey',
                         lastName:'Hyman',
                         nicknames:['joey ramone']}),
                         emails:[new tizen.ContactEmailAddress('user1@domain.com')],
                         phoneNumbers:[new tizen.ContactPhoneNumber('123456789')]});

var c2 = new tizen.Contact({name: new tizen.ContactName({firstName:'Elton',
                         lastName:'John',
                         nicknames:['El']}),
                         emails:[new tizen.ContactEmailAddress('user2@domain.com')],
                         phoneNumbers:[new tizen.ContactPhoneNumber('987654321')]});

addressbook.addBatch([c1, c2], contactsAddedCB, errorCB);


var __expect1 = 2;
var __expect2 = "a";
var __expect3 = "Jeffrey";
var __expect4 = "b";
var __expect5 = "Elton";
var __expect6 = "UnknownError";