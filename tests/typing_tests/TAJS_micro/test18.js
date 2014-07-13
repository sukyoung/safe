var x = 3;
var y = 7;
var z = x + y;

//assert(z == 11); // fails
var __result1 = (z == 11);  // for SAFE
var __expect1 = false;  // for SAFE

//assert(z == 10);
var __result2 = (z == 10);  // for SAFE
var __expect2 = true;  // for SAFE

//assert(7 == 42); // fails
var __result3 = (7 == 42);  // for SAFE
var __expect3 = false;  // for SAFE

//isFinite(42)
var __result4 = isFinite(42);  // for SAFE
var __expect4 = true;  // for SAFE
