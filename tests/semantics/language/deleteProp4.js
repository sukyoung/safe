var x = {p:1};

if (@Top) {
	var __result1 = delete x.p;
	var __expect1 = true;
} else
	x.p = 2;

var __result2 = x.p
var __expect2 = 2 | undefined
