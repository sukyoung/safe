/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = 123;
var i;

for(i = 1; i; i--) {
	x = 456;
	break;
	x = 789;
}

var __result2 = x;
var __expect2 = 456; //  __UInt

var __result3 = i;
var __expect3 = 1;
