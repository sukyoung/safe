function F() {}
F.prototype.a = 42;

var x = new F;
//assert(x.a === 42);
var __result1 = x.a;  // for SAFE
var __expect1 = 42;  // for SAFE

x.a = 7;
//assert(x.a === 7);
var __result2 = x.a;  // for SAFE
var __expect2 = 7;  // for SAFE

//assert(F.prototype.a === 42);
var __result3 = F.prototype.a;  // for SAFE
var __expect3 = 42;  // for SAFE

//assert(F.prototype.toString !== undefined);
var __result4 = (F.prototype.toString !== undefined);  // for SAFE
var __expect4 = true;  // for SAFE

F.prototype.toString = 123;
//assert(F.prototype.toString === 123);
var __result5 = F.prototype.toString;  // for SAFE
var __expect5 = 123;  // for SAFE

//dumpValue("done");