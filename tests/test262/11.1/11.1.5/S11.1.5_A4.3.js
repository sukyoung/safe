  var object = {
    undefined : true
  };
  {
    var __result1 = object.undefined !== true;
    var __expect1 = false;
  }
  var object = {
    undefined : true
  };
  {
    var __result2 = object["undefined"] !== true;
    var __expect2 = false;
  }
  var object = {
    "true" : true
  };
  {
    var __result3 = object["true"] !== true;
    var __expect3 = false;
  }
  var object = {
    "null" : true
  };
  {
    var __result4 = object["null"] !== true;
    var __expect4 = false;
  }
  