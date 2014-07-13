/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var contEmail1 = new tizen.ContactEmailAddress();
var contEmail2 = new tizen.ContactEmailAddress('user@domain.com', [2, 'HOME'], true);

var __result1 = contEmail1.email;
var __expect1 = "undefined"
var __result2 = contEmail1.isDefault;
var __expect2 = false
var __result3 = contEmail1.types[0];
var __expect3 = "WORK"

var __result4 = contEmail2.email;
var __expect4 = "user@domain.com"
var __result5 = contEmail2.isDefault;
var __expect5 = true
var __result6 = contEmail2.types[0];
var __expect6 = "HOME"
var __result7 = contEmail2.types.length;
var __expect7 = 1