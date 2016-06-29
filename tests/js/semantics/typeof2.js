/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x;

if (__TOP) {
	x = 1;
}
else if (__TOP) {
	x = true;
}
else {
	x = "str"
}
var __result1 = typeof x;
var __expect1 = "number";
	
var __result2 = typeof x;
var __expect2 = "boolean";
		
var __result3 = typeof x;
var __expect3 = "string";

