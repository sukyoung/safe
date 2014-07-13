/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var contPhone1 = new tizen.ContactPhoneNumber();
var contPhone2 = new tizen.ContactPhoneNumber("02-7125-5302", ["WORK", 2, "VOICE"], true);

var __result1 = contPhone1.number;
var __expect1 = "undefined"
var __result2 = contPhone1.isDefault;
var __expect2 = false
var __result3 = contPhone1.types[0];
var __expect3 = "VOICE"

var __result4 = contPhone2.number;
var __expect4 = "02-7125-5302"
var __result5 = contPhone2.isDefault;
var __expect5 = true
var __result6 = contPhone2.types[0];
var __expect6 = "WORK"
var __result7 = contPhone2.types[1];
var __expect7 = "VOICE"