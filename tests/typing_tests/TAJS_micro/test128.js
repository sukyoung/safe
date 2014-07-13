/*
 * Access to the dummy scope object created at recursive functions.
 */

var scope;


var f = function g(x) {
//	dumpValue(x)
	__result1 = x;
	if (x == 0) {
		return this;
	}
	else {
		scope = g(0); // 'g' evaluates to a reference whose base is the dummy scope object created for the recursive function
	}
}
var __expect1 = 0;  // for SAFE
f(1);


//dumpValue(scope); // should be the dummy object
//assert(scope !== this);
var __result2 = scope;  // for SAFE
var __expect2 = this;  // for SAFE, ECMA 5th prevents this behavior 
