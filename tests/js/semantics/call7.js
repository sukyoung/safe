/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/


function g(a) {
	return a;
};
function f(g) {
	var x = 2;
	return g(x);
};

var __result1 = f(g);
var __expect1 = 2;