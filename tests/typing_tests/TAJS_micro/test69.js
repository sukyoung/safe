var x = "123";
var y = +x;
//dumpValue(y);

//assert(y === 123.0);
var __result1 = (y === 123.0);  // for SAFE
var __expect1 = true;  // for SAFE

//assert(y !== "123");
var __result2 = (y !== "123");  // for SAFE
var __expect2 = true;  // for SAFE

//assert(y == "123");
var __result3 = (y == "123");  // for SAFE
var __expect3 = true;  // for SAFE
