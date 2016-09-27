var arr1 = [0,1,2];
var arr2 = arr1.slice();

var __result1 = arr2[0];
var __expect1 = 0;

var __result2 = arr2[1];
var __expect2 = 1;

var __result3 = arr2[2];
var __expect3 = 2;

var __result4 = arr2[3];
var __expect4 = undefined;

var x = [1,2,3,4,5];

var x5 = x.slice(2,4);
var __result5 = x5.length;
var __expect5 = 2;

var __result6 = x5.toString();
var __expect6 = "3,4";

var x6 = x.slice(4,2);
var __result7 = x6.length;
var __expect7 = 0;

var __result8 = x6.toString();
var __expect8 = "";

var x7 = x.slice(-3,-1);
var __result9 = x7.length;
var __expect9 = 2;

var __result10 = x7.toString();
var __expect10 = "3,4";
