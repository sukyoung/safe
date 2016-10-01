function bar() {
  return foo();
}

function foo() {
  return 10;
}

var x = bar();

var __result1 = x;
var __expect1 = 10;

