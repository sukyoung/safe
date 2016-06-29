/*******************************************************************************
Copyright (c) 2012, S-Core.
All rights reserved.

Use is subject to license terms.

This distribution may include materials developed by third parties.
******************************************************************************/

x = 10;
try {
	throw 20;
} catch (x) {
	var x = 30;
	var __result1 = x;
	var __expect1 = 30;
}

var __result2 = x;
var __expect2 = 10;
