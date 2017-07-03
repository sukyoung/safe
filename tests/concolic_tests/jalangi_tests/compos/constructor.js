function C(x) {
    this.x = x;
}

function f(o) {
	if (o.x === 100) {
			_<>_print("1");
	} else {
			_<>_print("2");
	}
	_<>_print("3");
}

var o = new C(0);
f(o);
