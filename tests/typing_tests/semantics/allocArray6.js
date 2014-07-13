/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var x = [];

var __result1 = x.length;
var __expect1 = 0;

x[0] = 1;

var __result2 = x.length;
var __expect2 = 1;

x[100] = 2;

var __result3 = x.length;
var __expect3 = 101;

x.length = 3;

var __result4 = x.length;
var __expect4 = 3;

var __result5 = x[100];
var __expect5 = undefined;
