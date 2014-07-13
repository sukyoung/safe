
function ex() {
    x = 10;
    try {
	while (Math.random()) {
	    x = x + 1;
	    if (Math.random()) 
		throw "Exception!";
	}
    } catch (ee) {
	throw ee;
    }
    return 12345;
}

function f() {
    try {
	try {
	    var xx = ex();
	} catch (e) {
	    throw e;
	}
    } catch (ee) {
	throw ee;
    }
    return xx;
}

try {
    var ret = f() // If f throws an exception ret is undef. 
//    dumpValue(ret); //At this point ret should *not* be undef.
    __result1 = ret;  // for SAFE
    
} catch(e) {
//    dumpValue(e);
    __result2 = e;  // for SAFE
}
__expect1 = 12345;  // for SAFE
__expect2 = "Exception!";  // for SAFE

//dumpValue(ret) // ret might be undef here. 
var __result3 = ret;  // for SAFE
var __expect3 = 12345;  // for SAFE

var __result4 = ret;  // for SAFE
var __expect4 = undefined;  // for SAFE


