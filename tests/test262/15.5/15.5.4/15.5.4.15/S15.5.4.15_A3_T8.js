  var __instance = new Number(NaN);
  __instance.substring = String.prototype.substring;
  {
    var __result1 = __instance.substring(Infinity, NaN) !== "NaN";
    var __expect1 = false;
  }
  