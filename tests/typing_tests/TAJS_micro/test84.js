var q = {a:5};
q.a++;
//dumpValue(q.a);
//assert(q.a == 6);
var __result1 = q.a;  // for SAFE
var __expect1 = 6;  // for SAFE

var x = {a:6}
x.a ^= 42;
//assert(x.a == 44);
var __result2 = x.a;  // for SAFE
var __expect2 = 44;  // for SAFE
