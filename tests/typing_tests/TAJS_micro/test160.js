var allResults = [ ];

function time() {
  allResults.push(42);
//  dumpObject(allResults);
  __result1 = allResults[0];  // for SAFE
}
var __expect1 = 42;  // for SAFE

time();

//dumpObject(allResults);
var __result2 = allResults[0];  // for SAFE
var __expect2 = 42;  // for SAFE
