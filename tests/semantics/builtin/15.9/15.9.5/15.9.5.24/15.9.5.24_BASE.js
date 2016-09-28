var x = new Date("2012/2/3");

var __result1 = x.getMilliseconds()
var __expect1 = 0

var y = new Date(2012, 2, 3);

var __result2 = y.getMilliseconds()
var __expect2 = 0

var z = new Date();

var __result1 = z.getMilliseconds();
var __expect1 = 791;
