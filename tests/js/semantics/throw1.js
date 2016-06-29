var x = 0;
var y = 0;
try {
  throw 1;
  y = 1;
} catch(e) {
  x = e;
}

var __result1 = x;
var __expect1 = 1;
var __result2 = y;
var __expect2 = 0;
