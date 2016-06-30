function Foo() { };
Foo.prototype.x = 10;

var __result1 = delete Foo.prototype;
var __expect1 = false;

var __result2 = Foo.prototype.x;
var __expect2 = 10;
