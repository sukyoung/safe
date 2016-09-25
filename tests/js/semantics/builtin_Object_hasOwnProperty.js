// prototype chain check
var x1 = {p1:10};
Object.prototype.p2 = 10;
var __result1 = x1.hasOwnProperty("p2");
var __expect1 = false;

// own property check
var x2 = {p1:10};
var __result2 = x2.hasOwnProperty("p2");
var __expect2 = false;
var __result3 = x2.hasOwnProperty("p1");
var __expect3 = true;
