/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var contact = new tizen.Contact({photoURI:"http://tizen.org/photo.jpg"});
var a = contact.convertToString("VCARD_30");

var __result1 = a;
var __expect1 = "PHOTO: http://tizen.org/photo.jpg"