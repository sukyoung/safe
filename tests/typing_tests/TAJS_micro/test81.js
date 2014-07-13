try {
    if (Math.random())
	throw "Exception!"
    else
	throw Object
} catch (e) {
    try {
//	dumpValue(e)
	__result1 = e;  // for SAFE
	__result2 = e;  // for SAFE
	
	if (Math.random())
	    throw Number;
    } catch (e) {
//	dumpValue(e)
	__result3 = e;  // for SAFE
	try {
	    if (Math.random()) 
		throw e
	    else 
		var xx = e;
	} catch (e) {
//	    dumpValue(e);
	    __result4 = e;  // for SAFE
	}
    }
}
__expect1 = "Exception!";  // for SAFE
__expect2 = Object;  // for SAFE
__expect3 = Number;  // for SAFE
__expect4 = Number;  // for SAFE

//dumpValue(xx);
var __result5 = xx;  // for SAFE
var __expect5 = Number;  // for SAFE

var __result6 = xx;  // for SAFE
var __expect6 = undefined;  // for SAFE
