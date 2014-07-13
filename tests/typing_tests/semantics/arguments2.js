/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var x1, x2, x3, x4, x5, x6, x7, x8;

function f(a) {
	x1 = a;
	x2 = arguments[0];
	x3 = arguments.length;
	x4 = arguments.callee;
}
f();

function g() {
	x5 = arguments[0];
	x6 = arguments[1];
	x7 = arguments.length;
	x8 = arguments.callee;
}
g(10,20);

var __result1 = x1;
var __result2 = x2;
var __result3 = x3;
var __result4 = x4;
var __result5 = x5;
var __result6 = x6;
var __result7 = x7;
var __result8 = x8;

var __expect1 = undefined;
var __expect2 = undefined;
var __expect3 = 0;
var __expect4 = f;
var __expect5 = 10;
var __expect6 = 20;
var __expect7 = 2;
var __expect8 = g;
