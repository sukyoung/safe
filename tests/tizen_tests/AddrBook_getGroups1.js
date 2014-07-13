/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


var contactRef = new tizen.ContactRef("1", "2");
var addressBook = tizen.contact.getAddressBook(contactRef.addressBookId);
var groups = addressBook.getGroups();
var __result1 = groups.length;
var __expect1 = 2;
var __result2 = groups[0].name;
var __expect2 = "a";

