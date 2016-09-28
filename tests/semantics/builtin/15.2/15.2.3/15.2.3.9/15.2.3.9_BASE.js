var o = {p1:1, p2:2};

var x = Object.freeze(o);

var __result1 = delete x.p1;
var __expect1 = false;

var __result2 = delete x.p2;
var __expect2 = false;

x.p1 = 111;
var __result3 = x.p1;
var __expect3 = 1;

x.p2 = 222;
var __result4 = x.p2;
var __expect4 = 2;

x.p3 = 3;
var __result5 = x.p3;
var __expect5 = undefined;
