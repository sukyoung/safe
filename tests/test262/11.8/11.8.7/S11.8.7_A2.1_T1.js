  {
    var __result1 = "MAX_VALUE" in Number !== true;
    var __expect1 = false;
  }
  var x = "MAX_VALUE";
  {
    var __result2 = x in Number !== true;
    var __expect2 = false;
  }
  var y = Number;
  {
    var __result3 = "MAX_VALUE" in y !== true;
    var __expect3 = false;
  }
  var x = "MAX_VALUE";
  var y = Number;
  {
    var __result4 = x in y !== true;
    var __expect4 = false;
  }
  