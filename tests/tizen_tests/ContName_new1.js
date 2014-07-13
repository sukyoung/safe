/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var contName1 = new tizen.ContactName();
var contName2 = new tizen.ContactName({firstName: "Youngjoo", lastName: "Kim", nicknames: ["circle"]});

var __result1 = contName1.prefix;
var __expect1 = null
var __result2 = contName1.suffix;
var __expect2 = null
var __result3 = contName1.firstName;
var __expect3 = null
var __result4 = contName1.middleName;
var __expect4 = null
var __result5 = contName1.lastName;
var __expect5 = null
var __result6 = contName1.nicknames.length;
var __expect6 = 0
var __result7 = contName1.phoneticFirstName;
var __expect7 = null
var __result8 = contName1.phoneticLastName;
var __expect8 = null


var __result9 = contName2.prefix;
var __expect9 = null
var __result10 = contName2.suffix;
var __expect10 = null
var __result11 = contName2.firstName;
var __expect11 = "Youngjoo"
var __result12 = contName2.middleName;
var __expect12 = null
var __result13 = contName2.lastName;
var __expect13 = "Kim"
var __result14 = contName2.nicknames.length;
var __expect14 = 1
var __result15 = contName2.nicknames[0];
var __expect15 = "circle"
var __result16 = contName2.phoneticFirstName;
var __expect16 = null
var __result17 = contName2.phoneticLastName;
var __expect17 = null