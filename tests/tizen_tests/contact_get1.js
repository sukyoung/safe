/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var person = tizen.contact.get("1");

var __result1 = person.id;
var __expect1 = "1"
var __result2 = person.displayName;
var __expect2 = "aa"
var __result3 = person.contactCount;
var __expect3 = 1
var __result4 = person.hasPhoneNumber;
var __expect4 = true
var __result5 = person.hasEmail;
var __expect5 = true
var __result6 = person.isFavorite;
var __expect6 = true
var __result7 = person.photoURI;
var __expect7 = ""
var __result8 = person.ringtoneURI;
var __expect8 = ""
var __result9 = person.displayContactId;
var __expect9 = ""
