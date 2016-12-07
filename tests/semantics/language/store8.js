var o1 = { };
var p1;
if (@Top) {
    p1 = "A";
} else {
    p1 = "B";
}

o1[p1] = 123;

var __result1 = o1["A"];
var __expect1 = 123;

var __result2 = o1["B"];
var __expect2 = 123;

var __result3 = o1[@Top];
var __expect3 = 123;
