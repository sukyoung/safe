/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = 100.123;
var __result1 = x.toString();
var __expect1 = "100.123";

var __result2 = x.toString(2);
var __expect2 = "1100100.00011111100101110010010001110100010100111001";

try {
 x.toString(37);
}
catch (e){
	var __result3 = e.name;
	var __expect3 = "RangeError";
}
  