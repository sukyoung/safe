/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = {p1: 1};
var y = {p1: 'str'};

var __result1 = x == y;
var __expect1 = false;

var __result2 = x === y;
var __expect2 = false;

var __result3 = x != y;
var __expect3 = true;

var __result4 = x !== y;
var __expect4 = true;