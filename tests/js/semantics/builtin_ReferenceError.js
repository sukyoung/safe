var x = new ReferenceError("111");

var __result1 = x.name;
var __expect1 = "ReferenceError";

var __result2 = x.message;
var __expect2 = "111";

var __result3 = x.toString();
var __expect3 = "ReferenceError: 111";
