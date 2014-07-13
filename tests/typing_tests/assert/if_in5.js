/*******************************************************************************
    Copyright (c) 2012, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */
function f() {this.a = 1}
f.prototype = {};
var obj = new f;
if(__TOP) {
  delete obj.a;
}
var str = "a";
if(str in obj) {
   var __result1 = obj[str];
} else {
   var __result2 = obj[str];
}

var __expect1 = 1;
var __expect2 = undefined; // 1, undefined
