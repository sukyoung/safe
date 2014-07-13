function fail(x) {
    throw x;
}
    g = {gt: "funny", b: 24, c:34}

var __result1;  // for SAFE
var __expect1 = "funny";  // for SAFE
var __result2;  // for SAFE
var __expect2 = "ReferenceError";  // for SAFE
try {
    with(g) {
	fail(gt)
    }
} catch (e) {
//    dumpValue(e)
	__result1 = e;
//    dumpValue(gt) //Should be absent
	try {  // for SAFE
		gt;  // for SAFE
	} catch (e2) {  // for SAFE
		__result2 = e2.name;  // for SAFE
	}  // for SAFE
}
