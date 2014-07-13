/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var o = {p1:1, p2:2};

var x = Object.keys(o);

// order is implementation dependent. 
var __result1 = x[0];
var __expect1 = "p2";

var __result2 = x[1];
var __expect2 = "p1";
