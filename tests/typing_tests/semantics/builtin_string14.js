/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = "12345";

var __result1 = x.slice(2,4);
var __expect1 = "34"

var __result2 = x.slice(4,2);
var __expect2 = ""
	
var __result3 = x.slice(-3,-1);
var __expect3 = "34"
	
var __result4 = x.slice();
var __expect4 = "12345";

var __result5 = x.slice(2, undefined);
var __expect5 = "345";