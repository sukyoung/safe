function Foo() { };
Foo.prototype.x = 10;
Foo.prototype = 20;

var __result1 = Foo.prototype;
var __expect1 = 20;
