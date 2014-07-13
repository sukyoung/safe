x = 0;
function spy() {return this;}
//try {throw spy} catch(spy) {spy().x = 1; dumpValue(x === 1);}
try {throw spy} catch(spy) {spy().x = 1; __result1 = (x === 1);}  // for SAFE
__expect1 = true;  // for SAFE
//dumpValue(x);
var __result2 = x;  // for SAFE
var __expect2 = 1;  // for SAFE
