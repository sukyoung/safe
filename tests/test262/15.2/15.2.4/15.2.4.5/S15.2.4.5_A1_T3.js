  var FACTORY = (function () 
  {
    this.aproperty = 1;
  });
  var instance = new FACTORY;
  {
    var __result1 = typeof Object.prototype.hasOwnProperty !== "function";
    var __expect1 = false;
  }
  {
    var __result2 = typeof instance.hasOwnProperty !== "function";
    var __expect2 = false;
  }
  {
    var __result3 = instance.hasOwnProperty("toString");
    var __expect3 = false;
  }
  {
    var __result4 = ! (instance.hasOwnProperty("aproperty"));
    var __expect4 = false;
  }
  