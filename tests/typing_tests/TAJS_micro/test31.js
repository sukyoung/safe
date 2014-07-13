function foo(x,y) {
	this.baz = y;
	return x + y;
}
bar2 = this.foo(42,88,123);
//dumpValue(bar2);
var __result1 = bar2;  // for SAFE
var __expect1 = 130;  // for SAFE

//dumpValue(baz);
var __result2 = baz;  // for SAFE
var __expect2 = 88;  // for SAFE
