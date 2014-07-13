var __result1 = 123;  // for SAFE
try { // duh... Rhino "optimizes" try-catch blocks by removing them if the try part is empty :-(
    var xx = 10 + 10;
//    dumpValue(xx)
} 
catch (ee) {
	__result1 = "HERE";  // for SAFE
//    dumpValue(ee) //shouldn't be printed
}
var __expect1 = 123;

//dumpValue(xx);
var __result2 = xx;  // for SAFE
var __expect2 = 20;  // for SAFE
