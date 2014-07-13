// String objects
// 15.5.1.1
var a = String();
var b = String(true);
var c = String(77);

//dumpValue(a);
var __result1 = a;  // for SAFE
var __expect1 = "";  // for SAFE

//dumpValue(b);
var __result2 = b;  // for SAFE
var __expect2 = "true";  // for SAFE

//dumpValue(c);
var __result3 = c;  // for SAFE
var __expect3 = "77";  // for SAFE

//dumpValue(c.valueOf());
var __result4 = c.valueOf();  // for SAFE
var __expect4 = "77";  // for SAFE

var d = new String();
var e = new String(false);
var f = new String(77.77);

//dumpValue(d);
//dumpValue(d.valueOf());
var __result5 = d.valueOf();  // for SAFE
var __expect5 = "";  // for SAFE

//dumpValue(d.length);
var __result6 = d.length;  // for SAFE
var __expect6 = 0;  // for SAFE

//dumpValue(e);
//dumpValue(e.valueOf());
var __result7 = e.valueOf();  // for SAFE
var __expect7 = "false";  // for SAFE

//dumpValue(e.toUpperCase());
var __result8 = e.toUpperCase();  // for SAFE
var __expect8 = "FALSE";  // for SAFE

//dumpValue(e.length);
var __result9 = e.length;  // for SAFE
var __expect9 = 5;  // for SAFE

//dumpValue(f);
//dumpValue(f.toString());
var __result10 = f.toString();  // for SAFE
var __expect10 = "77.77";  // for SAFE

//dumpValue(f.length);
var __result11 = f.length;  // for SAFE
var __expect11 = 5;  // for SAFE

//dumpValue(String.fromCharCode());
var __result12 = String.fromCharCode();  // for SAFE
var __expect12 = "";  // for SAFE

//dumpValue(String.fromCharCode(65, 66, 67, 68, 69, 70));
var __result13 = String.fromCharCode(65, 66, 67, 68, 69, 70);  // for SAFE
var __expect13 = "ABCDEF";  // for SAFE

//dumpValue(f.charAt(2));
var __result14 = f.charAt(2);  // for SAFE
var __expect14 = ".";  // for SAFE

//dumpValue(f.charAt(140));
var __result15 = f.charAt(140);  // for SAFE
var __expect15 = "";  // for SAFE

//dumpValue(f.charCodeAt(2));
var __result16 = f.charCodeAt(2);  // for SAFE
var __expect16 = 46;  // for SAFE

//dumpValue(f.charCodeAt(140));
var __result17 = f.charCodeAt(140);  // for SAFE
var __expect17 = NaN;  // for SAFE

var g = new Number(42.125);
g.charCodeAt = f.charCodeAt;
//dumpValue(g.charCodeAt(3));
var __result18 = g.charCodeAt(3);  // for SAFE
var __expect18 = 49;  // for SAFE

//dumpValue(f.concat(e, "finish"));
var __result19 = f.concat(e, "finish");  // for SAFE
var __expect19 = "77.77falsefinish";  // for SAFE

g.concat = f.concat;
//dumpValue(g.concat(g));
var __result20 = g.concat(g);  // for SAFE
var __expect20 = "42.12542.125";  // for SAFE

var h = new String("testing indexOf");
//dumpValue(h.indexOf("i"));
var __result21 = h.indexOf("i");  // for SAFE
var __expect21 = 4;  // for SAFE

//dumpValue(h.indexOf("i", 7));
var __result22 = h.indexOf("i", 7);  // for SAFE
var __expect22 = 8;  // for SAFE

//dumpValue(h.indexOf("i", 40));
var __result23 = h.indexOf("i", 40);  // for SAFE
var __expect23 = -1;  // for SAFE

g.indexOf = h.indexOf;
//dumpValue(g.indexOf("."));
var __result24 = g.indexOf(".");  // for SAFE
var __expect24 = 2;  // for SAFE

//dumpValue(h.lastIndexOf("i"));
var __result25 = h.lastIndexOf("i");  // for SAFE
var __expect25 = 8;  // for SAFE

//dumpValue(h.lastIndexOf("i", 7));
var __result26 = h.lastIndexOf("i", 7);  // for SAFE
var __expect26 = 4;  // for SAFE

//dumpValue(h.lastIndexOf("i", 1));
var __result27 = h.lastIndexOf("i", 1);  // for SAFE
var __expect27 = -1;  // for SAFE

g.lastIndexOf = h.lastIndexOf;
//dumpValue(g.lastIndexOf("."));
var __result28 = g.lastIndexOf(".");  // for SAFE
var __expect28 = 2;  // for SAFE

var i = new String("testing localeCompare");
//dumpValue(i.localeCompare(i));
var __result29 = i.localeCompare(i) === 0;  // for SAFE
var __expect29 = true;  // for SAFE

//dumpValue(i.localeCompare("test"));
var __result30 = i.localeCompare("test") === 0;  // for SAFE
var __expect30 = false;  // for SAFE

//dumpValue(i.localeCompare("utest"));
var __result31 = i.localeCompare("utest") === 0;  // for SAFE
var __expect31 = false;  // for SAFE

//dumpValue(i.localeCompare());
var __result32 = i.localeCompare() === 0;  // for SAFE
var __expect32 = false;  // for SAFE

// var j = new String("testing match");
// dumpValue(j.match(/test/));
// dumpValue(j.match(new RegExp("test")));
