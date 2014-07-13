function f1(x,y) {
	return x+y;
}
//assert(f1(1,2) == 3);
var __result1 = f1(1,2);  // for SAFE
var __expect1 = 3;  // for SAFE

function f2(x,y) {
	return x+y;
}
//assert(f2(1,2,3) == 3);
var __result2 = f2(1,2,3);  // for SAFE
var __expect2 = 3;  // for SAFE

function f3(x,y) {
	return x+y;
}
//assert(isNaN(f3(1)));
var __result3 = isNaN(f3(1));  // for SAFE
var __expect3 = true;  // for SAFE

function f4(x,y) {
	return arguments[0]+arguments["1"]+arguments[2];
}
//assert(f4(1,"foo",3) == "1foo3");
var __result4 = f4(1,"foo",3);  // for SAFE
var __expect4 = "1foo3";  // for SAFE

function f5(x,y) {
	this.q = x+y;
}
//assert((new f5(1,2)).q == 3);
var __result5 = (new f5(1,2)).q;  // for SAFE
var __expect5 = 3;  // for SAFE

function f6(x,y) {
	return Object;
}
//assert((new f6(1,2)) == Object);
var __result6 = (new f6(1,2));  // for SAFE
var __expect6 = Object;  // for SAFE

function f7(x) {
	x = x + 1;
	return g();
	function g() {return x+2}
}
//assert(f7(1)==4);
//dumpValue(f7(1));
var __result7 = f7(1);  // for SAFE
var __expect7 = 4;  // for SAFE


