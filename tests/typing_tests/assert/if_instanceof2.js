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
} else {
f.prototype = b;
}
var obj = new f
g.prototype = b;
// obj.@proto 2, g.prototype 1
if(obj instanceof g) {
 var __result1 = obj.a;
 var __result2 = obj.b;
} else {
 var __result3 = obj.a;
 var __result4 = obj.b;
}

var __expect1 = undefined // 2, undefined
var __expect2 = 3 // 3, undefined
var __expect3 = undefined // 2, undefined
var __expect4 = undefined // 3, undefined
