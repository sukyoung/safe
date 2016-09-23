/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var __result1 = isNaN(NaN);
var __expect1 = true;

var __result2 = isNaN("NaN");
var __expect2 = true;

var __result3 = isNaN(Infinity);
var __expect3 = false;

var __result4 = isNaN(0);
var __expect4 = false;

