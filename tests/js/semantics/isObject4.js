function Foo() {
	this.x = 10;
}

function Fun() {
	this.y = 20;
}
Fun.prototype = new Foo;


var o = new Fun();

var __result1 = o.x;
var __expect1 = 10;

var __result2 = o.y;
var __expect2 = 20;
