function g(v) {
	return {a:v};
}

var x = g(87);

function h() {
	x.a = true;
	g("foo");	
}

h();

//dumpValue(x);
//dumpObject(x);
var __result1 = x.a;  // for SAFE
var __expect1 = true;  // for SAFE

