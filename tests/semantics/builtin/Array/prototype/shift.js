var x = [1,2,3];

var __result1 = x.shift();
var __expect1 = 1;

var __result2 = x.toString();
var __expect2 = "2,3";

var __result3 = x.length;
var __expect3 = 2

var __result4 = x.shift();
var __expect4 = 2

var __result5 = x.length;
var __expect5 = 1

var __result6 = x.shift();
var __expect6 = 3

var __result7 = x.length;
var __expect7 = 0

var __result8 = x.shift();
var __expect8 = undefined

var __result9 = x.length;
var __expect9 = 0
