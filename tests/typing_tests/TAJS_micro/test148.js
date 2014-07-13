var x = {a:42}
var y;
function f() {
  y = x.a;
}
f()
//dumpValue(x.a)
var __result1 = x.a;  // for SAFE
var __expect1 = 42;  // for SAFE

x = {a:"foo"}
f()
//dumpValue(x.a)
var __result2 = x.a;  // for SAFE
var __expect2 = "foo";  // for SAFE
