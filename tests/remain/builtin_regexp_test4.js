/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o;
var p, f;
if (@BoolTop) {
	p = "^(?:\\s*(<[\\w\\W]+>)[^>]*|#([\\w-]*))$";
	f = "";
} else {
	p = "(aaa)|(abc)";
	f = "g";
}
o = new RegExp(p, f);

var r = o.test("#abc");
var __result1 = r;
var __expect1 = true;

var r = o.test("aabcaaa");
var __result2 = r;
var __expect2 = true;
var __result3 = o.lastIndex;
var __expect3 = 4;

