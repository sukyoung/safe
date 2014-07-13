var f;
var a = 42;
a = a + 1;
if (Math.random())
  f = 1;//function() {return 7;}
else if (Math.random())
  f = "sdf";
else
  f = {};
//dumpValue(f);
var __result1 = f;  // for SAFE
var __expect1 = 1;  // for SAFE

var __result2 = f;  // for SAFE
var __expect2 = "sdf";  // for SAFE

var __result3 = f instanceof Object;  // for SAFE
var __expect3 = true;  // for SAFE

var __result4 = "ABC";  // for SAFE
try {
	var x = f();
	__result4 = 123;  // for SAFE
} catch(e) { } // for SAFE
var __expect4 = "ABC";  // for SAFE

//dumpValue(x);
var __result5 = x;  // for SAFE
var __expect5 = undefined;  // for SAFE
