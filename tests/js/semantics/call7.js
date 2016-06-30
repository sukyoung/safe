function g(a) {
	return a;
};
function f(g) {
	var x = 2;
	return g(x);
};

var __result1 = f(g);
var __expect1 = 2;