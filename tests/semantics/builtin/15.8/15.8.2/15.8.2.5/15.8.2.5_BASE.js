// Math.atan2
var __result1 = Math.atan2(@NumTop, 1);
var __expect1 = @NumTop;
var __result2 = Math.atan2(34, @NumTop);
var __expect2 = @NumTop;
var __result3 = Math.atan2(NaN, 3.2);
var __expect3 = NaN;
var __result4 = Math.atan2(2, NaN);
var __expect4 = NaN;
var __result5 = Math.atan2(0, 0);
var __expect5 = 0;
var __result6 = Math.atan2(0, 1);
var __expect6 = 0;
var __result7 = Math.atan2(0, -1);
var __expect7 = 3.141592653589793;
