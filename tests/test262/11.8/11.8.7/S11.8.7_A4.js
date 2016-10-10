  var object = {
    
  };
  object["true"] = 1;
  {
    var __result1 = true in object !== "true" in object;
    var __expect1 = false;
  }
  var object = {
    
  };
  object.Infinity = 1;
  {
    var __result2 = Infinity in object !== "Infinity" in object;
    var __expect2 = false;
  }
  var object = {
    
  };
  object.undefined = 1;
  {
    var __result3 = undefined in object !== "undefined" in object;
    var __expect3 = false;
  }
  var object = {
    
  };
  object["null"] = 1;
  {
    var __result4 = null in object !== "null" in object;
    var __expect4 = false;
  }
  