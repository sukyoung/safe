/*******************************************************************************
Copyright (c) 2012, S-Core.
All rights reserved.

Use is subject to license terms.

This distribution may include materials developed by third parties.
******************************************************************************/

// FAIL: If no parameters are given to Array.prototype.slice, the result is incorrect.

var arr1 = [0,1,2];
var arr2 = arr1.slice();

var __result1 = arr2[0];
var __expect1 = 0;

var __result2 = arr2[1];
var __expect2 = 1;

var __result3 = arr2[2];
var __expect3 = 2;

var __result4 = arr2[3];
var __expect4 = undefined;
