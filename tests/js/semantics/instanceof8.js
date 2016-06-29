/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var __result1;
var __result2;

var o;
if (__TOP) {
	o = {};
} else {
	o = Object;
}

try {
	__result1 = 1 instanceof o;
} catch(e) {
	__result2 = e;
}

var __expect1 = false;
var __expect2 = __TypeErrLoc;

