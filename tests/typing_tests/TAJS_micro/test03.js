function fooo() {
	return Math.random();
}

var x = {};

if (fooo())
	x.bar = 1234;
else
	x.bar = 2345;

//dumpValue(x.bar);
var __result1 = x.bar;  // for SAFE
var __expect1 = 1234;  // for SAFE

var __result2 = x.bar;  // for SAFE
var __expect2 = 2345;  // for SAFE
