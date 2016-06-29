/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = {};
var y = x;

var __result1 = x == y;
var __expect1 = true;

var __result2 = x === y;
var __expect2 = true;


var __result3 = x != y;
var __expect3 = false;

var __result4 = x !== y;
var __expect4 = false;

