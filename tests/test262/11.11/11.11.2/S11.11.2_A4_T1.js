  {
    var __result1 = (true || true) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = (true || false) !== true;
    var __expect2 = false;
  }
  var x = new Boolean(true);
  {
    var __result3 = (x || new Boolean(true)) !== x;
    var __expect3 = false;
  }
  var x = new Boolean(true);
  {
    var __result4 = (x || new Boolean(false)) !== x;
    var __expect4 = false;
  }
  var x = new Boolean(false);
  {
    var __result5 = (x || new Boolean(true)) !== x;
    var __expect5 = false;
  }
  var x = new Boolean(false);
  {
    var __result6 = (x || new Boolean(false)) !== x;
    var __expect6 = false;
  }
  