/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
function f() { return {};}

var x = f();
var y;

function rec() {
	y = x;
	x = f();  // x is recent, y is old
	x.p = 1;  // strong update
	y.p = "2";// weak update
	if (__TOP)
		rec();
	else
		return;
}

rec();

//x is Recent
var __result1 = (x.p == 1);
var __expect1 = true;

var __result2 = (x.p == "2");
var __expect2 = false;

// y is old
var __result3 = (y.p == "2");
var __expect3 = true;