// test with Object.preventExtensions
var o1 = {p1:1, p2:2};
var x1 = Object.preventExtensions(o1);
var __result1 = Object.isExtensible(x1);
var __expect1 = false;

// test with Object.seal
var o2 = {p1:1, p2:2};
var x2 = Object.seal(o2);
var __result2 = Object.isExtensible(x2);
var __expect2 = false;

// test with Object.freeze
var o3 = {p1:1, p2:2};
var x3 = Object.freeze(o3);
var __result3 = Object.isExtensible(x3);
var __expect3 = false;
