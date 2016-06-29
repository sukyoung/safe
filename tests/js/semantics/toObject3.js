/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var x = true;
x.p = 1;

var y = false;
y.p = 0;

var __result1 = x.p;
var __expect1 = undefined;

var __result2 = y.p;
var __expect2 = undefined;
