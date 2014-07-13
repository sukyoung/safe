/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

// FAIL: This case fails because Function.prototype.apply modeling is incorrect.
// - current: the second parameter of apply is used as arguments object.
// - should be: new arguments object must be created.

function f() {
	arguments.x = 123;
	return arguments;
}

function g() {
	arguments.x = "ABC";
	f.apply(this, arguments);
	return arguments;
}

var arg1 = f();
var arg2 = g();

var __result1 = arg1.x;
var __expect1 = 123;

var __result2 = arg2.x;
var __expect2 = "ABC";
