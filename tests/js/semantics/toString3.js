/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

var o = {};
o[true] = 1;
o[false] = 0;


var __result1 = o["true"];
var __expect1 = 1;

var __result2 = o["false"];
var __expect2 = 0;