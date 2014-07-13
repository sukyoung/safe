var foo;
try { 
	x=null;
	y=x.a
} 
catch (e) {
	foo = 42;
}
//assert(foo===42);
var __result1 = foo;  // for SAFE
var __expect1 = 42;  // for SAFE
