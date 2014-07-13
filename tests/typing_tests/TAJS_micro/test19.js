var t;
if (Math.random()) 
	t = Object;
else
	t = Boolean;
var x = new t;
//dumpValue(t);
var __result1 = t;  // for SAFE
var __expect1 = Object;  // for SAFE

var __result2 = t;  // for SAFE
var __expect2 = Boolean;  // for SAFE

//dumpValue(x);
//dumpValue(Object);
var __result3 = x instanceof Object;  // for SAFE
var __expect3 = true;  // for SAFE

var __result4 = x instanceof Boolean;  // for SAFE
var __expect4 = true;  // for SAFE

//dumpValue(new Object);

x.foo = 42;
//dumpObject(x);
var __result5 = x.foo;  // for SAFE
var __expect5 = 42;  // for SAFE
