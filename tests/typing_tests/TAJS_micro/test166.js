// Test string concatenation of non-identifier strings and unsigned ints.
y = "z ="

if (Math.random()) {
   x = 4
} else {
   x = 5
}

//dumpValue(y + x)
var __result1 = y + x;  // for SAFE
var __expect1 = "z =4";  // for SAFE

var __result2 = y + x;  // for SAFE
var __expect2 = "z =5";  // for SAFE
