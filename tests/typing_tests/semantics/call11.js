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
	}
}
function bar() {
	try {
		return 20;
	} finally {
		x3 = qqq();
	}
}
function qqq() {
	return 30;
}

x1 = foo();

var __result1 = x1;
var __expect1 = 10;
var __result2 = x2;
var __expect2 = 20;
var __result3 = x3;
var __expect3 = 30;

