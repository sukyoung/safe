/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function f(x) {
	var y;
	y=1;
	if (x) return y;
	y=2;
	f(true);
	return y;
}

var __result1 = f(false);
var __expect1 = 2;

