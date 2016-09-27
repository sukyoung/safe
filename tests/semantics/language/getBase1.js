function g() {return 1};
function f() {
	function g() {return 2};
	return g();
}

var __result1 = f();
var __expect1 = 2;
