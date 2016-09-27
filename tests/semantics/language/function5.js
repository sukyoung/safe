var f = function() {
}

var g = function() {
}

g.prototype.bar = function() {
	this.y = 20;
}

f.prototype = g.prototype;

var o = new f();
o.bar();

var __result1 = o.y;
var __expect1 = 20;

