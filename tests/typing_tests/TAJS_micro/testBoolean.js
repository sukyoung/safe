var a = new Boolean();
//dumpObject(a);
var b = new Boolean(36984);
//dumpObject(b);
//dumpValue(b.xy);
var __result1 = b.xy;  // for SAFE
var __expect1 = undefined;  // for SAFE

var c = new Boolean(true);
//dumpObject(c);
var d = Boolean("false");
//dumpValue(d);
var __result2 = d;  // for SAFE
var __expect2 = true;  // for SAFE

var e = Boolean("");
//dumpValue(e);
var __result3 = e;  // for SAFE
var __expect3 = false;  // for SAFE

var f = b.toString();
//dumpValue(f);
var __result4 = f;  // for SAFE
var __expect4 = "true";  // for SAFE

var g = c.valueOf();
//dumpValue(g)
var __result5 = g;  // for SAFE
var __expect5 = true;  // for SAFE

var h = new Number(16384);

var __result6;  // for SAFE
try {  // for SAFE
	h.booString = a.toString;
	h.booString();
} catch (e) {  // for SAFE
	__result6 = e.name;  // for SAFE
}
var __expect6 = "TypeError";  // for SAFE

var __result7;  // for SAFE
try {  // for SAFE
	h.valuOf = a.valueOf;
	h.valuOf();
} catch (e) {  // for SAFE
	__result7 = e.name;  // for SAFE
}
var __expect7 = "TypeError";  // for SAFE

b.boo = a.toString;
var i = b.boo();
//dumpValue(i);
var __result8 = i;  // for SAFE
var __expect8 = "true";  // for SAFE


