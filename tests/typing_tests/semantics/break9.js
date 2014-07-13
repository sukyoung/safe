/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var x;
var b = true;
do {
	x = 123;
	b = false;
	break;
	x = 456;
	b = true;
} while(b)

var __result1 = x;
var __expect1 = 123;
