var arr = [1,2,3];

var __result1 = typeof arr;
var __expect1 = 'object';

var n_obj = Object(arr);

arr.push(4);

var __result2 = (n_obj !== arr) || (n_obj[3]!==4);
var __expect2 = false;
