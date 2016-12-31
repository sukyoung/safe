/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var v;
var o = RegExp("^(?:\\s*(<[\\w\\W]+>)[^>]*|#([\\w-]*))$", "gim");
if (@BoolTop) {
	v = "^(?:\\s*(<[\\w]+>)[^>]*|#([\\w-]*))$";
} else {
	v = o;
}
var o_2 = RegExp(v);

var __result1 = o_2.source;
var __expect1 = "^(?:\\s*(<[\\w\\W]+>)[^>]*|#([\\w-]*))$";
var __result2 = o_2.global;
var __expect2 = true;
var __result3 = o_2.ignoreCase;
var __expect3 = true;
var __result4 = o_2.multiline;
var __expect4 = true;
var __result5 = o_2.lastIndex;
var __expect5 = 0;

var __result6 = o_2.source;
var __expect6 = "^(?:\\s*(<[\\w]+>)[^>]*|#([\\w-]*))$";

