var obj = {}; 

//dumpValue(obj == false);
var __result1 = (obj == false);  // for SAFE
var __expect1 = false;  // for SAFE

//dumpValue(obj == true);
var __result2 = (obj == true);  // for SAFE
var __expect2 = false;  // for SAFE

//dumpValue(false == obj);
var __result3 = (false == obj);  // for SAFE
var __expect3 = false;  // for SAFE

//dumpValue(true == obj);
var __result4 = (true == obj);  // for SAFE
var __expect4 = false;  // for SAFE

//assert((obj == false) === false);
//assert((obj == true) === false);
//assert((false == obj) === false);
//assert((true == obj) === false);
