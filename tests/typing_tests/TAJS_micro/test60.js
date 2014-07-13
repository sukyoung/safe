function yell(n) { 
    return n > 0 ? yell(n-1) + 10 : 20; 
 } 

var x = yell(4);

//dumpValue(x);
//assert(x === 60);
var __result1 = x;  // for SAFE
var __expect1 = 60;  // for SAFE

