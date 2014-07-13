/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
function f() {this.a = 1}
f.prototype = {a:1};
var obj = new f;
if(__TOP) {
  delete obj.a;
  delete f.prototype.a;
}
var str = "a";
if(str in obj) {
   var __result1 = obj[str];
} else {
   var __result2 = obj[str];
   var __result3 = str in obj;
}

var __expect1 = 1;
var __expect2 = undefined;
var __expect3 = false; // undefined, Bool
