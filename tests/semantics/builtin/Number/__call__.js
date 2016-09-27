var __result1 = Number(123);
var __expect1 = 123;

var __result2 = Number();
var __expect2 = 0;

var __result3 = Number(undefined);
var __expect3 = NaN;

var NaN;
var __result4 = NaN;
var __expect4 = Number(NaN);

var x = new Number(123);
var __result5 = x.valueOf();
var __expect5 = 123;

var x = 123;
var __result6 = x.valueOf();
var __expect6 = 123;

var __result7 = Number.length;
var __expect7 = 1;

var y = new Number();
var __result8 = y.valueOf(); 
var __expect8 = 0;

var __result9 = (new Number()).constructor
var __expect9 = Number.prototype.constructor
