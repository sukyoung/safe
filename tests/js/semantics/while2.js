var x = 123;

do {
	x = 456;
	break;
	x = 789;
} while(__TOP)

var __result2 = x;
var __expect2 = 456;
