function f() {
	try {
		try {
			return 1;
		} finally {
			throw 0; // @return must be reset.
		}
	} catch(e) {
	}
	// return value is undefined.
}

var __result1 = f();
var __expect1 = undefined;

