/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
function f() {
	var x;
	return {
		set_x1: function (v) { x = v; },
		set_x2: function (v) { x = v; },
    	set_x3: function (v) { x = v; },
        get_x: function () { return x; }
    };
}

var o1 = f();
o1.set_x1(null);

var o2 = f();
o2.set_x2(10);

var o3 = f();
o3.set_x3("A");

var __result1 = o1.get_x();
var __expect1 = null;

var __result2 = o2.get_x();
var __expect2 = 10;

var __result3 = o3.get_x();
var __expect3 = "A";
