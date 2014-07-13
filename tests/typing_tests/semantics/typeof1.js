/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var __result1 = typeof 1;
var __expect1 = "number";
	
var __result2 = typeof true;
var __expect2 = "boolean";
		
var __result3 = typeof "str";
var __expect3 = "string";

var __result4 = typeof undefined;
var __expect4 = "undefined";

var __result5 = typeof null;
var __expect5 = "object";

var __result6 = typeof {};
var __expect6 = "object";

var __result7 = typeof (function () {});
var __expect7 = "function";
