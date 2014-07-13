g = {a: 234, b:23, t:44}
o = {}
//dumpObject(g)
var __result1 = g.a;  // for SAFE
var __expect1 = 234;  // for SAFE

var __result2 = g.b;  // for SAFE
var __expect2 = 23;  // for SAFE

var __result3 = g.t;  // for SAFE
var __expect3 = 44;  // for SAFE

for (x in g) {
    o[x] = g[x]
}
//dumpObject(o)
var __result4 = o.a;  // for SAFE
var __expect4 = 234;  // for SAFE

var __result5 = o.b;  // for SAFE
var __expect5 = 23;  // for SAFE

var __result6 = o.t;  // for SAFE
var __expect6 = 44;  // for SAFE
