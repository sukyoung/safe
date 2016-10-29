var __result1 = false
try {
	Error.prototype();
	$FAIL('#1: "Error.prototype()" lead to throwing exception');
} catch (e) {
  var __result1 = true
}
var __expect1 = true
