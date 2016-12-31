function f() { return {};}

var x = f();
var y;

while(@Top) {
	y = x;
	x = f();  // x is recent, y is old
	x.p = 1;  // strong update
	y.p = "2";// weak update
}

// x is Recent
var __result1 = (x.p == 1);
var __expect1 = true;

var __result2 = (x.p == "2");
var __expect2 = false;

// y is old
var __result3 = (y.p == "2");
var __expect3 = true;
