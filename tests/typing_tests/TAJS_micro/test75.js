var standalone = false;
try {
  document;
} catch(error) {
  standalone = true;
} finally {
  foo = 42;
}

//assert(standalone);
var __result1 = standalone;  // for SAFE
var __expect1 = true;  // for SAFE

//assert(foo === 42);
var __result2 = foo;  // for SAFE
var __expect2 = 42;  // for SAFE

//dumpValue(standalone);
//dumpValue(foo);