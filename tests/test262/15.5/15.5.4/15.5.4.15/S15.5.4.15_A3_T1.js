  var __instance = new Array(1, 2, 3, 4, 5);
  __instance.substring = String.prototype.substring;
  {
    var __result1 = __instance.substring(Infinity, - Infinity) !== "1,2,3,4,5";
    var __expect1 = false;
  }
  