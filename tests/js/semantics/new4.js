function Foo(x) {
  this.x = x;
}
Foo.prototype.bar = function(x) {
  this.y = x;
}

var o = new Foo(10);
o.bar(20);

var __result1 = o.x;
var __expect1 = 10;
var __result2 = o.y;
var __expect2 = 20;

