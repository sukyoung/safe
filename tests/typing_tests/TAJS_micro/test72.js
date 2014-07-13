var foo1 = function() {
	this.baz1 = 42;
} 
var foo2 = function() {}

foo2.prototype = new foo1();

var xxx = new foo2();
var y1 = xxx.baz1;
//dumpValue(y1); 
var __result1 = y1;  // for SAFE
var __expect1 = 42;  // for SAFE

new foo1();

//assert(y1 === 42); 
//dumpValue(y1); 
var __result2 = y1;  // for SAFE
var __expect2 = 42;  // for SAFE
