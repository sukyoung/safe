try {  // for SAFE
	var x = flub(87);
} catch (e) {  // for SAFE
	__result1 = e.name;  // for SAFE
}
__expect1 = "ReferenceError";  // for SAFE

//	dumpValue(x);
var __result2 = x;  // for SAFE
var __expect2 = undefined;  // for SAFE
