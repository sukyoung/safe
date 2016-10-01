function foo() {
	return this.x;
};

var o1 = {x: "abc", foo: foo};

var __result1 = o1.foo();
var __expect1 = "abc";
