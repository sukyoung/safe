/*******************************************************************************
Copyright (c) 2012, S-Core.
All rights reserved.

Use is subject to license terms.

This distribution may include materials developed by third parties.
******************************************************************************/

function foo() {
	return this.x;
};

var x = 10;
var o1 = {x: "abc", foo: foo};

var __result1 = foo();
var __expect1 = 10;

var __result2 = o1.foo();
var __expect2 = "abc";
