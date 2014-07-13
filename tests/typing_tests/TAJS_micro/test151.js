var x = {a:42}
var y = {}
var z;

var u = Math.random()

function f() {
	y[87] = x.a;
//	dumpObject(y)
	z = y[u]
//	dumpValue(z)
	__result1 = z;  // for SAFE
	__result2 = z;  // for SAFE
}
var __expect1 = 42;  // for SAFE
var __expect2 = undefined;  // for SAFE

f()

//dumpValue(y.b)
//dumpValue(z)

