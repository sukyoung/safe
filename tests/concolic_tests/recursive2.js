function obj() {}

obj.prototype.f = function(x) {
	if (x != 0)
		this.f(x-1)
}

var o = new obj();
o.f(1);
