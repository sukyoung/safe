/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


var x = 0;
function foo() {
	x = 1;
	var x;
	y = 2;
}

y = 0;

foo();

var __result1 = x;
var __expect1 = 0;
var __result2 = y;
var __expect2 = 2;

