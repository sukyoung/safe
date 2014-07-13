var x = 1;
function f() {
	return 2;
	x = 3; // should be reported as unreachable
}
var y = f();
//dumpValue(y);
var __result1 = y;  // for SAFE
var __expect1 = 2;  // for SAFE

//dumpValue(x);
var __result2 = x;  // for SAFE
var __expect2 = 1;  // for SAFE
