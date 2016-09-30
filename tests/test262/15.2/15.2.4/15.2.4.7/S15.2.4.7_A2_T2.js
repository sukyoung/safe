  {
    var __result1 = typeof Object.prototype.propertyIsEnumerable !== "function";
    var __expect1 = false;
  }
  var obj = {
    the_property : true
  };
  {
    var __result2 = typeof obj.propertyIsEnumerable !== "function";
    var __expect2 = false;
  }
  {
    var __result3 = ! (obj.propertyIsEnumerable("the_property"));
    var __expect3 = false;
  }
  var accum = "";
  for(var prop in obj)
  {
    accum += prop;
  }
  {
    var __result4 = accum.indexOf("the_property") !== 0;
    var __expect4 = false;
  }
  