/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x;
var v;
var __result1;
if (true)
	v = 0;
else
	v = -1;

try {
	x = new Array(v);
} catch(e) {
	__result1 = e;
}

var __expect1 = undefined // undefined, ##RangeErr
var __result2 = x.length
var __expect2 = 0

