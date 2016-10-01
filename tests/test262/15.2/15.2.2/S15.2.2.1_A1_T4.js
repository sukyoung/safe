var obj = new Object(undefined);

var __result1 = obj === undefined;
var __expect1 = false;

var __result2 = obj.constructor;
var __expect2 = Object;

var __result3 = Object.prototype.isPrototypeOf(obj);
var __expect3 = true;

var to_string_result = '[object '+ 'Object' +']';
var __result4 = obj.toString();
var __expect4 = to_string_result;

var __result5 = obj.valueOf().toString();
var __expect5 = to_string_result.toString();
