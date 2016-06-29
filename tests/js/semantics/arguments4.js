/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var x1, x2, x3, x4, x5, x6;
function f(a) {
	x1 = delete arguments[0];
	x2 = delete arguments.length;
	x3 = delete arguments.callee;
}
f(10);

function g(b) {
	arguments[0] = "A";
	x4 = arguments[0];

	arguments.length = "B";
	x5 = arguments.length;
	
	arguments.callee = "C";
	x6 = arguments.callee;
}
g(20);

var __result1 = x1;
var __result2 = x2;
var __result3 = x3;
var __result4 = x4;
var __result5 = x5;
var __result6 = x6;

var __expect1 = true;
var __expect2 = true;
var __expect3 = true;
var __expect4 = "A";
var __expect5 = "B";
var __expect6 = "C";
