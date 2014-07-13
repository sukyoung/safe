/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var e1;
try {
  var contRef1 = new tizen.ContactRef();
} catch(e){
  e1 = 1;
}
var contRef2 = new tizen.ContactRef("a", "b");

var __result1 = contRef1;
var __expect1 = undefined
var __result2 = e1;
var __expect2 = 1

var __result3 = contRef2.addressBookId;
var __expect3 = "a"
var __result4 = contRef2.contactId;
var __expect4 = "b"