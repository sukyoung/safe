var y = [1,2,3]
if (Math.random())
    var x = 1000
else 
    var x = 0
//dumpValue(x);
var __result1 = x;  // for SAFE
var __expect1 = 1000;  // for SAFE

var __result2 = x;  // for SAFE
var __expect2 = 0;  // for SAFE
    
y.length = x;
//dumpObject(y);
var __result3 = y[0];  // for SAFE
var __expect3 = 1;  // for SAFE

var __result4 = y[1];  // for SAFE
var __expect4 = 2;  // for SAFE

var __result5 = y[2];  // for SAFE
var __expect5 = 3;  // for SAFE

var __result6 = y[0];  // for SAFE
var __expect6 = undefined;  // for SAFE

var __result7 = y[1];  // for SAFE
var __expect7 = undefined;  // for SAFE

var __result8 = y[2];  // for SAFE
var __expect8 = undefined;  // for SAFE

y[45] = 10;
//dumpObject(y);
var __result9 = y[45];  // for SAFE
var __expect9 = 10;  // for SAFE

y.length = 6;
//dumpObject(y);
var __result10 = y[45];  // for SAFE
var __expect10 = undefined;  // for SAFE
