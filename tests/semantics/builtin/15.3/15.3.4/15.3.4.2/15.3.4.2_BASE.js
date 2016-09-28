var f = function () { return 1; };
var receiver = f.toString;

var __result1;
var __expect1 = __TypeErrLoc;

try {
	receiver();
} catch (e) {
	__result1 = e;
}

var __result2 = f.toString();
var __expect2 = "function () { return 1; }";
