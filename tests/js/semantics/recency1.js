/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
function f() { return {};}

var x = f(); // x is recent
x.p = "1";

var y = f(); // y is recent, x is old

y.p = "11";

x = y;  // x, y are recent

y = f(); // y is recent, x is old

y.p = "111"


var __result1 = (y.p == "1")
var __expect1 = false;

var __result2 = (y.p == "11")
var __expect2 = false;

var __result3 = (y.p == "111")
var __expect3 = true;

var __result4 = (x.p == "11")
var __expect4 = true;
