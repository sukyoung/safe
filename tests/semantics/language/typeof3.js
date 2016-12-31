var x;

if (@Top) {
	x = {};
	var __result1 = typeof x;
	var __expect1 = "object";
}
else if (@Top) {
	x = [];
	var __result2 = typeof x;
	var __expect2 = "object";
}
else {
	x = function () {return 1};
	var __result3 = typeof x;
	var __expect3 = "function";
}

var __result4 = typeof x;
var __expect4 = "object";

var __result5 = typeof x;
var __expect5 = "function";
