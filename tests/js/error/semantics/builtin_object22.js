/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o = {p1:1, p2:2};

var __result1 = o.toString();
var __expect1 = "[object Object]";

// we can't know where this code is executed.
var __result2 = o.toLocaleString();
var __expect2 = "[object Object]";

var __result3 = o.valueOf().p1;
var __expect3 = 1;