/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var v;
var o2 = new RegExp("abc");
o2.lastIndex = 1;

if (@BoolTop) {
	v = "^(?:\\s*(<[\\w]+>)[^>]*|#([\\w-]*))$";
} else {
	v = o2;
}
var o;
o = RegExp(v);

var __result1 = o.source;
var __expect1 = "^(?:\\s*(<[\\w]+>)[^>]*|#([\\w-]*))$";
var __result2 = o.source;
var __expect2 = "abc";
var __result5 = o.lastIndex;
var __expect5 = 0;
var __result6 = o.lastIndex;
var __expect6 = 1;

