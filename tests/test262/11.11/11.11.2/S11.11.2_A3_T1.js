  {
    var __result1 = (false || true) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = (false || false) !== false;
    var __expect2 = false;
  }
  var y = new Boolean(true);
  {
    var __result3 = (false || y) !== y;
    var __expect3 = false;
  }
  var y = new Boolean(false);
  {
    var __result4 = (false || y) !== y;
    var __expect4 = false;
  }
  