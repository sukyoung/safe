x = 1;

if (@BoolTop) {
	var __result1 = delete x;
	var __expect1 = true;
} else {
	x = 2;
}

var __result2 = x;
var __expect2 = 2;
