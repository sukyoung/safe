var x = "foo";

function g() {
	throw new Error; // crashes WALA
	x = 1;
}

var __result1;  // for SAFE
var __result2;  // for SAFE
var __result3;  // for SAFE
try{
//	  dumpValue("HERE1");
	__result1 = "HERE1";  // for SAFE
  g();
//  dumpValue("HERE2");
  __result2 = "HERE2";  // for SAFE
} catch(ex) {
//  dumpValue("HERE3");
  __result3 = "HERE3";  // for SAFE
}
var __expect1 = "HERE1";
var __expect2 = undefined;
var __expect3 = "HERE3";

//dumpValue(x);
var __result4 = x;  // for SAFE
var __expect4 = "foo";  // for SAFE
