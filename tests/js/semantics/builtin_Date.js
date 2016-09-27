var x = Date();

var __result1 = x
var __expect1 = "Tue Aug 2012 00:00:00 GMT+0900 (...)"

// current time
var y = Date.now();
var z = new Date(y);

var __result2 = y;
var __expect2 = 1351559847614;

var x = Date.prototype.constructor;
if (x === 1)
  Date.prototype.constructor = 2;
else
  Date.prototype.constructor = 1;

var __result4 = Date.prototype.constructor === x;
var __expect4 = false;
