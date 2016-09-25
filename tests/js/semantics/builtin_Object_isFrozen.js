// test with Object.freeze
var o1 = {p1:1, p2:2};
var x1 = Object.freeze(o1);
var __result1 = Object.isFrozen(x1);
var __expect1 = true;

// test with Object.seal
var o2 = {p1:1, p2:2};
var x2 = Object.seal(o2);
var __result2 = Object.isFrozen(x2);
var __expect2 = false;
