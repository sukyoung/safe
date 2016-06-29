var e = 0;
try {
  throw 1;
} catch(e) {
  e = 2;
}

var __result1 = e;
var __expect1 = 0;
