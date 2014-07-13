/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = [1,2,3];

var y = x.concat(4,5,6);

var __result1 = y.toString();
var __expect1 = "1,2,3,4,5,6";

var __result2 = y.length;
var __expect2 = 6;

var __result3 = x.toString();
var __expect3 = "1,2,3";

var __result4 = x.length;
var __expect4 = 3;