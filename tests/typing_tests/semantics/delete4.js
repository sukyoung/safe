/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


x = 1;

if (__TOP) {
	var __result1 = delete x;
	var __expect1 = true;
} else {
	x = 2;
}

var __result2 = x;
var __expect2 = 2;
