/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = 123;

do {
	x = 456;
	break;
	x = 789;
} while(__TOP)

var __result2 = x;
var __expect2 = 456;
