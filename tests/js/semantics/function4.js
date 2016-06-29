Function.prototype.foo = function() {
	this.x = 10;
}

var f = function() {
}

f.foo();

var __result1 = f.x;
var __expect1 = 10;

