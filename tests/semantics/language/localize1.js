var x = {};

function f() { return x; }

x.a = 123;
f();

x.a = "ABC";
f();

var __result1 = x.a;
var __expect1 = "ABC";
