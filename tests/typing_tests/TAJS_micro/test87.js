var a = {b:42, c:true}

function f(x) {
	
//	dumpState();
	a.b = x;
//	dumpState();
	
}

//dumpState();
f(17);
//dumpState();

//dumpObject(a);
var __result1 = a.b;  // for SAFE
var __expect1 = 17;  // for SAFE

var __result2 = a.c;  // for SAFE
var __expect2 = true;  // for SAFE
