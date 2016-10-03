function __FACTORY() 
{
  this.toString = (function () 
  {
    return "wizard";
  });
}
;
__FACTORY.prototype.charCodeAt = String.prototype.charCodeAt;
var __instance = new __FACTORY;
with (__instance)
{
  {
    var __result1 = __instance.charCodeAt(eval("1"), true, null, {
    }) !== 0x69;
    var __expect1 = false;
  }
}
