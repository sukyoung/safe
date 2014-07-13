function g() {
	throw new Error;
}

var __result1;  // for SAFE
var __result2;  // for SAFE
try{
  g();
//  dumpValue("HERE1"); // should not appear
  __result1 = "HERE1";  // for SAFE
} catch(ex) {
//  dumpValue("HERE2");
  __result2 = "HERE2";  // for SAFE
}
var __expect1 = undefined;
var __expect2 = "HERE2";

//dumpValue("HERE3");
var __result3 = "HERE3";  // for SAFE
var __expect3 = "HERE3";  // for SAFE
