// test with Object.seal
var o1 = {p1:1, p2:2};
var x1 = Object.seal(o1);
var __result1 = Object.isSealed(x1);
var __expect1 = true;

// test with Object.freeze
var o2 = {p1:1, p2:2};
var x2 = Object.freeze(o2);
var __result2 = Object.isSealed(x2);
var __expect2 = true;
