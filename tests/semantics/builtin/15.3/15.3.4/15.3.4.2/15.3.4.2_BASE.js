var f = function () { return 1; };
var receiver = f.toString;

var __result1;
var __expect1 = true;

try {
    receiver();
} catch (e) {
    __result1 = e instanceof @TypeErr;
}

var __result2 = f.toString();
var __expect2 = "function () { return 1; }";
