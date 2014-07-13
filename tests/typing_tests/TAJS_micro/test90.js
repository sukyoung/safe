if (Math.random()) {
	xxx = 42;
}

//dumpValue(xxx);
var __result1;  // for SAFE
var __result2;  // for SAFE
var __result3;  // for SAFE
var __result4;  // for SAFE
try {  // for SAFE
	__result1 = xxx;  // for SAFE
	__result2 = xxx;  // for SAFE
} catch(e) {  // for SAFE
	__result3 = e.name;  // for SAFE
	__result4 = e.name;  // for SAFE
}
var __expect1 = 42;  // for SAFE
var __expect2 = undefined;  // for SAFE
var __expect3 = "ReferenceError";  // for SAFE
var __expect4 = undefined;  // for SAFE
