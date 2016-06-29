var o1 = { };
o1.x = 10;
o1.f1 = function () { this.f2(); };
o1.f2 = function () { };

var x = 20;
o1.f1();

var __result1 = this.x;
var __expect1 = 20;
