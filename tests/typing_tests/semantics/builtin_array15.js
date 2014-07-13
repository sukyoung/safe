/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = [1,2,3];
var y = [];

var __result1 = x.shift();
var __expect1 = 1;

var __result2 = x.toString();
var __expect2 = "2,3";

var __result3 = y.shift();
var __expect3 = undefined