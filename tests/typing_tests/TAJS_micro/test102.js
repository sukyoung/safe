var i = 0;
var j = 799;
//dumpValue(i);
var __result1 = i;  // for SAFE
var __expect1 = 0.0;  // for SAFE

//dumpValue(j);
var __result2 = j;  // for SAFE
var __expect2 = 799.0;  // for SAFE

while (i <= j) {
//	dumpValue("foo");
	var __result3 = "foo";  // for SAFE

	i++;
} 
var __expect3 = "foo";  // for SAFE

//dumpValue(i);
var __result4 = i;  // for SAFE
var __expect4 = 800.0;  // for SAFE

//dumpValue("bar");
var __result5 = "bar";  // for SAFE
var __expect5 = "bar";  // for SAFE
