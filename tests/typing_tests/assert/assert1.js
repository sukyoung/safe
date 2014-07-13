/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
var __result2;
var __expect1;
var __expect2;

function foo() {
	__result1 = this;
	for (var i=0; i<10; i++) {
		__result2 = this;
	}	
}

var o = new foo();
__expect1 = o;
__expect2 = o;

