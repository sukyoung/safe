var b;
var p;
var u;

if (Math.random()) {
	b = true;
	q = 42;
	p = "foo";
} else {
	b = false;
}
	
//assert(b);
//dumpValue(b);
var __result1 = b;  // for SAFE
var __expect1 = true;  // for SAFE

var __result2 = b;  // for SAFE
var __expect2 = false;  // for SAFE

//dumpValue(q);
var __result3 = q;  // for SAFE
var __expect3 = 42;  // for SAFE

//dumpValue(p);
var __result4 = p;  // for SAFE
var __expect4 = "foo";  // for SAFE

var __result5 = p;  // for SAFE
var __expect5 = undefined;  // for SAFE

//dumpValue(u);
var __result6 = u;  // for SAFE
var __expect6 = undefined;  // for SAFE
