var foo = 42;
foo = foo;
function f() {
	return foo;
}
//dumpValue(f());
var __result1 = f();  // for SAFE
var __expect1 = 42;  // for SAFE

//assert(this.foo == 42);
//dumpValue(this.foo);
var __result2 = this.foo;  // for SAFE
var __expect2 = 42;  // for SAFE
