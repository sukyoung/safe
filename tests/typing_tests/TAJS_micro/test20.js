function foo(x,y) {
	this.baz = y;
	return x + y;
}
foo.id = "ABC";  // for SAFE

var t = foo;
if (Math.random()) {
  foo = t;
} else {
  foo = 44;
}
foo2 = t;
//dumpValue(foo);
var __result1 = foo.id;  // for SAFE
var __expect1 = "ABC";  // for SAFE

var __result2 = foo;  // for SAFE
var __expect2 = 44;  // for SAFE

//dumpValue(foo2);
var __result3 = foo2.id;  // for SAFE
var __expect3 = "ABC";  // for SAFE

bar1 = foo(42,87,123);
bar2 = this.foo(42,88,123);
bar3 = foo(42);
//dumpValue(bar1);
var __result4 = bar1;  // for SAFE
var __expect4 = 129;  // for SAFE

//dumpValue(bar2);
var __result5 = bar2;  // for SAFE
var __expect5 = 130;  // for SAFE

//dumpValue(bar3);
var __result6 = bar3;  // for SAFE
var __expect6 = NaN;  // for SAFE

//dumpValue(bar4);
try {  // for SAFE
	bar4;  // for SAFE
} catch(e) {  // for SAFE
	__result7 = e.name;  // for SAFE
}
__expect7 = "ReferenceError";  // for SAFE
