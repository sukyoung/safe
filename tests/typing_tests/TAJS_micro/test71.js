var standalone = false;
try {
  document; // ReferenceError
} catch(error) {
  standalone = true;
}
//assert(standalone);
//dumpValue(standalone);
var __result1 = standalone;  // for SAFE
var __expect1 = true;  // for SAFE

