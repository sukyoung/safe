var x = new Date("2012/2/3");

var __result1 = x.getSeconds()
var __expect1 = 0

var y = new Date(2012, 2, 3);

var __result2 = y.getSeconds()
var __expect2 = 0

var z = new Date();

var __result3 = z.getSeconds();
var __expect3 = 10;
