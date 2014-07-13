/*******************************************************************************
Copyright (c) 2012, S-Core.
All rights reserved.

Use is subject to license terms.

This distribution may include materials developed by third parties.
******************************************************************************/

var v;

if (__TOP) {
	v = 123;
} else {
	v = "ABC";
} 

var obj = new Object(v);
var obj_str = obj.toString();
var obj_val = obj.valueOf();

var __result1 = obj_str;
var __expect1 = "123";

var __result2 = obj_str;
var __expect2 = "ABC";

var __result3 = obj_val;
var __expect3 = 123;

var __result4 = obj_val;
var __expect4 = "ABC";
