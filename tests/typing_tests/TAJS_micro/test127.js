/*
 * Access to the dummy scope object created at 'catch'
 */

var scope;

try {
	throw (function() {
		return this;
	})
} catch (x) {
	scope = x(); // 'x' evaluates to a reference whose base is the dummy scope object (which is not an activation object)
}

//dumpValue(scope); // should be the dummy object
//assert(scope !== this);
var __result1 = scope;  // for SAFE
var __expect1 = this;  // for SAFE, ECMA 5th prevents this behavior 
