/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var o1 = { };
var p1;
if (__TOP) {
    p1 = "0";
} else {
    p1 = "1";
}

o1[p1] = 123;

var __result1 = o1[0];
var __expect1 = 123;

var __result2 = o1[1];
var __expect2 = 123;

var __result3 = o1[__TOP];
var __expect3 = 123;