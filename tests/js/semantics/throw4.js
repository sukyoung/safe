var x = 0;

function foo() {
  throw 1;
}

try {
  foo();
} catch(e) {
  x = e;
}

var __result1 = x;
var __expect1 = 1;
