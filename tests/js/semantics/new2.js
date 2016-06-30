function Foo(x) {
  this.x = x;
}

var o = new Foo(10);

var __result1 = o.x;
var __expect1 = 10;

