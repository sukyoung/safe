function f1(x) { return x+1; }

var __result1 = f1.call(undefined,10);
var __expect1 = 11;


function f2() { return this.x+1; }

var __result2 = f2.call({x:1});
var __expect2 = 2;

var __result3 = f2.call.call(f2, {x:1});
var __expect3 = 2;

// ECMAScript 5.1 section 15.3.4.4 Function.prototype.call(thisArg, argArray)
// "NOTE: The thisArg value is passed without modification as the this value. 
// This is a change from Edition 3, where a undefined or null thisArg is 
// replaced with the global object and ToObject is applied to all other values 
// and that result is passed as the this value."

// However, most of the browsers returns the object as ECMAScript 3 spec.
// This test for function g follows the ECMAScript 3 style

function g() { return this; }

var y = g.call(null);
var __result4 = y;
var __expect4 = this;

var z = g.call(10);
var __result5 = z.valueOf();
var __expect5 = 10;
