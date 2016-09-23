var x1 = new Object();
var __result1 = x1.p1
var __expect1 = undefined

var x2 = new Object({p1: 1});
var __result2 = x2.p1
var __expect2 = 1

var x3 = new Object("abc");
String.prototype.y = 10;
var __result3 = x3.y;
var __expect3 = 10;

var x4 = new Object(0);
Number.prototype.y = 10;
var __result4 = x4.y;
var __expect4 = 10;

var x5 = new Object(true);
Boolean.prototype.y = 10;
var __result5 = x5.y;
var __expect5 = 10;
