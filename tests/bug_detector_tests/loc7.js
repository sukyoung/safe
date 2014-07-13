var x = {f1:f}
var y = x.f1().f2().g;
function f() {
	return {f2:g}
}
function g() {}
