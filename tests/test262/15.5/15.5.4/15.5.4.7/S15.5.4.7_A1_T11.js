  var __instance = new Date(0);
  __instance.indexOf = String.prototype.indexOf;
  {
    var __result1 = (__instance.getTimezoneOffset() > 0 ? __instance.indexOf('31') : __instance.indexOf('01')) !== 8;
    var __expect1 = false;
  }
  