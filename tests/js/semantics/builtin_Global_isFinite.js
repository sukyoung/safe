/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var __result1 = isFinite(NaN);
var __expect1 = false;

var __result2 = isFinite("NaN");
var __expect2 = false;

var __result3 = isFinite(Infinity);
var __expect3 = false;

var __result4 = isFinite("Infinity");
var __expect4 = false;

var __result5 = isFinite(0);
var __expect5 = true;
