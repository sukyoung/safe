/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
function f() {}
function g() {this.a = 1;}
var a = {a:2};
var b = {b:3};
if(__TOP) {
f.prototype = a;
var obj = new f
g.prototype = a;
} else {
f.prototype = b;
g.prototype = b;
}
// obj.@proto 1, g.prototype 2
if(obj instanceof g) {
 var __result1 = g.prototype.a;
 var __result2 = g.prototype.b;
} else {
 var __result3 = g.prototype.a;
 var __result4 = g.prototype.b;
}

var __expect1 = 2 // 2, undefined
var __expect2 = undefined // 3, undefined
var __expect3 = undefined // 2, undefined
var __expect4 = 3 // 3, undefined
