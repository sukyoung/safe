/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o = {a:"str", b:11};

var desc = Object.getOwnPropertyDescriptor(o, "a");

var __result1 = desc.value;
var __expect1 = "str" 

var __result2 = desc.writable;
var __expect2 = true

var __result3 = desc.enumerable;
var __expect3 = true
	
var __result4 = desc.configurable;
var __expect4 = true
	