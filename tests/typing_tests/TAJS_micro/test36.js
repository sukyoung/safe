this.foo = 42;
function q() {
	foo = "sdf";
}
//dumpValue(foo);
var __result1 = foo;  // for SAFE
var __expect1 = 42;  // for SAFE

q();
//dumpValue(foo);
//assert(foo == "sdf");
var __result2 = foo;  // for SAFE
var __expect2 = "sdf";  // for SAFE
