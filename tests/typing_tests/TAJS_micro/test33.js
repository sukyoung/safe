var foo = 42;
foo = foo + 1;

bar = 87; 
bar = bar + 2;
this.baz = 117; 
baz = baz + 3;

//dumpValue(this.foo);
var __result1 = this.foo;  // for SAFE
var __expect1 = 43;  // for SAFE

//dumpValue(this.bar);
var __result2 = this.bar;  // for SAFE
var __expect2 = 89;  // for SAFE

//dumpValue(this.baz);
var __result3 = this.baz;  // for SAFE
var __expect3 = 120;  // for SAFE
