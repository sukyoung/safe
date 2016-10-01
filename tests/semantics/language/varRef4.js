var x = 123;

function f() {
	return x;
}

var o = { f: function () {return x}, x: 456};

var g = o.f;


var __result1 = f();
var __expect1 = 123;

var __result2 = o.f();
var __expect2 = 123;

var __result3 = g();
var __expect3 = 123;
