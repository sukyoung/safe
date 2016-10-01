var __instance = new Object(42);
__instance.charAt = String.prototype.charAt;
var __result1 = __instance.charAt(false) + __instance.charAt(true) !== "42";
var __expect1 = false;
