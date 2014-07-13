var fib = function f(x) {
//	dumpValue(x)
	if (x <= 1)
		return 1;
	else
		return f(x-1) + f(x-2);
}

var t = fib(3);
//dumpValue(t);
var __result1 = t;  // for SAFE
var __expect1 = 3;  // for SAFE
