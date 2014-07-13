/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var x1, x2, x3;
function f(x,arguments,z) {
	x1 = x;
	x2 = arguments;
	x3 = z;
}
f(10,20,30);

var __result1 = x1;
var __result2 = x2;
var __result3 = x3;

var __expect1 = 10;
var __expect2 = 20;
var __expect3 = 30;
