/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

function Class1(n) {
	this.p = n
}
function Class2(n) {
	this.pp = n;
}
Class2.prototype = new Class1(456)

var x = new Class2(123);

var __result1 = x.pp;
var __expect1 = 123;

var __result2 = x.p;
var __expect2 = 456;
