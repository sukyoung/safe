/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x1, x2, x3;

function foo() {
	try {
		return 10;
	} finally {
		x2 = bar();
		return 1;
	}
}
function bar() {
	try {
		return 20;
	} finally {
		x3 = qqq();
		return 2;
	}
}
function qqq() {
	return 3;
}

x1 = foo();

var __result1 = x1;
var __expect1 = 1;
var __result2 = x2;
var __expect2 = 2;
var __result3 = x3;
var __expect3 = 3;

