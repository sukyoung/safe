var x;
if (Math.random()) 
  x = null;
else if (Math.random()) 
  x = {a:42};
else
  x = {a:42, b:{c:87}};

//dumpValue(x);
var __result1 = x;  // for SAFE
var __expect1 = null;  // for SAFE

var y1 = x.a;
var y2 = x.a;
//dumpValue(y1);
var __result2 = y1;  // for SAFE
var __expect2 = 42;  // for SAFE

//dumpValue(y2);
var __result3 = y2;  // for SAFE
var __expect3 = 42;  // for SAFE

//dumpValue(x);

//dumpValue(x.b);
var __result4 = x.b;  // for SAFE
var __expect4 = undefined;  // for SAFE

var y3 = x.b.c;
var y4 = x.b.c;
//dumpValue(y3);
var __result5 = y3;  // for SAFE
var __expect5 = 87;  // for SAFE

//dumpValue(y4);
var __result6 = y4;  // for SAFE
var __expect6 = 87;  // for SAFE

//dumpValue(x.b);
