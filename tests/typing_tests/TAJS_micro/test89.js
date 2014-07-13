function g(v) {
	return {a:v};
}

function h() {
	g("foo");	
}

var x = g(87);
h();
//dumpValue(x.a);
//assert(x.a === 87);
var __result1 = x.a;  // for SAFE
var __expect1 = 87;  // for SAFE

//dumpValue(x);
//dumpObject(x);
