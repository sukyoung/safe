function foo() {
	return 10;
};

var bar = foo;
foo();

var __result1 = bar();
var __expect1 = 10;
