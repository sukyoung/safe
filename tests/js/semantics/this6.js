/*******************************************************************************
Copyright (c) 2012, S-Core.
All rights reserved.

Use is subject to license terms.

This distribution may include materials developed by third parties.
******************************************************************************/

function foo() {
	return this.x;
};

var o1 = {x: "abc", foo: foo};

var __result1 = o1.foo();
var __expect1 = "abc";
