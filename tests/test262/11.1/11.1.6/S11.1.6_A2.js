  {
    var __result1 = delete (x) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = typeof (x) !== "undefined";
    var __expect2 = false;
  }
  var object = {
    
  };
  {
    var __result3 = delete (object.prop) !== true;
    var __expect3 = false;
  }
  {
    var __result4 = typeof (object.prop) !== "undefined";
    var __expect4 = false;
  }
  