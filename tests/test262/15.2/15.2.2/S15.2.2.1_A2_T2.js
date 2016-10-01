var func = function(){return 1;};

var n_obj = new Object(func);

var __result1 = n_obj;
var __expect1 = func;

var __result2 = n_obj();
var __expect2 = 1;
