var __result1 = typeof aaa;
var __expect1 = "undefined";

if (@Top) bbb = "Hello";

var __result2 = typeof bbb;
var __expect2 = "string";

var __result3 = typeof bbb;
var __expect3 = "undefined";

var __result4;
var __expect4 = "ReferenceError";
try {
	typeof ccc.p;
} catch (e) {
	__result4 = e.name;
}
