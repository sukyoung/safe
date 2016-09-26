var x1 = new Array(0);
var __result1 = x1.length
var __expect1 = 0

var __result2;
try { new Array(-1);
} catch(e) { __result2 = e; }
var __expect2 = __RangeErrLoc

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

var x4 = new Array(1, 2, 3);
var __result1 = x4.length
var __expect1 = 3
var __result2 = x4[0]
var __expect2 = 1
var __result3 = x4[1]
var __expect3 = 2
var __result4 = x4[2]
var __expect4 = 3
var __result5 = x4[3]
var __expect5 = undefined

var x = new Array();
var __result1 = x.length
var __expect1 = 0
var __result2 = x[0]
var __expect2 = undefined
