var __result1 = true;
var __result2;
try {
  new isNaN();
  __result1 = false;
} catch (e) {
  __result2 = e;
}
var __expect1 = true;
var __expect2 = __TypeErrLoc;
