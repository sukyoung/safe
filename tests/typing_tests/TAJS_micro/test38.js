// Array constructor test
var arr0 = new Array()
//assert(arr0.length == 0)
var __result1 = arr0.length;  // for SAFE
var __expect1 = 0;  // for SAFE

var arr1 = new Array("test")
//assert(arr1.length == 1)
var __result2 = arr1.length;  // for SAFE
var __expect2 = 1;  // for SAFE

//assert(arr1[0] == "test");
var __result3 = arr1[0];  // for SAFE
var __expect3 = "test";  // for SAFE

if (Math.random())
	var t = 42;
else	
	var t = 24;	
var arr42 = new Array(t);
//dumpObject(arr42);
var __result4 = arr42.length;  // for SAFE
var __expect4 = 42;  // for SAFE

var __result5 = arr42.length;  // for SAFE
var __expect5 = 24;  // for SAFE

var arr3 = new Array(0,1,2)
//assert(arr3.length == 3)
var __result6 = arr3.length;  // for SAFE
var __expect6 = 3;  // for SAFE

//assert(arr3[0] == 0)
var __result7 = arr3[0];  // for SAFE
var __expect7 = 0;  // for SAFE

//assert(arr3[1] == 1)
var __result8 = arr3[1];  // for SAFE
var __expect8 = 1;  // for SAFE

//assert(arr3[2] == 2)
var __result9 = arr3[2];  // for SAFE
var __expect9 = 2;  // for SAFE
