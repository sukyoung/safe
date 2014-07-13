function f(x) {
	return function(y) {
		x = x + 1;
		return x+y
	}
}

var g1 = f(1);
var g2 = g1(2);
var g3 = g1(2);

//assert(g2 === 4);
var __result1 = g2;  // for SAFE
var __expect1 = 4;  // for SAFE

//assert(g3 == 5);
var __result2 = g3;  // for SAFE
var __expect2 = 5;  // for SAFE

//dumpValue(g2);
//dumpValue(g3);