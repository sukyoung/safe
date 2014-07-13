var foo = {a:42, b:foo, c:this, d:Math};
//dumpObject(foo);
var __result1 = foo.a;  // for SAFE
var __expect1 = 42;  // for SAFE

var __result2 = foo.b;  // for SAFE
var __expect2 = undefined;  // for SAFE

var __result3 = foo.c;  // for SAFE
var __expect3 = this;  // for SAFE

var __result4 = foo.d;  // for SAFE
var __expect4 = Math;  // for SAFE

//dumpValue(this);

function Qwe() {
	this.bar = 42;
}

var x = new Qwe();
//dumpValue(x.bar);
var __result5 = x.bar;  // for SAFE
var __expect5 = 42;  // for SAFE

try {  // for SAFE
	baz = {a:baz} // ReferenceError
} catch(e) {  // for SAFE
	__result6 = e.name;  // for SAFE
}
__expect6 = "ReferenceError";  // for SAFE
