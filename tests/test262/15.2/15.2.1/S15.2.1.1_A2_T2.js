var num = 1.1;

var __result1 = typeof num;
var __expect1 = 'number';

var obj = Object(num);

var __result2 = typeof obj;
var __expect2 = 'object';

var __result3 = obj.constructor;
var __expect3 = Number;

var __result4 = ((obj != 1.1)||(obj === 1.1));
var __expect4 = false;
