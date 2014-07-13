/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var addrbook = tizen.contact.getAddressBook("1");

var __result1 = addrbook.id;
var __expect1 = "1"
var __result2 = addrbook.name;
var __expect2 = "aa"
var __result3 = addrbook.readOnly;
var __expect3 = false