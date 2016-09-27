/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = [1,2,3,4,5];

var __result1 = x.unshift();
var __expect1 = 5;

var __result2 = x.toString();
var __expect2 = "1,2,3,4,5";

var __result3 = x.unshift(-2,-1,0);
var __expect3 = 8;

var __result4 = x.toString();
var __expect4 = "-2,-1,0,1,2,3,4,5";

