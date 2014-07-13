function f(x, y) {
        y = x * -1
	return arguments[1]
}

//dumpValue(f(3, 0))
var __result1 = f(3, 0);  // for SAFE
var __expect1 = -3;  // for SAFE

function g(x, y) {
        x = x * -1
	return arguments[1]
}

//dumpValue(g(4, 0))
var __result2 = g(4, 0);  // for SAFE
var __expect2 = 0;  // for SAFE
