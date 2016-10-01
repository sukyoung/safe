x = 10;
try {
	throw 20;
} catch (x) {
	var x = 30;
	var __result1 = x;
	var __expect1 = 30;
}

var __result2 = x;
var __expect2 = 10;
