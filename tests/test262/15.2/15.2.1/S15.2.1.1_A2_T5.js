var num = NaN;

var __result1 = typeof num;
var __expect1 = 'number';

var obj = Object(num);

var __result2 = obj.constructor;
var __expect2 = Number;

var __result3 = typeof obj;
var __expect3 = 'object';
