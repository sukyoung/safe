var x;
var y;

try {
  throw 1
} catch(e) {
  x = e;
}

try {
  throw "2"
} catch(e) {
  y = e;
}

var __result1 = x;
var __expect1 = 1;
var __result2 = y;
var __expect2 = "2";

