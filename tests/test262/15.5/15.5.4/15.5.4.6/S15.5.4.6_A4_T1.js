  var __instance = {
    toString : (function () 
    {
      return "one";
    })
  };
  __instance.concat = String.prototype.concat;
  {
    var __result1 = __instance.concat("two", x) !== "onetwoundefined";
    var __expect1 = false;
  }
  var x;
  