  var __instance = new Object(42);
  __instance.charCodeAt = String.prototype.charCodeAt;
  {
    var __result1 = (__instance.charCodeAt(false) !== 52) || (__instance.charCodeAt(true) !== 50);
    var __expect1 = false;
  }
  