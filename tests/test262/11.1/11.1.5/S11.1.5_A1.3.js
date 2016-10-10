  var object = {
    "x" : true
  };
  {
    var __result1 = typeof object !== "object";
    var __expect1 = false;
  }
  {
    var __result2 = object instanceof Object !== true;
    var __expect2 = false;
  }
  {
    var __result3 = object.toString !== Object.prototype.toString;
    var __expect3 = false;
  }
  {
    var __result4 = object["x"] !== true;
    var __expect4 = false;
  }
  {
    var __result5 = object.x !== true;
    var __expect5 = false;
  }
  