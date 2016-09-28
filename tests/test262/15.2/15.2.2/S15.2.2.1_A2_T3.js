var arr = [1,2,3];

var n_obj = new Object(arr);

arr.push(4);

var __result1 = n_obj;
var __expect1 = arr;

var __result2 = n_obj[3];
var __expect2 = 4;
