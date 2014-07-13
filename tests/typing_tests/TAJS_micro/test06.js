bar = 0;
if (bar == 33)
  bar = 777;
else
  bar = "jolly";

foo = bar + 1234;

//dumpValue(foo);
//assert(foo === "jolly1234");
var __result1 = foo;  // for SAFE
var __expect1 = "jolly1234";  // for SAFE


if (Math.random() == 33)
  bar = 777;
else
  bar = "jolly";

foo = bar + 1234;

//dumpValue(foo);
//assert(foo === "jolly1234");
var __result2 = foo;  // for SAFE
var __expect2 = "jolly1234";  // for SAFE

