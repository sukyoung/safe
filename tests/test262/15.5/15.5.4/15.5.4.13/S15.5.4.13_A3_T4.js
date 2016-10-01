  __FACTORY.prototype.toString = (function () 
  {
    return this.value + '';
  });
  var __instance = new __FACTORY(void 0);
  {
    var __result1 = __instance.slice(0, 100) !== "undefined";
    var __expect1 = false;
  }
  function __FACTORY(value) 
  {
    this.value = value, this.slice = String.prototype.slice;
  }
  