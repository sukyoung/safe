function f(a,b) { }
f.length = "A";

var __result1 = f.length;
var __expect1 = 2;

var __result2 = delete f.length;
var __expect2 = false;
