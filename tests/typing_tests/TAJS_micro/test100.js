function f() {
	//if (Math.random())
		throw new Error;
}

var x = "foo";

function g() {
	f();
	x = 1;
}

function h() {
	g();
	x = true;
}

try{
  h();
} catch(ex) {
//  dumpValue("HERE"); // should appear
	var __result1 = "HERE";  // for SAFE
}
var __expect1 = "HERE";  // for SAFE

//dumpValue(x); // expected: "foo"
var __result2 = x;  // for SAFE
var __expect2 = "foo";  // for SAFE
