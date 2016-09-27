function f1(x,y) { return x+y; }

var __result1 = f1.apply(this, [1,2]);
var __expect1 = 3;

function f2(y,z) { return this.x+y+z; }

var __result2 = f2.apply({x:1}, [2,3]);
var __expect2 = 6;

function f3() { return this; }

var __result3 = f3.apply(this);
var __expect3 = this;


function g() {
	arguments.x = 123;
	return arguments;
}

function h() {
	arguments.x = "ABC";
	g.apply(this, arguments);
	return arguments;
}

var arg1 = g();
var arg2 = h();

var __result4 = arg1.x;
var __expect4 = 123;

var __result5 = arg2.x;
var __expect5 = "ABC";

// ECMAScript 5.1 section 15.3.4.3 Function.prototype.apply(thisArg, argArray)
// "NOTE: The thisArg value is passed without modification as the this value. 
// This is a change from Edition 3, where a undefined or null thisArg is 
// replaced with the global object and ToObject is applied to all other values 
// and that result is passed as the this value."

// However, most of the browsers returns the object as ECMAScript 3 spec.
// The test for function f4 follows the ECMAScript 3 style

function f4() { return this; }
var y = f4.apply(null);
var __result6 = y;
var __expect6 = this;

var z = f4.apply(10);
var __result7 = z.valueOf();
var __expect7 = 10;
