/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


var contactRef = new tizen.ContactRef("1", "2");
var addressBook = tizen.contact.getAddressBook(contactRef.addressBookId);
var group = new tizen.ContactGroup('Company');

var __result1 = group.id;
var __expect1 = null;
var __result2 = group.addressBookId;
var __expect2 = null;

addressBook.addGroup(group);

var __result3 = group.id;
var __expect3 = "a";
var __result4 = group.addressBookId;
var __expect4 = "1";

