function foo() {
	return this.x;
};

var x = 10;
var o1 = {x: "abc", foo: foo};

var __result1 = foo();
var __expect1 = 10;

var __result2 = o1.foo();
var __expect2 = "abc";
