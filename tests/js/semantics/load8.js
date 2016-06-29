/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var o1 = {p1:123};
var o2 = { };
var o3;

if (__TOP) {
	o3 = o1; 
} else {
	o3 = o2;
}

var __result1 = o3.p1; 
var __expect1 = 123;

var __result2 = o3.p1; 
var __expect2 = undefined;
