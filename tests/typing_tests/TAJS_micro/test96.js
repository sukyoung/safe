var x = "foo";

function g() {
	x = 1;
}

function h() {
	g();
	x = true;
}

h();
//dumpValue(x); // expected: true
var __result1 = x;  // for SAFE
var __expect1 = true;  // for SAFE
