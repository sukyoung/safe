var x;
var b = true;

while(b) {
	x = 123;
	b = false;
	continue;
	x = 456;
	b = true;
}

var __result1 = x;
var __expect1 = 123;
