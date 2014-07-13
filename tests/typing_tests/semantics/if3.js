/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var x = 123;

function f() {
	if(__TOP)
		x = 456;
	else
		return;
}

f();

var __result1 = x;
var __expect1 = 123;

var __result2 = x;
var __expect2 = 456;
