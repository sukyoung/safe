var baz1 = 42;
var baz2 = 10;

function f(x) {
	var y = x + 7+ baz1;
//	dumpValue(y);
	__result1 = y;  // for SAFE
	
	baz1 = baz1 + 1;
	var g = function gg(z) { y = y+1; /*baz1 = 44; baz2 = 45;*/ return x+y+z+baz1 + baz2 - 1}
	var w = g(123);
	return w + y;
}
var __expect1 = 616;  // for SAFE

var a = f(567);

//assert(a == 1976);
var __result2 = a;  // for SAFE
var __expect2 = 1976;  // for SAFE

//dumpValue(a);
//dumpState();
