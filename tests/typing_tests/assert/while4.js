/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var a = [1,2];

var sum = 0;

for (var x in a) {
	sum += a[x];
}

var __result1 = sum;
var __expect1 = __NumTop; 

var __result3 = sum;
var __expect3 = __StrTop;

var __result2 = x;
var __expect2 = __StrTop;

var __result4 = x;
var __expect4 = undefined;
