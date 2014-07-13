/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

function f()  {this.b = 1}
f.prototype = {a:__TOP}
var x = new f();
var y;
var z;
if(__TOP) delete x.a;
if(x.a === 2) { y = f.prototype.a }
else z = f.prototype.a;

var __result1 = y;
var __expect1 = undefined; // PValue

var __result2 = y;
var __expect2 = 2;

var __result3 = z;
var __expect3 = 2;

var __result4 = f.prototype.a;
var __expect4 = 2;

