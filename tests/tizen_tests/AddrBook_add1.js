/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var addressbook;

// Get default address book
addressbook = tizen.contact.getDefaultAddressBook();

var contact = new tizen.Contact({name: new tizen.ContactName({firstName:'Jeffrey',
                                      lastName:'Hyman',
                                      nicknames:['joey ramone']}),
                                emails:[new tizen.ContactEmailAddress('user@domain.com')],
                                phoneNumbers:[new tizen.ContactPhoneNumber('123456789')]});

var __result1 = contact.id;
var __expect1 = null;

addressbook.add(contact);



var __result2 = contact.id;
var __expect2 = "2";
