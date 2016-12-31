var o1 = [];
var o2 = [123];
var o3 = [123,456];
var o4 = [123,456,789];
var p;

if (@Top)
	p = "0" 
else
	p = "1"

var __result1 = delete o1[p];
var __expect1 = true;
var __result2 = o1[p];
var __expect2 = undefined;

var __result3 = delete o2[p];
var __expect3 = true;
var __result4 = o2[p];
var __expect4 = undefined;

var __result5 = delete o3[p];
var __expect5 = true;
var __result6 = o3[p];
var __expect6 = undefined;

var __result7 = delete o4[p];
var __expect7 = true;
var __result8 = o4[p];
var __expect8 = undefined;
var __result9 = o4[2];
var __expect9 = 789;
