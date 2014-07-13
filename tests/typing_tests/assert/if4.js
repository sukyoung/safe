/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var __result1 = true || false;
var __expect1 = true; // Bool

var __result2 = false || 123;
var __expect2 = 123; // false, 123

var __result3 = true && 456;
var __expect3 = 456; // true, 456

var __result4 = false && 789;
var __expect4 = false; // false, 789
