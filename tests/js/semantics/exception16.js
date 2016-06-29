/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var __result1;
var __expect1 = 10;
var __result2;
var __expect2 = true;
var __result3;
var __expect3 = undefined;

var global;
try {
	true[1] = x;
} catch(e) {
	global = e;
}
global.x = 10;

__result1 = global.x;
__result2 = delete global.x;
__result3 = global.x;

