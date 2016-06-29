/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var __result1 = 2 | 2;
var __expect1 = 2;

var __result2 = 2 & 1;
var __expect2 = 0;

var __result3 = 5 ^ 3;
var __expect3 = 6;

var __result4 = 32 << 1;
var __expect4 = 64;

var __result5 = 16 >> 1;
var __expect5 = 8;

var __result6 = 32 >>> 1;
var __expect6 = 16;

var __result7 = 111 + 123;
var __expect7 = 234;

var __result8 = 111 - 1;
var __expect8 = 110;

var __result9 = 11 * 11;
var __expect9 = 121;

var __result10 = 36 / 6;
var __expect10 = 6;

var __result11 = 207 % 200;
var __expect11 = 7;

var __result12 = 1 == "1";
var __expect12 = true;

var __result13 = 2 != "2";
var __expect13 = false;

var __result14 = 3 === "3";
var __expect14 = false;

var __result15 = 4 !== "4";
var __expect15 = true;

var __result16 = 5 < 5;
var __expect16 = false;

var __result17 = 5 < 6;
var __expect17 = true;

var __result18 = 6 > 6;
var __expect18 = false;

var __result19 = 6 > 5;
var __expect19 = true;

var __result20 = 7 <= 7;
var __expect20 = true;

var __result21 = 7 <= 8;
var __expect21 = true;

var __result22 = 8 <= 7;
var __expect22 = false;

var __result23 = 8 >= 7;
var __expect23 = true;

var __result24 = 8 >= 8;
var __expect24 = true;

var __result25 = 8 >= 9;
var __expect25 = false;
