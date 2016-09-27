/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

// current time
var x = Date.now();
var y = new Date(x);

var __result1 = x;
var __expect1 = 1351559847614;

var __result2 = y.valueOf();
var __expect2 = 1351559847614;