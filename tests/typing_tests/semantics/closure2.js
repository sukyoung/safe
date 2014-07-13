/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var arr = [];
var i;

for(i=0; i<10; i++) {
	try {
		throw true;
	} catch (x) {
		arr[i] = {
			setX: function (v) { x = v; },
			getX: function () { return x; }
		}
	}
}

arr[0].setX(10);
arr[1].setX("ABC");

var __result1 = arr[0].getX();
var __expect1 = 10;

var __result2 = arr[1].getX();
var __expect2 = "ABC";
