function f1() { return this; }

var f1b = f1.bind({x:1});
var __result1 = f1b().x;
var __expect1 = 1;

function f2(x, y) { return x + y; }

var f2b = f2.bind(null, 10);
var __result2 = f2b(3);
var __expect2 = 13;

function id(x) { return x; }

var two = id.bind(null, 2);
if (Math.random < 1) {
  two = f2.bind(null, 1, 1);
}
var __result3 = two();
var __expect3 = 2;