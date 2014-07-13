var x = {gt: 4, funtimes: "goodtimes"}
delete x.gt;
//dumpObject(x);
var __result1 = x.gt;  // for SAFE
var __expect1 = undefined;  // for SAFE

var __result2 = x.funtimes;  // for SAFE
var __expect2 = "goodtimes";  // for SAFE
