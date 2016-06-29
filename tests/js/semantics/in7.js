/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o1 = {};
var o2 = {1:123};
var o3 = {1:123, x: 456};
var p;

if (__TOP)
	p = "1" 
else
	p = "x"

var __result1 = p in o1;
var __expect1 = false;

var __result2 = p in o2;
var __expect2 = true;
var __result3 = p in o2;
var __expect3 = false;

var __result4 = p in o3;
var __expect4 = true;