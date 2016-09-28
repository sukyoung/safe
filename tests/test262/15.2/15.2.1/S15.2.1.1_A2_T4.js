var obj = {flag:true};

var __result1 = typeof(obj);
var __expect1 = 'object';

var n_obj = Object(obj);

var __result2 = ((n_obj !== obj)||(!(n_obj['flag'])));
var __expect2 = false;
