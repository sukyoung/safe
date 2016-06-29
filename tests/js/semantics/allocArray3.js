/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var x = [1];

var __result1 = x.length;
var __expect1 = 1

x[1] = {p1:2};

var __result2 = x.length;
var __expect2 = 2	// not supported

var __result3 = x[0];
var __expect3 = 1

var __result4 = x[1].p1;
var __expect4 = 2
 
