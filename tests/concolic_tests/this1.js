function f(x) {
	this.g = function(x) {
	}
}
var a = new f(3);
a.g(3);
