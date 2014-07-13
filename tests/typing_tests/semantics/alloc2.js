/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

function Class(n) {
	this.p = n;
}

var x = new Class(123);

var y = new Class([456]);

var __result1 = x.p;
var __expect1 = 123;

var __result2 = (y.p)[0];
var __expect2 = 456;
