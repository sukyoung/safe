/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var o1 = { };
var p1;
if (__TOP) {
    p1 = "A";
} else {
    p1 = "B";
}

o1[p1] = 123;

var __result1 = o1["A"];
var __expect1 = 123;

var __result2 = o1["B"];
var __expect2 = 123;

var __result3 = o1[__TOP];
var __expect3 = 123;