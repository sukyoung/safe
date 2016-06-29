/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var x1, x2, x3, x4, x5, x6, x7, x8, x9;

function f(a,b,c) {
	x1 = arguments[0];
	x2 = arguments[1];
	x3 = arguments[2];
	x4 = arguments[3];
	x5 = arguments.length;
	x6 = arguments.callee;
	x7 = a;
	x8 = b;
	x9 = c;
}
f(10,20,30);

var __result1 = x1;
var __result2 = x2;
var __result3 = x3;
var __result4 = x4;
var __result5 = x5;
var __result6 = x6;
var __result7 = x7;
var __result8 = x8;
var __result9 = x9;

var __expect1 = 10;
var __expect2 = 20;
var __expect3 = 30;
var __expect4 = undefined;
var __expect5 = 3;
var __expect6 = f;
var __expect7 = 10;
var __expect8 = 20;
var __expect9 = 30;
