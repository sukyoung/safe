function foo() {
  try {
    return 10;
  } finally {
    return 20;
  }
}

var x = foo();

var __result1 = x;
var __expect1 = 20;

