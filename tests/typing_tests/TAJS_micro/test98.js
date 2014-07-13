function f() {
	if (Math.random())
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

var __result1;  // for SAFE
var __result2;  // for SAFE
try{
  h();
} catch(ex) {
//  dumpValue("HERE"); // should appear
  __result1 = "HERE";  // for SAFE
  __result2 = "HERE";  // for SAFE
}
var __expect1 = "HERE";  // for SAFE
var __expect2 = undefined;  // for SAFE

//dumpValue(x); // expected: "foo"|true
var __result3 = x;  // for SAFE
var __expect3 = "foo";  // for SAFE

var __result4 = x;  // for SAFE
var __expect4 = true;  // for SAFE
