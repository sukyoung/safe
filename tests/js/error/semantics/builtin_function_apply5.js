/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function f() { return this; }
var x = f.apply(10);

var __result1 = x.valueOf();
var __expect1 = 10;


function g() { return this; }
var y = g.apply(null);

var __result2 = y;
var __expect2 = this;  // ECMA 3rd, most browsers work in this way
//var __expect2 = null;  // ECMA 5th, Function.prototype.call/apply don't adjust this value. 
