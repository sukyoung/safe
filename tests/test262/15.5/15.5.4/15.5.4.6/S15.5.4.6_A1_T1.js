  var __instance = new Object(42);
  __instance.concat = String.prototype.concat;
  {
    var __result1 = __instance.concat(false, true) !== "42falsetrue";
    var __expect1 = false;
  }
  