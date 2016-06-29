/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var x = 10;
function f() { }
function g() {
    try {
        return 20;
    } finally {
        x = f();
    }
}

g();

var __result1 = x;
var __expect1 = undefined;
