/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = [1,2,3,4,5];
var y = [1,2,3,4,5];
var z = [1,2,3,4,5];

var __result1 = x.splice(2,2).toString();
var __expect1 = "3,4";
	
var __result2 = x.toString();
var __expect2 = "1,2,5";

var __result3 = y.splice(1,3,22,33,44,55).toString();
var __expect3 = "2,3,4";
	
var __result4 = y.toString();
var __expect4 = "1,22,33,44,55,5";

var __result5 = z.splice(1,3,22).toString();
var __expect5 = "2,3,4";
	
var __result6 = z.toString();
var __expect6 = "1,22,5";