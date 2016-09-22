/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = [1,2,3];
var y = [];

var __result1 = x.pop();
var __expect1 = 3;

var __result2 = x.toString();
var __expect2 = "1,2";

var __result3 = x.length;
var __expect3 = 2;

var __result4 = y.pop();
var __expect4 = undefined;

var __result5 = y.length;
var __expect5 = 0;