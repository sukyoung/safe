var x = ["a","b","c"];

for (var i = 0; i < x.length; i++) {
//	dumpValue(i);
	__result1 = i;  // for SAFE
}
var __expect1 = 2;  // for SAFE

var q1 = 0, q2 = 1;
var w;
if (Math.random())
	w1 = q1;
else
	w1 = q2;
//dumpValue(w1);
var __result2 = w1;  // for SAFE
var __expect2 = 0;  // for SAFE

var __result3 = w1;  // for SAFE
var __expect3 = 1;  // for SAFE

var w2 = w1 + 1;
//dumpValue(w2);
var __result4 = w2;  // for SAFE
var __expect4 = 1;  // for SAFE

var __result5 = w2;  // for SAFE
var __expect5 = 2;  // for SAFE

//dumpValue(w1.toString());
var __result6 = w1.toString();  // for SAFE
var __expect6 = "0";  // for SAFE

var __result7 = w1.toString();  // for SAFE
var __expect7 = "1";  // for SAFE

//dumpValue(w2.toString());
var __result8 = w2.toString();  // for SAFE
var __expect8 = "1";  // for SAFE

var __result9 = w2.toString();  // for SAFE
var __expect9 = "2";  // for SAFE
