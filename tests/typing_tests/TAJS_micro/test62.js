var x = {}
var y = x instanceof Object;
var z = x instanceof Function;

//dumpValue(y);
var __result1 = y;  // for SAFE
var __expect1 = true;  // for SAFE

//dumpValue(z);
var __result2 = z;  // for SAFE
var __expect2 = false;  // for SAFE

//assert(y);
//assert(!z);
