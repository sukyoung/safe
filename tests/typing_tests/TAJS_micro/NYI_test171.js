function f(x) {
        x = x * -1
	return arguments[0]
}

//dumpValue(f(3))
var __result1 = f(3);  // for SAFE
var __expect1 = -3;  // for SAFE

function g(x) {
        x = x * -1
	return x
}

//dumpValue(g(4))
var __result2 = g(4);  // for SAFE
var __expect2 = -4;  // for SAFE
