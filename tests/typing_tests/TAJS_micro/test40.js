//Setting length with a field ref.
var gt = new Array(1,2,3,4,5,6,7,8,9)
var a ="gth";
gt["len" + a] = 3;
//assert(gt.length == 3)
var __result1 = gt.length;  // for SAFE
var __expect1 = 3;  // for SAFE

//dumpObject(gt)
var __result2 = gt[8];  // for SAFE
var __expect2 = undefined;  // for SAFE

//Change length by simple assign
var arr = new Array(0,1,2,3,4,5,6,7,8,9)
//assert(arr.length == 10);
var __result3 = arr.length;  // for SAFE
var __expect3 = 10;  // for SAFE

arr[3] = 1000;
//assert(arr.length == 10)
var __result4 = arr.length;  // for SAFE
var __expect4 = 10;  // for SAFE

arr[42] = 1000;
//assert(arr.length == 43)
var __result5 = arr.length;  // for SAFE
var __expect5 = 43;  // for SAFE

//dumpObject(arr)
