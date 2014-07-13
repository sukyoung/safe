function f() {
}

function g() {
	var ttttt = x;
//	dumpValue(ttttt);
	__result1 = ttttt;  // for SAFE
	
	x = f();
//	dumpValue(ttttt); // should be same as above
	__result2 = ttttt;  // for SAFE
}
var __expect1 = 42;  // for SAFE
var __expect2 = 42;  // for SAFE

var x = "dyt";
g();
x = 42;
g();
