/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = {p:1};

if (__TOP) {
	var __result1 = delete x.p;
	var __expect1 = true;
} else
	x.p = 2;

var __result2 = x.p
var __expect2 = 2 | undefined
