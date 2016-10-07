  {
    var __result1 = (true && true) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = (true && false) !== false;
    var __expect2 = false;
  }
  var y = new Boolean(true);
  {
    var __result3 = (new Boolean(true) && y) !== y;
    var __expect3 = false;
  }
  var y = new Boolean(false);
  {
    var __result4 = (new Boolean(true) && y) !== y;
    var __expect4 = false;
  }
  var y = new Boolean(true);
  {
    var __result5 = (new Boolean(false) && y) !== y;
    var __expect5 = false;
  }
  var y = new Boolean(false);
  {
    var __result6 = (new Boolean(false) && y) !== y;
    var __expect6 = false;
  }
  