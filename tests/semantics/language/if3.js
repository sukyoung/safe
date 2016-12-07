var x = 123;

function f() {
	if(@Top)
		x = 456;
	else
		return;
}

f();

var __result1 = x;
var __expect1 = 123;

var __result2 = x;
var __expect2 = 456;
