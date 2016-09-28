var bool = true;

var __result1 = typeof bool;
var __expect1 = 'boolean';

var obj = Object(bool);

var __result2 = obj.constructor;
var __expect2 = Boolean;

var __result3 = typeof obj;
var __expect3 = 'object';

var __result4;
if (obj) { __result4 = true; }
var __expect4 = true;

var __result5 = obj === true;
var __expect5 = false;
