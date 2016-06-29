var x;

function foo() {
  try {
    return 10;
  } finally {
    return;
  }
  return 20;
}

x = foo();

var __result1 = x;
var __expect1 = undefined;

