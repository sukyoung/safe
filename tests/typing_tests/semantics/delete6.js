/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

if (__TOP) {
	x = 1;
}

var __result1 = delete x;
var __expect1 = true;

var __result2 = "bot";
var __result3;
try {
	__result2 = x;
} catch(e) {
	__result3 = e;
}
var __expect2 = "bot";
var __expect3 = __RefErrLoc;

