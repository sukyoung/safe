/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = 12345.123456789;

var __result1 = x.toExponential();
var __expect1 = "1.2345123456789e+4";
	
var __result2 = x.toExponential(4);
var __expect2 = "1.2345e+4";
		
var __result3 = x.toExponential(11);
var __expect3 = "1.234512345678900e+4";

try {
	x.toExponential(21);
}
catch (e) {
	var __result4 = e.name;
	var __expect4 = "RangeError";
}
		  