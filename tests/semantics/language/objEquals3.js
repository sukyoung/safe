var o1 = {a:1};
var o2;


if (@Top) o2 = o1; else o2 = {a:2};

var __result1 = o1 == o2;
var __expect1 = true;

var __result2 = o1 == o2;
var __expect2 = false;

var __result3 = o1 === o2;
var __expect3 = true;

var __result4 = o1 === o2;
var __expect4 = false;


var __result5 = o1 != o2;
var __expect5 = false;

var __result6 = o1 != o2;
var __expect6 = true;

var __result7 = o1 !== o2;
var __expect7 = false;

var __result8 = o1 !== o2;
var __expect8 = true;
