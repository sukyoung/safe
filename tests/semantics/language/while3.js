var x = 123;
var i;

for(i = 1; i; i--) {
	x = 456;
	break;
	x = 789;
}

var __result1 = x;
var __expect1 = 456;

var __result2 = i;
var __expect2 = 1;
