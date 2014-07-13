function person(g) {
    this.age = 5678 + g;
}
var linus = new person(6000);
var rms = linus.age;

//assert(rms == 11678);
//dumpValue(rms);
var __result1 = rms;  // for SAFE
var __expect1 = 11678;  // for SAFE

function foo(x) {
	x = x +1;
	function qwerty() {
		return x + 2;
	}
//	return qwerty() + 1234;
	return x+ 2 + 1234;
}

var bar = foo(5678);
var bar2 = foo(5678);
//dumpValue(bar);
//assert(bar == 6915);
var __result2 = bar;  // for SAFE
var __expect2 = 6915;  // for SAFE
