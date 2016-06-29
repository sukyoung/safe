/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
var x;
l_1 : {
	l_2 : {
		x = 1;
		break l_1;
	}
	x = 2;
	break l_1;
	x = 3;
}

var __result1 = x;
var __expect1 = 1;
