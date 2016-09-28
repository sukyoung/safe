var x = 1;

var obj = this;

var n_obj = new Object(obj);

var __result1 = n_obj;
var __expect1 = obj;

var __result2 = n_obj['x'];
var __expect2 = 1;
