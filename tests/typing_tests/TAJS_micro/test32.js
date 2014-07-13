var erct = 42;

function foo(x) {
	var y = 6;
	function bar() {return x + y + erct}
	return bar()
}
var www = foo(12);
//dumpValue(www);
var __result1 = www;  // for SAFE
var __expect1 = 60;  // for SAFE
