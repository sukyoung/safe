/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = [1,2,2,2,3];

var __result1 = x.indexOf(2);
var __expect1 = 1;

var __result2 = x.indexOf(2,2);
var __expect2 = 2;

var __result3 = x.indexOf(4);
var __expect3 = -1;

var __result4 = x.lastIndexOf(2);
var __expect4 = 3;

var __result5 = x.lastIndexOf(2,2);
var __expect5 = 2;

var __result6 = x.lastIndexOf(4);
var __expect6 = -1;

