function __FACTORY() 
{
  this.toString = (function () 
  {
    return "wizard";
  });
}
;
__FACTORY.prototype.charAt = String.prototype.charAt;
__instance = new __FACTORY;
with (__instance)
{
  {
    var __result1 = __instance.charAt(eval("1"), true, null, {
    }) !== "i";
    var __expect1 = false;
  }
}
  
