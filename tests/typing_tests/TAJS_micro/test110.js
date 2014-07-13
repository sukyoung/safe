var x = {a:42}
var y = {b:x}
y.b.a += 1;
//dumpValue(y.b.a);
var __result1 = y.b.a;  // for SAFE
var __expect1 = 43.0;  // for SAFE

x.a += 1;
//dumpValue(y.b.a);
var __result2 = y.b.a;  // for SAFE
var __expect2 = 44.0;  // for SAFE

var z = Math.random() ? null : x;
//dumpValue(z);
var __result3 = z;  // for SAFE
var __expect3 = null;  // for SAFE

var __result4 = z.a;  // for SAFE
var __expect4 = 44.0;  // for SAFE

z.a += 1;
//dumpValue(x.a);
var __result5 = x.a;  // for SAFE
var __expect5 = 45.0;  // for SAFE
