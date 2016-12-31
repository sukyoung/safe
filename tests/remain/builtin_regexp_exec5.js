/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o;
var p, f;
if (@BoolTop) {
	p = "(aaa)|(abc)";
	f = "";
} else {
	p = "(aaa)|(abc)";
	f = "g";
}
o = new RegExp(p, f);

var r = o.exec("aaa");
var __result1 = r[0];
var __expect1 = "aaa";
var __result2 = r[1];
var __expect2 = "aaa";
var __result3 = r[2];
var __expect3 = undefined;
var __result4 = r.index;
var __expect4 = 0;
var __result5 = r.input;
var __expect5 = "aaa";
var __result6 = r.length;
var __expect6 = 3;

var r = o.exec("aabcaaa");
var __result7 = r[0];
var __expect7 = "abc";
var __result8 = r[1];
var __expect8 = undefined;
var __result9 = r[2];
var __expect9 = "abc";
var __result10 = r.index;
var __expect10 = 1;
var __result11 = r.input;
var __expect11 = "aabcaaa";
var __result12 = r.length;
var __expect12 = 3;
var __result13 = o.lastIndex;
var __expect13 = 4;

