var foo = function() {
	this.baz = 42;
} 

var x1 = new foo();
var x2 = new foo();

//dumpValue(x1.baz);
var __result1 = x1.baz;  // for SAFE
var __expect1 = 42;  // for SAFE

//dumpValue(x2.baz);
var __result2 = x2.baz;  // for SAFE
var __expect2 = 42;  // for SAFE
