var str = 'Luke Skywalker';

var __result1 = typeof str;
var __expect1 = 'string';

var obj = Object(str);

var __result2 = obj.constructor;
var __expect2 = String;

var __result3 = typeof obj;
var __expect3 = 'object';

var __result4 = ((obj != "Luke Skywalker")||(obj === "Luke Skywalker"));
var __expect4 = false;
