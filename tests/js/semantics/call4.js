var x = 1
function f() {
	var x = 2
	function ff() {return x}
	return ff();
};
	
	

var __result1 = f();
var __expect1 = 2;