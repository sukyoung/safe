/*
 * Access to the 'with' object via a reference type
 */

var scope;

var foo = {f:function() {return this}}

with (foo) {
	scope = f();
}


//dumpValue(scope); // should be the foo object
//assert(scope === foo);
var __result1 = scope === foo;  // for SAFE
var __expect1 = true;  // for SAFE
