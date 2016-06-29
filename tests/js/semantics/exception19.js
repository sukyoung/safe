/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

function f() { throw "x" }

var __result1, __result2;
var y;
try {
    if(true) {
        y = 2;
        f();
        y = 3;
    }
} catch(e) {
    __result1 = y;
    __result2 = e;
}

var __expect1 = 2;
var __expect2 = "x";

