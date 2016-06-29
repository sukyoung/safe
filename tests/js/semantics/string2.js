/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x;

if(__TOP)
	x = "111"
else
	x = "str"
		
var __result1 = x
var __expect1 = "111"
	
var __result2 = x
var __expect2 = "str"
