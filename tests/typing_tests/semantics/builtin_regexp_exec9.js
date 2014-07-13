/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o;
o = new RegExp("(aaa)|(abc)","g");

var r = o.exec("aaaaaa");
var __result1 = r[0];
var __expect1 = "aaa";
var __result2 = r[1];
var __expect2 = "aaa";
var __result3 = r[2];
var __expect3 = undefined;
var __result4 = r.index;
var __expect4 = 0;
var __result5 = r.input;
var __expect5 = "aaaaaa";
var __result6 = r.length;
var __expect6 = 3;
var __result7 = o.lastIndex;
var __expect7 = 3;

r = o.exec("aaabaaa");
var __result8 = r[0];
var __expect8 = "aaa";
var __result9 = r[1];
var __expect9 = "aaa";
var __result10 = r[2];
var __expect10 = undefined;
var __result11 = r.index;
var __expect11 = 4;
var __result12 = r.input;
var __expect12 = "aaabaaa";
var __result13 = r.length;
var __expect13 = 3;
var __result14 = o.lastIndex;
var __expect14 = 7;

r = o.exec("aaabcccccc");
var __result14 = r;
var __expect14 = null;
var __result15 = o.lastIndex;
var __expect15 = 0;

