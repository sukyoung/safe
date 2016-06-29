/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var x = 123;

function f() {
	return x;
}

var o = { f: function () {return x}, x: 456};

var g = o.f;


var __result1 = f();
var __expect1 = 123;

var __result2 = o.f();
var __expect2 = 123;

var __result3 = g();
var __expect3 = 123;
