var x = 123;

while(@Top) {
	x = 456;
	break;
	x = 789;
}

var __result1 = x;
var __expect1 = 123;

var __result2 = x;
var __expect2 = 456;
