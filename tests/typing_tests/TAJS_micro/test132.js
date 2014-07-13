function A() {}
A.prototype.count = 0;
function B() {}
B.prototype = new A;

var x = new B;
var y = new B;
//assert(x.count == 0);
var __result1 = x.count;  // for SAFE
var __expect1 = 0;  // for SAFE

//assert(y.count == 0);
var __result2 = y.count;  // for SAFE
var __expect2 = 0;  // for SAFE

x.count++;
//assert(x.count == 1);
var __result3 = x.count;  // for SAFE
var __expect3 = 1;  // for SAFE

//assert(y.count == 0);
var __result4 = y.count;  // for SAFE
var __expect4 = 0;  // for SAFE
