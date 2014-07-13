/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = [1,2,3,4,5];

var __result1 = x.slice(2,4).toString();
var __expect1 = "3,4";

var __result2 = x.slice(4,2).toString();
var __expect2 = "";

var __result3 = x.slice(-3,-1).toString();
var __expect3 = "3,4";