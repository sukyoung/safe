function f() { return this; }
var x = f.apply(10);

var __result1 = x.valueOf();
var __expect1 = 10;
