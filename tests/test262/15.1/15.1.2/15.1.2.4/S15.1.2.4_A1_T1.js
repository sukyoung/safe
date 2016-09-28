var __result1 = isNaN(NaN);
var __expect1 = true;

var __result2 = isNaN(Number.NaN);
var __expect2 = true;

var __result3 = isNaN(Number(void 0));
var __expect3 = true;

var __result4 = isNaN(void 0);
var __expect4 = true;

var __result5 = isNaN("string");
var __expect5 = true;

var __result6 = isNaN(Number.POSITIVE_INFINITY);
var __expect6 = false;

var __result7 = isNaN(Number.NEGATIVE_INFINITY);
var __expect7 = false;

var __result8 = isNaN(Number.MAX_VALUE);
var __expect8 = false;

var __result9 = isNaN(Number.MIN_VALUE);
var __expect9 = false;

var __result10 = isNaN(-0);
var __expect10 = false;

var __result11 = isNaN(true);
var __expect11 = false;

var __result12 = isNaN("1");
var __expect12 = false;
