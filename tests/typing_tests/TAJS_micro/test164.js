function f1() {
	this.v1 = "v1";
}
function f2() {
	this.v2 = "v2";
}
f2.prototype = new f1;
var x = new f2();

function g1() {
	this.w1 = "w1";
}
function g2() {
	this.w2 = "w2";
}
g2.prototype = new g1;
var y = new g2();

with (x) {
	with (y) {
//		dumpState();
		v1 = v1 + "#";
		v2 = v2 + "#";
		w1 = w1 + "#";
		w2 = w2 + "#";
		var foo = 42;

//		dumpValue(v1);
		var __result1 = v1;  // for SAFE
		var __expect1 = "v1#";  // for SAFE

//		dumpValue(v2);
		var __result2 = v2;  // for SAFE
		var __expect2 = "v2#";  // for SAFE

//		dumpValue(w1);
		var __result3 = w1;  // for SAFE
		var __expect3 = "w1#";  // for SAFE

//		dumpValue(w2);
		var __result4 = w2;  // for SAFE
		var __expect4 = "w2#";  // for SAFE

//		dumpValue(foo);
		var __result5 = foo;  // for SAFE
		var __expect5 = 42;  // for SAFE
	}
//	dumpState();
}
//dumpState();

//dumpValue(x.v1);
var __result6 = x.v1;  // for SAFE
var __expect6 = "v1#";  // for SAFE

//dumpValue(y.w1);
var __result7 = y.w1;  // for SAFE
var __expect7 = "w1#";  // for SAFE

//dumpValue(foo);
var __result8 = foo;  // for SAFE
var __expect8 = 42;  // for SAFE
