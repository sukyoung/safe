function f1(x) { return x+1; }

var __result1 = f1.call(undefined,10);
var __expect1 = 11;


function f2() { return this.x+1; }

var __result2 = f2.call({x:1});
var __expect2 = 2;

var __result3 = f2.call.call(f2, {x:1});
var __expect3 = 2;

function g() { return this; }

var y = g.call(null);
var __result4 = y;
var __expect4 = null;

var z = g.call(10);
var __result5 = z;
var __expect5 = 10;
