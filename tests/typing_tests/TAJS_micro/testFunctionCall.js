// plain function invocation
function f(x, y) {
	return x+y;
}
var a1 = f(17, 4);
//dumpValue(a1);
var __result1 = a1;  // for SAFE
var __expect1 = 21;  // for SAFE

// function invocation via call without receiver
function g(x, y) {
	return x-y;
}
var a2 = g.call(null, 101, 1);
//dumpValue(a2);
var __result2 = a2;  // for SAFE
var __expect2 = 100;  // for SAFE

// function invocation via call with receiver
function h() {
	return this.x;
}
var a3 = h.call({ x: "hoohoo" });
//dumpValue(a3);
var __result3 = a3;  // for SAFE
var __expect3 = "hoohoo";  // for SAFE

// calling a primitve
// var d = "xxx".concat("yyy"); // works ok
var a4 = "".concat.call("xxx", "yyy");
//dumpValue(a4);
var __result4 = a4;  // for SAFE
var __expect4 = "xxxyyy";  // for SAFE

// recursive calling
var a5 = "zzz".substring.call.call("".concat, "xxx", "yyy");
//dumpValue(a5);
var __result5 = a5;  // for SAFE
var __expect5 = "xxxyyy";  // for SAFE

var a6 = Function.call.call("".concat, "xxx", "yyy");
//dumpValue(a6);
var __result6 = a6;  // for SAFE
var __expect6 = "xxxyyy";  // for SAFE

var a7 = f.call.call("".concat, "xxx", "yyy");
//dumpValue(a7);
var __result7 = a7;  // for SAFE
var __expect7 = "xxxyyy";  // for SAFE

var a8 = f.call.call.call("".concat, "xxx", "yyy");
//dumpValue(a8);
var __result8 = a8;  // for SAFE
var __expect8 = "xxxyyy";  // for SAFE
