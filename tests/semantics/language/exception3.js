function Class() {this.p = 1}
Class.prototype = 1;

var o = new Class();

try {
	o instanceof Class;
}
catch (e) {
	var __result1 = e.name;
	var __expect1 = "TypeError"
}