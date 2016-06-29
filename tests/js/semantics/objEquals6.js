function f() {
	return {};
}

var o1 = f();
var o2 = f();

var __result1 = o1 == o2;
var __expect1 = false;

var __result2 = o1 === o2;
var __expect2 = false;


var __result3 = o1 != o2;
var __expect3 = true;

var __result4 = o1 !== o2;
var __expect4 = true;