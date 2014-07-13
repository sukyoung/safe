/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var __result1, __result2;
var contactRef = new tizen.ContactRef("1", "2");
var addressBook = tizen.contact.getAddressBook(contactRef.addressBookId);
var contact = addressBook.get(contactRef.contactId);


var __result1 = contact.id;
var __expect1 = "2";
var __result2 = contact.addressBookId;
var __expect2 = "1";