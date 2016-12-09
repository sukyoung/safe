var x1 = new Array(0);
var __result1 = x1.length
var __expect1 = 0

var __result2;
try { new Array(-1);
} catch(e) { __result2 = e instanceof @RangeErr; }
var __expect2 = true;

var x2 = new Array("abc");
var __result3 = x2.length
var __expect3 = 1
var __result4 = x2[0]
var __expect4 = "abc"

var x3 = new Array(3);
var __result5 = x3.length
var __expect5 = 3
var __result6 = x3[0]
var __expect6 = undefined
