/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o1 = {};
var o2 = {x:123};
var o3 = {x:123, y:456};
var p;

if (__TOP)
	p = "x" 
else
	p = "y"

var __result1 = o1[p];
var __expect1 = undefined;

var __result2 = o2[p];
var __expect2 = 123;
var __result3 = o2[p];
var __expect3 = undefined;

var __result4 = o3[p]
var __expect4 = 123;
var __result5 = o3[p]
var __expect5 = 456;