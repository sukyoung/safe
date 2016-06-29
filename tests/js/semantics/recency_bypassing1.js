/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function f() {
	return {};
}

function g() {
	var o1 = f();
	o1.x = 10;

	var o2 = f();
	o2.x = "ABC";

	__result1 = o1.x;
	__expect1 = 10;

	__result2 = o2.x;
	__expect2 = "ABC";
}

g();
