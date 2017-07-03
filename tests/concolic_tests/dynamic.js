function x() {
}
x.prototype.g = function() {
}

function f(x) {
	if (x.id == 0) {
		x.g();
	}
}
y = new x();
y.id = 3;
f(y);


