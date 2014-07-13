var foo1 = function bar1(x1) {
	this.baz1 = 42;
	return x1+123;
}
var foo2 = function bar2(x2) {
	this.baz2 = 43;
	return x2+124;
}

foo2.prototype = new foo1(456);

var xxx = new foo2(89);
xxx.baz3 = 44;
var y1 = xxx.baz1;
var y2 = xxx.baz2;
var y3 = xxx.baz3;

var z1 = foo1(-23);

//assert(y1 === 42);
var __result1 = y1;  // for SAFE
var __expect1 = 42;  // for SAFE

//assert(y2 === 43);
var __result2 = y2;  // for SAFE
var __expect2 = 43;  // for SAFE

//assert(y3 === 44);
var __result3 = y3;  // for SAFE
var __expect3 = 44;  // for SAFE

//assert(z1 === 100); 
var __result4 = z1;  // for SAFE
var __expect4 = 100;  // for SAFE

//dumpValue(y1); 
//dumpValue(z1);
