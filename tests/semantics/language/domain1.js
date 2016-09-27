// FAIL: Fails because UInt range is 2^31, but '*' doesn't impose the bound on UInt values.
// NOTE: '*' on singleton values should apply alpha function to the result.

var __result1 = 2000000000 * 2;
var __expect1 = 4000000000;
