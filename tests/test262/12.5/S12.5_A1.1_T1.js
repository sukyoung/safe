var __result1 = true;
if (0) {
    var __result1 = false;
}
var __expect1 = true;

var __result2 = true;
if (false) {
    var __result2 = false;
}
var __expect2 = true;

var __result3 = true;
if (null) {
    var __result3 = false;
}
var __expect3 = true;

var __result4 = true;
if (undefined) {
    var __result4 = false;
}
var __expect4 = true;

var __result5 = true;
if ("") {
    var __result5 = false;
}
var __expect5 = true;

var __result6 = true;
if (NaN) {
    var __result6 = false;
}
var __expect6 = true;
