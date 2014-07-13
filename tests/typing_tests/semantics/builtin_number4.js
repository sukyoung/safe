/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = 12345.123456789;

var __result1 = x.toFixed();
var __expect1 = "12345";
	
var __result2 = x.toFixed(4);
var __expect2 = "12345.1235";
		
var __result3 = x.toFixed(11);
var __expect3 = "12345.12345678900";

try {
	x.toFixed(21);
}
catch (e) {
	var __result4 = e.name;
	var __expect4 = "RangeError";
}
		  