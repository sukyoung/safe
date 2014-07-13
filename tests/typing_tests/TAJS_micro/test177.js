function highest(){ 
  return makeArray(arguments)
} 
 
function makeArray(array){ 
//  dumpValue(array)
	__result1 = array[0];  // for SAFE
	__result2 = array[1];  // for SAFE
	__result3 = array[2];  // for SAFE
	__result4 = array[3];  // for SAFE
	__result5 = array.length;  // for SAFE
	__result6 = array.callee;  // for SAFE

  return Array().slice.call( array ); 
} 
__expect1 = 1;  // for SAFE
__expect2 = 1;  // for SAFE
__expect3 = 2;  // for SAFE
__expect4 = 3;  // for SAFE
__expect5 = 4;  // for SAFE
__expect6 = highest;  // for SAFE

//dumpObject(highest(1, 1, 2, 3)); 
var arr = highest(1, 1, 2, 3);  // for SAFE

var __result7 = arr[0];  // for SAFE
var __expect7 = 1;  // for SAFE

var __result8 = arr[1];  // for SAFE
var __expect8 = 1;  // for SAFE

var __result9 = arr[2];  // for SAFE
var __expect9 = 2;  // for SAFE

var __result10 = arr[3];  // for SAFE
var __expect10 = 3;  // for SAFE

var __result11 = arr.length;  // for SAFE
var __expect11 = 4;  // for SAFE

var __result12 = arr.callee;  // for SAFE
var __expect12 = undefined;  // for SAFE
