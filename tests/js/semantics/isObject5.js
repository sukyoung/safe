function Foo() {
	this.x = 10;
}

Foo.prototype = 1;

var o = new Foo();

var __result1 = o.x;
var __expect1 = 10;
