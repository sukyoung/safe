var x = 0;
try {
  throw 1;
} catch(e) {
  try {
    throw "2";
  } catch(e) {
    x = e;
  }
  x = e;
}

var __result1 = x;
var __expect1 = 1;

