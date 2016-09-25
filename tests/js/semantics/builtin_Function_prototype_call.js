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

var __result2 = y;
var __expect2 = this;  // most browsers work in this way
//var __expect2 = null;  // ECMA 5th, Function.prototype.call/apply don't adjust this value. 
