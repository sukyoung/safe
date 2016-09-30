  {
    var __result1 = typeof Object.prototype.hasOwnProperty !== "function";
    var __expect1 = false;
  }
  var obj = {
    the_property : true
  };
  {
    var __result2 = typeof obj.hasOwnProperty !== "function";
    var __expect2 = false;
  }
  {
    var __result3 = obj.hasOwnProperty("hasOwnProperty");
    var __expect3 = false;
  }
  {
    var __result4 = ! (obj.hasOwnProperty("the_property"));
    var __expect4 = false;
  }
  