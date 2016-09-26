var x = "12345";

var __result1 = x.localeCompare(x);
var __expect1 = 0

var __result2 = x.localeCompare("12346");
var __expect2 = -1

var __result3 = x.localeCompare("12344");
var __expect3 = 1
