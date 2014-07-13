var x = {c:42}
var y1 = x.a;
var y2 = x["b"]
var y3 = x.c
var y4 = x["c"]

//dumpValue(y1)
var __result1 = y1;  // for SAFE
var __expect1 = undefined;  // for SAFE

//dumpValue(y2)
var __result2 = y2;  // for SAFE
var __expect2 = undefined;  // for SAFE

//dumpValue(y3)
var __result3 = y3;  // for SAFE
var __expect3 = 42.0;  // for SAFE

//dumpValue(y4)
var __result4 = y4;  // for SAFE
var __expect4 = 42.0;  // for SAFE
