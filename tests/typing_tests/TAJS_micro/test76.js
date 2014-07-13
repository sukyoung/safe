var x = 1;

var t1 = -x;
var t2 = +x;
var t3 = ~x;
var t4 = !x;

//dumpValue(t1);
//dumpValue(t2);
//dumpValue(t3);
//dumpValue(t4);

//assert(t1 === -1);
var __result1 = t1;  // for SAFE
var __expect1 = -1;  // for SAFE

//assert(t2 === 1);
var __result2 = t2;  // for SAFE
var __expect2 = 1;  // for SAFE

//assert(t3 === -2);
var __result3 = t3;  // for SAFE
var __expect3 = -2;  // for SAFE

//assert(t4 === false);
var __result4 = t4;  // for SAFE
var __expect4 = false;  // for SAFE
