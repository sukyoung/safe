var x = (6).toString();
//assert(x === "6");
var __result1 = x;  // for SAFE
var __expect1 = "6";  // for SAFE

//assert(new String(6) !== new String(6));
var __result2 = new String(6) !== new String(6);  // for SAFE
var __expect2 = true;  // for SAFE
