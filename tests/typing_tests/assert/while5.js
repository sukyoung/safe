/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = 123;

while(__TOP) {
	x = 456;
	while(__TOP) {
		x = 789;
		break;
		x = -1;
	}
	break;
	x = -2;
}

var __result1 = x;
var __expect1 = __UInt;

