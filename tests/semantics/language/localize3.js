var obj;

function f(x) {
  obj = x;
}

function g(x) {
  f(x);
  return obj.x;
}

var __result1;
var __expect1 = 123;

var __result2;
var __expect2 = "ABC";

if (@Top) {
  __result1 = g({x: 123});
} else {
  __result2 = g({x: "ABC"});
}
