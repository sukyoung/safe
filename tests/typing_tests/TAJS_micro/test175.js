//dumpObject([1,2])
var __result1 = [1,2][0];  // for SAFE
var __expect1 = 1;  // for SAFE

var __result2 = [1,2][1];  // for SAFE
var __expect2 = 2;  // for SAFE

var __result3 = [1,2].length;  // for SAFE
var __expect3 = 2;  // for SAFE

//dumpObject((function () { }))
var __result4 = (function () { }).length;  // for SAFE
var __expect4 = 0;  // for SAFE
