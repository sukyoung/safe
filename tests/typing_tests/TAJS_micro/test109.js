function f() { return 42}
//assert(f.call.call.call(f) === 42)
var __result1 = f.call.call.call(f);  // for SAFE
var __expect1 = 42;  // for SAFE

//assert(f.apply.apply.apply(f) === 42)
var __result2 = f.apply.apply.apply(f);  // for SAFE
var __expect2 = 42;  // for SAFE

//assert(Function.prototype.apply.apply(f) == 42)
var __result3 = Function.prototype.apply.apply(f);  // for SAFE
var __expect3 = 42;  // for SAFE

//dumpValue(f.apply.apply.apply(f))

function g(x) { return x + 1}
//assert(Function.prototype.call(g,45) === undefined)
var __result4 = Function.prototype.call(g,45);  // for SAFE
var __expect4 = undefined;  // for SAFE

//assert(isNaN(g.call.call(g,45)))
var __result5 = g.call.call(g,45);  // for SAFE
var __expect5 = NaN;  // for SAFE

//dumpValue(g.call.call(g,45))

//assert(Function.prototype.call.call(g,null,87) == 88);
var __result6 = Function.prototype.call.call(g,null,87);  // for SAFE
var __expect6 = 88;  // for SAFE

//dumpValue(Function.prototype.call.call(g,null,87));