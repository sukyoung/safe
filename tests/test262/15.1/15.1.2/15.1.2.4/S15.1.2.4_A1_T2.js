var __result1 = isNaN({});
var __expect1 = true;

var __result2 = isNaN(new String("string"));
var __expect2 = true;

var __result3 = isNaN(new String("string"));
var __expect3 = true;

var __result4 = isNaN(new String("1"));
var __expect4 = true;

var __result5 = isNaN(new Number(1));
var __expect5 = true;

var __result6 = isNaN(new Number(NaN));
var __expect6 = true;

var __result7 = isNaN(new Boolean(true));
var __expect7 = true;
