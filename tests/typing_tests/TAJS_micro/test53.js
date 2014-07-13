var x = {gt: 4}
var f;
if (Math.random())
    f = "gt"
else
    f = "g" + "t";
var y  =  x[f];
//dumpValue(y)
var __result1 = y;  // for SAFE
var __expect1 = 4;  // for SAFE
