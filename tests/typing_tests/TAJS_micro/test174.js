//dumpValue(Array.isArray(3))
var __result1 = Array.isArray(3);  // for SAFE
var __expect1 = false;  // for SAFE

//dumpValue(Array.isArray([]))
var __result2 = Array.isArray([]);  // for SAFE
var __expect2 = true;  // for SAFE

//dumpValue(Array.isArray({}))
var __result3 = Array.isArray({});  // for SAFE
var __expect3 = false;  // for SAFE

//dumpValue(Array.isArray([1,2,3]))
var __result4 = Array.isArray([1,2,3]);  // for SAFE
var __expect4 = true;  // for SAFE

//dumpValue(Array.isArray(true))
var __result5 = Array.isArray(true);  // for SAFE
var __expect5 = false;  // for SAFE

//dumpValue(Array.isArray("hi"))
var __result6 = Array.isArray("hi");  // for SAFE
var __expect6 = false;  // for SAFE

if (Math.random())
   x = [1,2,3]
else
   x = 3
//dumpValue(Array.isArray(x))
var __result7 = Array.isArray(x);  // for SAFE
var __expect7 = true;  // for SAFE

var __result8 = Array.isArray(x);  // for SAFE
var __expect8 = false;  // for SAFE
