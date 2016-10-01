var obj = new Date(1978,3);

var n_obj = new Object(obj);

var __result1 = obj;
var __expect1 = n_obj;

var __result2 = (n_obj.getFullYear() !== 1978)||(n_obj.getMonth() !== 3)
var __expect2 = false;
