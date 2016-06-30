function f(x) {
	var y;
	y=1;
	if (x) return y;
	y=2;
	f(true);
	return y;
}

var __result1 = f(false);
var __expect1 = 2;

