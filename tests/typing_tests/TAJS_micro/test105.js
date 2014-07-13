function F(a) {
  this.foo = a;
}

var x = new F(117)
var y = new F(87);

//dumpValue(x.foo);
var __result1 = x.foo;  // for SAFE
var __expect1 = 117.0;  // for SAFE

//dumpValue(y.foo);
var __result2 = y.foo;  // for SAFE
var __expect2 = 87.0;  // for SAFE
