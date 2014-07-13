function make() {
	return {};
}

function f() {
	make();
//	dumpValue(x.p);
	__result1 = x.p;  // for SAFE
	
	make();
//	dumpValue(x.p);
	__result2 = x.p;  // for SAFE
}
var __expect1 = 42;  // for SAFE
var __expect2 = 42;  // for SAFE

var x = make();
x.p = 42;
f();
//dumpValue(x.p);
var __result3 = x.p;  // for SAFE
var __expect3 = 42;  // for SAFE
