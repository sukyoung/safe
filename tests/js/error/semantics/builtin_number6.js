/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = 12345.123456789;

var __result1 = x.toPrecision();
var __expect1 = "12345.123456789";
	
var __result2 = x.toPrecision(4);
var __expect2 = "1.235e+4";
		
var __result3 = x.toPrecision(11);
var __expect3 = "12345.123457";

try {
	x.toPrecision(22);
}
catch (e) {
	var __result4 = e.name;
	var __expect4 = "RangeError";
}
		  