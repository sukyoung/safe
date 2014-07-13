function f(x) {
	return x+x;
}
var v = f("a") + f("b");
//dumpValue(v); // "aabb"
var __result1 = v;  // for SAFE
var __expect1 = "aabb";  // for SAFE

var obj = {aabb:42};

//dumpValue(obj[v]); // 42
var __result2 = obj[v];  // for SAFE
var __expect2 = 42;  // for SAFE

obj[v] = "foo";
//dumpValue(obj[v]); // "foo"
var __result3 = obj[v];  // for SAFE
var __expect3 = "foo";  // for SAFE

//dumpValue(obj.aabbb); // "foo"
var __result4 = obj.aabb;  // for SAFE
var __expect4 = "foo";  // for SAFE

var c = 487;
function r() {
	return c;
}
var ww = r();
//dumpValue(ww); // 487.0
var __result5 = ww;  // for SAFE
var __expect5 = 487;  // for SAFE


var bb = 123;
bb = !bb;
//dumpValue(bb); // false
var __result6 = bb;  // for SAFE
var __expect6 = false;  // for SAFE

var qwe = {y1:true}
//dumpValue("y1" in qwe); // true
var __result7 = "y1" in qwe;  // for SAFE
var __expect7 = true;  // for SAFE

//dumpValue(v in qwe); // false
var __result8 = v in qwe;  // for SAFE
var __expect8 = false;  // for SAFE

//dumpValue("y2" in qwe); //false
var __result9 = "y2" in qwe;  // for SAFE
var __expect9 = false;  // for SAFE

//dumpValue(typeof(42)); // "number"
var __result10 = typeof(42);  // for SAFE
var __expect10 = "number";  // for SAFE

function F() {
	this.a = 123;
}
F.prototype = "dyt";

function F2() {return 1234345}
F2.prototype = new F;

var pp = new F();
//dumpObject(pp);
var __result11 = pp instanceof Object;  // for SAFE
var __expect11 = true;  // for SAFE


var pp2 = new F2();
//dumpObject(pp2);
var __result12 = pp2.a;  // for SAFE
var __expect12 = 123;  // for SAFE
