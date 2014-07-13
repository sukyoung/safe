var glob = 42;
try {
//    print ("Outter try");
    try {
//	print ("First nested");
	if (Math.random()) 
	    throw 30;
	else 
	    var x = 10;
    } catch (ee) {
	glob = "42";
//	dumpValue(ee);
	__result1 = ee;  // for SAFE
	throw "" + ee;
    }
    throw {ex: "yes"};
} catch (e) {
//    dumpValue(e);
    __result2 = e;  // for SAFE
//    dumpObject(e);
    __result3 = e.ex;  // for SAFE
}
__expect1 = 30;  // for SAFE
__expect2 = "30";  // for SAFE
__expect3 = "yes";  // for SAFE

//dumpValue(glob);
var __result4 = glob;  // for SAFE
var __expect4 = 42;  // for SAFE

var __result5 = glob;  // for SAFE
var __expect5 = "42";  // for SAFE
