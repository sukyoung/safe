var o = {p1:1, p2:2};

var x = Object.seal(o);

var __result1 = delete x.p1;
var __expect1 = false;

var __result2 = delete x.p2;
var __expect2 = false;

x.p3 = 3;
var __result3 = x.p3;
var __expect3 = undefined;
