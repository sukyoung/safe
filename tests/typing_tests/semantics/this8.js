/*******************************************************************************
Copyright (c) 2012, S-Core.
All rights reserved.

Use is subject to license terms.

This distribution may include materials developed by third parties.
******************************************************************************/

var x = 10;
function f1() {
    var g1 = function () { return this.x; };
    return g1();
}

var __result1 = f1();
var __expect1 = 10;

function f2() {
    var g2 = function () { return this.x; };
    var h2 = function () { return g2(); }
    return g2();
}

var __result2 = f2();
var __expect2 = 10;
