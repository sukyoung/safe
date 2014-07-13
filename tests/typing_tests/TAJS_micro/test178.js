var obj = {}
var y = "bla";
if (obj.foo)
	y = 42;
//dumpValue(y);
var __result1 = y;  // for SAFE
var __expect1 = "bla";  // for SAFE

if (Math.random())
  obj.foo = "dsg";
if (obj.foo)
	y = true;
//dumpValue(y);
var __result2 = y;  // for SAFE
var __expect2 = true;  // for SAFE

var __result3 = y;  // for SAFE
var __expect3 = "bla";  // for SAFE
