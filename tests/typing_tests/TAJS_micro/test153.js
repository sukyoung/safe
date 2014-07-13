function make() {
	return {};
}

function f() {
	make();
//	dumpValue(x.p);
	__result1 = x.p;  // for SAFE
}
var __expect1 = 42;  // for SAFE

var x = make();
x.p = 42;
f();
