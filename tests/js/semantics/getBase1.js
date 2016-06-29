/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
function g() {return 1};
function f() {
	function g() {return 2};
	return g();
}

var __result1 = f();
var __expect1 = 2;
