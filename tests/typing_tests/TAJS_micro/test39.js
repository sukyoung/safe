var arr1 = new Array(1,2,3)
var arr2 = new Array("we", "the", "people", "of the US")

//dumpValue(arr1.toString())
var __result1 = arr1.toString();  // for SAFE
var __expect1 = "1,2,3";  // for SAFE

//dumpValue(arr2.join("--SEP--"))
var __result2 = arr2.join("--SEP--");  // for SAFE
var __expect2 = "we--SEP--the--SEP--people--SEP--of the US";  // for SAFE
