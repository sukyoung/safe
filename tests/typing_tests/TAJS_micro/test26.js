function f(x) {
	return g(x);
	function g(y) {return y+1}; 
}

var a = f(1);

//assert(a == 2);
var __result1 = a;  // for SAFE
var __expect1 = 2;  // for SAFE

//dumpValue(a);
