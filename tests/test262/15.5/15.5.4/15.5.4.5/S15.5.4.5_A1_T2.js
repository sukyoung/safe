  var __instance = new Boolean;
  __instance.charCodeAt = String.prototype.charCodeAt;
  {
    var __result1 = __instance.charCodeAt(false) !== 0x66;
    var __expect1 = false;
  }
  {
    var __result2 = __instance.charCodeAt(true) !== 0x61;
    var __expect2 = false;
  }
  {
    var __result3 = __instance.charCodeAt(true + 1) !== 0x6c;
    var __expect3 = false;
  }
  