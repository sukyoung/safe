  {
    var __result1 = (true ? false : true) !== false;
    var __expect1 = false;
  }
  var y = new Boolean(true);
  {
    var __result2 = (true ? y : false) !== y;
    var __expect2 = false;
  }
  var y = new Boolean(false);
  {
    var __result3 = (y ? y : true) !== y;
    var __expect3 = false;
  }
  