function a() {
	this.id = 3;
}
a.prototype.f = function (x) {
	if (this.id == 0)
		x.id += 1;
}
var x = new a();
x.f(x);
