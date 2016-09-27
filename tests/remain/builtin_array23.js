function f0() {}

function f1(input) {
  var w = new Array(input.length / 4);
  w[0] = new Array();
  return w[0][0];
}

function f2() {
  return f1(new Array());
}

var __result1 = f2();
var __expect1 = undefined;
