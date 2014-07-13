function f() {
	//if (Math.random())
	//	throw new Error;
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
try{
  h();
} catch(ex) {
//  dumpValue("HERE"); // should not appear
  __result1 = "HERE"
	  
}
var __expect1 = undefined;

//dumpValue(x); // expected: |true
var __result2 = x;  // for SAFE
var __expect2 = true;  // for SAFE
