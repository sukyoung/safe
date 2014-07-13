function f(x) {
	if (x)
	  return "a";
	else
	  return "b";
}

var a = f(true) + f(false);
//dumpValue(a);
//assert(a === "ab");
var __result1 = a;  // for SAFE
var __expect1 = "ab";  // for SAFE

var x = {};
x[a] = "hello";

//dumpObject(x);

//dumpValue(x.dfg);
var __result2 = x.dfg;  // for SAFE
var __expect2 = undefined;  // for SAFE

//dumpValue(x[1]);
var __result3 = x[1];  // for SAFE
var __expect3 = undefined;  // for SAFE

//dumpValue(x[2]);
var __result4 = x[2];  // for SAFE
var __expect4 = undefined;  // for SAFE

//dumpValue(x.ab);
var __result5 = x.ab;  // for SAFE
var __expect5 = "hello";  // for SAFE

//assert(x.dfg === undefined);
//assert(x[1] === undefined);
//assert(x[2] === undefined);
//assert(x.ab === "hello");



