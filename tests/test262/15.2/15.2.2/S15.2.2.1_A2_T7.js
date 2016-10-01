var __result1 = typeof func;
var __expect1 = 'undefined';

var n_obj = new Object(function func(){return 1;});

var __result2 = n_obj.constructor;
var __expect2 = Function;

var __result3 = n_obj();
var __expect3 = 1;

var __result4 = typeof func;
var __expect4 = 'undefined';
