/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var y = new Date();

var __result1 = y.setTime(0);
var __expect1 = 0;

var __result2 = y.setUTCFullYear(2012);
var __expect2 = 1325376000000
var __result3 = y.getUTCFullYear();
var __expect3 = 2012;

var __result4 = y.setUTCMonth(9);
var __expect4 = 1349049600000;
var __result5 = y.getUTCMonth();
var __expect5 = 9;

var __result6 = y.setUTCDate(30);
var __expect6 = 1351555200000;
var __result7 = y.getUTCDate();
var __expect7 = 30;

var __result9 = y.getUTCDay();
var __expect9 = 2;

var __result10 = y.setUTCHours(8);
var __expect10 = 1351584000000;
var __result11 = y.getUTCHours();
var __expect11 = 8;

var __result12 = y.setUTCMinutes(46);
var __expect12 = 1351586760000;
var __result13 = y.getUTCMinutes();
var __expect13 = 46;

var __result14 = y.setUTCSeconds(10);
var __expect14 = 1351586770000;
var __result15 = y.getUTCSeconds();
var __expect15 = 10;

var __result16 = y.setUTCMilliseconds(791);
var __expect16 = 1351586770791;
var __result17 = y.getUTCMilliseconds();
var __expect17 = 791;
