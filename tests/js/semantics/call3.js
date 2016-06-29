function f() {
	function ff() {return 1}
	return ff()};

var __result1 = f();
var __expect1 = 1;