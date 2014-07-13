/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var contact = new tizen.Contact({photoURI:"http://tizen.org/photo.jpg"});
var a = contact.clone();

var __result1 = a.id;
var __expect1 = null
var __result2 = a.personId;
var __expect2 = contact.personId;
var __result3 = a.addressBookId;
var __expect3 = contact.addressBookId;