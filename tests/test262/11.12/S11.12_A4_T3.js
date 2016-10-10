  {
    var __result1 = ("1" ? "" : "1") !== "";
    var __expect1 = false;
  }
  var y = new String("1");
  {
    var __result2 = ("1" ? y : "") !== y;
    var __expect2 = false;
  }
  var y = new String("y");
  {
    var __result3 = (y ? y : "1") !== y;
    var __expect3 = false;
  }
  