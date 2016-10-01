function foo() {
	return this.x;
};

var x = 10;

var __result1 = foo();
var __expect1 = 10;
