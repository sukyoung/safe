function f1() { return this; }

var f1b = f1.bind({x:1});
var __result1 = f1b().x;
var __expect1 = 1;

function f2(x, y) { return x + y; }

var f2b = f2.bind(null, 10);
var __result2 = f2b(3);
var __expect2 = 13;

function f3() { this.p = 3; }

var f3b = f3.bind(null);
var obj1 = new f3b();
var __result3 = obj1.p;
var __expect3 = 3;

function f4(x) { return { p: x }; }

var f4b = f4.bind(null, 4);
var obj2 = new f4b();
var __result4 = obj2.p;
var __expect4 = 4;

function id(x) { return x; }

var foo = id.bind(null, 2);
if (Math.random < 1) {
  foo = function two() { return 2; }
}
var __result5 = foo();
var __expect5 = 2;


