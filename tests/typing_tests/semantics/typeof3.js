/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x;

if (__TOP) {
	x = {};
	var __result1 = typeof x;
	var __expect1 = "object";
}
else if (__TOP) {
	x = [];
	var __result2 = typeof x;
	var __expect2 = "object";
}
else {
	x = function () {return 1};
	var __result3 = typeof x;
	var __expect3 = "function";
}

var __result4 = typeof x;
var __expect4 = "object";

var __result5 = typeof x;
var __expect5 = "function";
