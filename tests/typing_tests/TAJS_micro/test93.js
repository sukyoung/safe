function f(x,y) {
	return x + y;
}

var a = f(1,2);
var b = f(1,"2");

//dumpValue(a);
var __result1 = a;  // for SAFE
var __expect1 = 3;  // for SAFE

//dumpValue(b);
var __result2 = b;  // for SAFE
var __expect2 = "12";  // for SAFE
