var t1 = Object();
var t2 = Object(null);
var t3 = Object(undefined);
var t4 = Object(1, 2);
var t5 = Object("foo");

//dumpObject(t1);
var __result1 = t1 instanceof Object;  // for SAFE
var __expect1 = true;  // for SAFE

//dumpObject(t2);
var __result2 = t2 instanceof Object;  // for SAFE
var __expect2 = true;  // for SAFE

//dumpObject(t3);
var __result3 = t3 instanceof Object;  // for SAFE
var __expect3 = true;  // for SAFE

//dumpObject(t4);
var __result4 = t4 instanceof Number;  // for SAFE
var __expect4 = true;  // for SAFE

//dumpObject(t5);
var __result5 = t5 instanceof String;  // for SAFE
var __expect5 = true;  // for SAFE

//dumpValue(t1);
//dumpValue(t2);
//dumpValue(t3);
//dumpValue(t4);
//dumpValue(t5);



var t6 = new Object();
var t7 = new Object(null);
var t8 = new Object(undefined);
var t9 = new Object(1, 2);
var t10 = new Object("foo");

//dumpObject(t6);
var __result6 = t6 instanceof Object;  // for SAFE
var __expect6 = true;  // for SAFE

//dumpObject(t7);
var __result7 = t7 instanceof Object;  // for SAFE
var __expect7 = true;  // for SAFE

//dumpObject(t8);
var __result8 = t8 instanceof Object;  // for SAFE
var __expect8 = true;  // for SAFE

//dumpObject(t9);
var __result9 = t9 instanceof Number;  // for SAFE
var __expect9 = true;  // for SAFE

//dumpObject(t10);
var __result10 = t10 instanceof String;  // for SAFE
var __expect10 = true;  // for SAFE

//dumpValue(t6);
//dumpValue(t7);
//dumpValue(t8);
//dumpValue(t9);
//dumpValue(t10);

var x1 = t8.toString();
var x2 = t9.toString();
var x3 = t10.toString();

var x4 = t8.valueOf();
var x5 = t9.valueOf();
var x6 = t10.valueOf();

//dumpValue(x1);
var __result11 = x1;  // for SAFE
var __expect11 = "[object Object]";  // for SAFE

//dumpValue(x2);
var __result12 = x2;  // for SAFE
var __expect12 = "1";  // for SAFE

//dumpValue(x3);
var __result13 = x3;  // for SAFE
var __expect13 = "foo";  // for SAFE

//dumpValue(x4);
var __result14 = x4;  // for SAFE
var __expect14 = t8;  // for SAFE

//dumpValue(x5);
var __result15 = x5;  // for SAFE
var __expect15 = 1;  // for SAFE

//dumpValue(x6);
var __result16 = x6;  // for SAFE
var __expect16 = "foo";  // for SAFE

