/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var x = "12345";

var __result1 = x.indexOf("45")
var __expect1 = 3

var __result2 = x.indexOf("45", 4)
var __expect2 = -1

var __result3 = x.indexOf("56", 0)
var __expect3 = -1
