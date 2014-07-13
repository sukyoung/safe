var a = 1;
var b = 2;
var c;
if (a < b)
	c = 42;
var d;
if (a == b)
	d = 43;
else
	d = 87;
var e;
if (a != b)
	e = 44;
else
	e = 98;
//dumpValue(c);
var __result1 = c;  // for SAFE
var __expect1 = 42;  // for SAFE

//dumpValue(d);
var __result2 = d;  // for SAFE
var __expect2 = 87;  // for SAFE

//dumpValue(e);
var __result3 = e;  // for SAFE
var __expect3 = 44;  // for SAFE
