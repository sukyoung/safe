var __result1 = typeof func;
var __expect1 = 'undefined';

var n_obj = Object(function func(){return 1;});

var __result2 = (n_obj.constructor !== Function) || (n_obj()!==1);
var __expect2 = false;

var __result3 = typeof func !== 'undefined';
var __expect3 = false;
