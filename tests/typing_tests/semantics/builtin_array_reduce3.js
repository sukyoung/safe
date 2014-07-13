function f(a,v) {a.push(v.toString());return a}
var o = [1,2,3].reduce(f,[]);

var __result1 = o[0];
var __expect1 = "1";

var __result2 = o[1];
var __expect2 = "2";

var __result3 = o[2];
var __expect3 = "3";

var __result4 = o.length;
var __expect4 = 3;

