var x4 = new Array(1, 2, 3);
var __result1 = x4.length
var __expect1 = 3
var __result2 = x4[0]
var __expect2 = 1
var __result3 = x4[1]
var __expect3 = 2
var __result4 = x4[2]
var __expect4 = 3
var __result5 = x4[3]
var __expect5 = undefined

var x5 = new Array();
var __result6 = x5.length
var __expect6 = 0
var __result7 = x5[0]
var __expect7 = undefined

function f0() {}
function f1(input) {
  var w = new Array(input.length / 4);
  w[0] = new Array();
  return w[0][0];
}
function f2() {
  return f1(new Array());
}
var __result8 = f2();
var __expect8 = undefined;
