/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var v;
if (@BoolTop) {
	v = "^(?:\\s*(<[\\w]+>)[^>]*|#([\\w-]*))$";
} else {
	v = "^(?:\\s*(<[\\w]+>)[^>]*|#([\\w-]*)$"; // syntax error
}
var o;
try {
	o = RegExp(v);
} catch(e) {
	__result6 = e.name;
}

var __result1 = o.source;
var __expect1 = "^(?:\\s*(<[\\w]+>)[^>]*|#([\\w-]*))$";
var __result2 = o.global;
var __expect2 = false;
var __result3 = o.ignoreCase;
var __expect3 = false;
var __result4 = o.multiline;
var __expect4 = false;
var __result5 = o.lastIndex;
var __expect5 = 0;

var __expect6 = "SyntaxError";

