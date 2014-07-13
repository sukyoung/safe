/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var x;

if(__TOP) {
	x = 12;
	if(__TOP) {
		x = 34;
	}
	else {
		l:{break l; x=-1;}
	}
}
else {
	x = 56;
	if(__TOP) {
		l:{break l; x=-2;}
	}
	else {
		l:{break l; x=-3;}
	}
}
	

var __result1 = x;
var __expect1 = 12;

var __result2 = x;
var __expect2 = 34;

var __result3 = x;
var __expect3 = 56;
