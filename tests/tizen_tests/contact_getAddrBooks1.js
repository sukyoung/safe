/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function addressBooksCB(addressbooks) {
  __result1 = addressbooks[0].name;
}
function errorCB(e){
  __result2 = e.name;
}

tizen.contact.getAddressBooks(addressBooksCB, errorCB);


var __expect1 = "aa";
var __expect2 = "UnknownError";