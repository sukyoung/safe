var x = {}
var p;
if (Math.random()) 
	p = "foo";
else
	p = "bar";
var y = x[p];
//dumpValue(y);
var __result1 = y;  // for SAFE
var __expect1 = undefined;  // for SAFE

var q;
if (y == null)
	q = true;
else
	q = false;

//dumpValue(p);
var __result2 = p;  // for SAFE
var __expect2 = "foo";  // for SAFE

var __result3 = p;  // for SAFE
var __expect3 = "bar";  // for SAFE

//dumpValue(y);
var __result4 = y;  // for SAFE
var __expect4 = undefined;  // for SAFE

//dumpValue(q);
var __result5 = q;  // for SAFE
var __expect5 = true;  // for SAFE
