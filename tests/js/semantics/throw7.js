var x;

function foo() {
  throw 1;
}

function bar() {
  foo();
}

try {
  bar();
} catch(e) {
  x = e;
}

var __result1 = x;
var __expect1 = 1;

