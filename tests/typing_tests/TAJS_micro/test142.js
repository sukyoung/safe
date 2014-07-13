var x = new Array();
x[0] = {m:"foo"};
var y = x[Math.random()];
var z = y.m;

var __result1 = z;  // for SAFE
var __expect1 = "foo";  // for SAFE
