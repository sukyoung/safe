  __FACTORY.prototype.substring = String.prototype.substring;
  var __instance = new __FACTORY(void 0);
  {
    var __result1 = __instance.substring(0, 100) !== "undefined";
    var __expect1 = false;
  }
  function __FACTORY(value) 
  {
    this.value = value;
    this.toString = (function () 
    {
      return this.value + '';
    });
  }
  