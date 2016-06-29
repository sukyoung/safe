/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var o = {};

if (__TOP) {
	o.x = 1;
}

var __result1 = delete o.x;
var __expect1 = true;

var __result2 = o.x;
var __expect2 = undefined;