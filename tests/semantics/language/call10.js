function foo() {
	return this;
};

var o = {};
o.f = foo;
var r2 = o.f();

var __result1 = r2;
var __expect1 = o;

