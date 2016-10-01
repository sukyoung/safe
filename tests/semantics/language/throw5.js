var x;
var y = "";
try {
  try {
    throw 1;
  } finally {
    try {
      throw "2";
    } catch(e) {
	  y = e;
    }
  }
} catch(e) {
  x = e;
}

var __result1 = x;
var __expect1 = 1;
var __result2 = y;
var __expect2 = "2";

