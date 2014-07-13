// Number Objects
// 15.7.1 the Number Constructor Called as a Function
var a = Number();
var b = Number(42.66);
var c = Number(true);
//dumpValue(a);
var __result1 = a;  // for SAFE
var __expect1 = 0;  // for SAFE

//dumpValue(b);
var __result2 = b;  // for SAFE
var __expect2 = 42.66;  // for SAFE

//dumpValue(c);
var __result3 = c;  // for SAFE
var __expect3 = 1;  // for SAFE

//assert(a == 0);
//assert(b == 42.66);
//assert(c == 1);

// 15.7.2
var d = new Number();
var e = new Number(47.11);
var f = new Number(true);
//dumpValue(d);
//dumpValue(d.valueOf());
var __result5 = d.valueOf();  // for SAFE
var __expect5 = 0;  // for SAFE

//dumpValue(e);
//dumpValue(e.valueOf());
var __result7 = e.valueOf();  // for SAFE
var __expect7 = 47.11;  // for SAFE

//dumpValue(f);
//dumpValue(f.valueOf());
var __result9 = f.valueOf();  // for SAFE
var __expect9 = 1;  // for SAFE

// 15.7.3.1-6
//dumpValue(Number.prototype.valueOf());
var __result10 = Number.prototype.valueOf();  // for SAFE
var __expect10 = 0;  // for SAFE

//dumpValue(Number.MAX_VALUE);
var __result11 = Number.MAX_VALUE;  // for SAFE
var __expect11 = 1.7976931348623157e+308;  // for SAFE

//dumpValue(Number.MIN_VALUE);
var __result12 = Number.MIN_VALUE;  // for SAFE
var __expect12 = 5e-324;  // for SAFE

//dumpValue(Number.NaN);
var __result13 = Number.NaN;  // for SAFE
var __expect13 = NaN;  // for SAFE

//dumpValue(Number.NEGATIVE_INFINITY);
var __result14 = Number.NEGATIVE_INFINITY;  // for SAFE
var __expect14 = -Infinity;  // for SAFE

//dumpValue(Number.POSITIVE_INFINITY);
var __result15 = Number.POSITIVE_INFINITY;  // for SAFE
var __expect15 = Infinity;  // for SAFE

// 15.7.4
//dumpValue(e.toString());
var __result16 = e.toString();  // for SAFE
var __expect16 = "47.11";  // for SAFE

//dumpValue(b.toString());
var __result17 = b.toString();  // for SAFE
var __expect17 = "42.66";  // for SAFE

//dumpValue(b.valueOf());
var __result18 = b.valueOf();  // for SAFE
var __expect18 = 42.66;  // for SAFE

//dumpValue(b.toExponential(1));
var __result19 = b.toExponential(1);  // for SAFE
var __expect19 = "4.3e+1";  // for SAFE

//dumpValue(e.toFixed());
var __result20 = e.toFixed();  // for SAFE
var __expect20 = "47";  // for SAFE

//dumpValue(e.toExponential(4));
var __result21 = e.toExponential(4);  // for SAFE
var __expect21 = "4.7110e+1";  // for SAFE

//dumpValue(e.toPrecision(10));
var __result22 = e.toPrecision(10);  // for SAFE
var __expect22 = "47.11000000";  // for SAFE
