var x0 = Infinity;
var x1 = isNaN (Infinity) + " " + isNaN(0/0);
var x2 = Math.E;
var NaN = 0/0;
var x5 = Math.hasOwnProperty();
var x5str = Math.toString();
var x6 = parseInt("  4711   ");
var x7 = this.NaN;

var y1 = Math.max(42, 77, -20, 99, -1, 111);

var ns1 = new String();

var zz = "fooooooo";

//dumpValue(x0);
var __result1 = x0;  // for SAFE
var __expect1 = Infinity;  // for SAFE

//dumpValue(x1);
var __result2 = x1;  // for SAFE
var __expect2 = "false true";  // for SAFE

//dumpValue(x2);
var __result3 = x2;  // for SAFE
var __expect3 = 2.718281828459045;  // for SAFE

//dumpValue(NaN);
var __result4 = NaN;  // for SAFE
var __expect4 = NaN;  // for SAFE

//dumpValue(x5);
var __result5 = x5;  // for SAFE
var __expect5 = false;  // for SAFE

//dumpValue(x5str);
var __result6 = x5str;  // for SAFE
var __expect6 = "[object Math]";  // for SAFE

//dumpValue(x6);
var __result7 = x6;  // for SAFE
var __expect7 = 4711.0;  // for SAFE

//dumpValue(x7);
var __result8 = x7;  // for SAFE
var __expect8 = NaN;  // for SAFE

//dumpValue(y1);
var __result9 = y1;  // for SAFE
var __expect9 = 111.0;  // for SAFE

//dumpValue(ns1);
var __result10 = ns1.valueOf();  // for SAFE
var __expect10 = "";  // for SAFE
