function f(x) {
  var x;
  return x;
}
//dumpValue(f(7)); // 7.0
var __result1 = f(7);  // for SAFE
var __expect1 = 7;  // for SAFE

function g(x) {
  function x() {};
  g_x = x;  // for SAFE
  return x;
}
//dumpValue(g(7)); // {@x#fun3}
var __result2 = g(7);  // for SAFE
var __expect2 = g_x;  // for SAFE
