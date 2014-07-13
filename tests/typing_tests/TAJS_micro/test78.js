try {
    if (Math.random())
	throw "String"
    else
	throw 10;
} catch (e) {
	__result1 = e;
	__result2 = e;
//    dumpValue(e);  // for SAFE
}
__expect1 = "String";  // for SAFE
__expect2 = 10;  // for SAFE
