var foo = function(x) {
	return x + 42;
}

var x = foo(2222);

//assert(x == 2264);
var __result1 = x;  // for SAFE
var __expect1 = 2264;  // for SAFE
