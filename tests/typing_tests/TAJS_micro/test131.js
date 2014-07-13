y = "5"
try {
	if (Math.random())
		throw "hep"
	else
		y = 5;
} catch (e) {
	if (Math.random())
		throw e
	else
		y = {};
} finally {
//	dumpValue(y)
//	dumpObject(y)
	var __result1 = y;  // for SAFE
	var __expect1 = 5;  // for SAFE

	var __result2 = y.toString();  // for SAFE
	var __expect2 = "[object Object]";  // for SAFE
}
