/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function foo() {
	return this;
};

var o = {};
o.f = foo;
var r2 = o.f();

var __result1 = r2;
var __expect1 = o;

