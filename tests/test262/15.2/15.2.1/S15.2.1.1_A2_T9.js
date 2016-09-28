var __result1 = typeof func;
var __expect1 = 'function';

var n_obj = Object(func);

var __result2 = ((n_obj !== func)||(n_obj()!==1));
var __expect2 = false;

function func(){return 1;};
