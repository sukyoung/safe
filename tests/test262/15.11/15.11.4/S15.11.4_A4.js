var __result1 = false;
try {
	__instance = new Object.prototype;
	$FAIL('#1: "__instance = new Object.prototype" lead to throwing exception');
} catch (e) {
    var __result1 = true;
}
var __expect1 = true;
