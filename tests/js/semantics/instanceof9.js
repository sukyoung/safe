/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function f() {
	return function () { }
}

var f1 = f();
var f2 = f();
var f3 = f();

var obj = new f1();

var __result1 = obj instanceof f1;
var __expect1 = true;

var __result2 = obj instanceof f2;
var __expect2 = false;
