var x = 1
function g() {
	return x;
};
function f(k) {
	var x = 2;
	return k();
};

var __result1 = f(g);
var __expect1 = 1;