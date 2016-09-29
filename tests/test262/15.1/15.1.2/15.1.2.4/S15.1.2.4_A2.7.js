var __result1 = true;
var __result2;
try {
  new isNaN();
  __result1 = false;
} catch (e) {
      var __result2 = ! (e instanceof TypeError);
      var __expect2 = false;
}
var __expect1 = true;
