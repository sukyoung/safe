/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

function f()  {this.a = 1}
f.prototype = {b:__UInt}
var x = new f();
var y;
var z;
if(__TOP) delete x.a;
if(x.b == 2) { y = x.b }
else z = x.b;

var __result1 = x.a;
var __expect1 = 1;

var __result2 = x.a;
var __expect2 = undefined;

var __result3 = y;
var __expect3 = undefined;

var __result4 = y;
var __expect4 = 2; // undefined, UInt

var __result5 = z;
var __expect5 = undefined;

var __result6 = z;
var __expect6 = __UInt;
