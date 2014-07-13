var x = {a:42};
var y = x;
function f(q) {
	var z = {garbage: q};
//	dumpValue(z.garbage);
	__result1 = z.garbage;  // for SAFE
	
	z = null;
	x.a = 7;
	x = null;
}
var __expect1 = 123;  // for SAFE

f(123);
//assert(y.a === 7);
var __result2 = y.a;  // for SAFE
var __expect2 = 7;  // for SAFE

//dumpObject(y);
//dumpState();
