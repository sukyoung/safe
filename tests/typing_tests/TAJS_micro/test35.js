this.foo = function(xx) {
	this.fooo = 54;
	return xx + 42;
}

var x = this.foo(2222);
var y = this.fooo;

//assert(x == 2264);
var __result1 = x;  // for SAFE
var __expect1 = 2264;  // for SAFE

//assert(y === 54);
//dumpValue(y);
var __result2 = y;  // for SAFE
var __expect2 = 54;  // for SAFE
