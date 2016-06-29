/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var f1 = function g1() { 
	return g1; 
};

var __result1 = f1();
var __expect1 = f1;

var __result2;
var __expect2 = false;
var f2 = function g2() { 
	__result2 = delete g2;
	return g2;
};

var __result3 = f2();
var __expect3 = f2;
