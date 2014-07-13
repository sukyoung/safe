/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var x = __TOP;
var y = "str";
if(x == y) {
	y = x + 1;
}

var __result1 = x;
var __expect1 = __TOP;

var __result2 = y;
var __expect2 = __StrTop; // Number, String
