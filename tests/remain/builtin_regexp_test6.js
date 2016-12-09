/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o;
var p, f;
if (@BoolTop) {
	o = new RegExp("(aaa)|(abc)");
} else {
	o = new RegExp("(aaa)|(abc)", "g");
}

var r = o.test("aaa");
var __result1 = r;
var __expect1 = true;

r = o.test("aabcaaa");
var __result2 = r;
var __expect2 = true;
var __result3 = o.lastIndex;
var __expect3 = 0;
var __result4 = o.lastIndex;
var __expect4 = 4;

r = o.test("ccccc");
var __result5 = r;
var __expect5 = false;
var __result6 = o.lastIndex;
var __expect6 = 0;

