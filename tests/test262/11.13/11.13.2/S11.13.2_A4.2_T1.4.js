  x = null;
  x /= undefined;
  {
    var __result1 = isNaN(x) !== true;
    var __expect1 = false;
  }
  x = undefined;
  x /= null;
  {
    var __result2 = isNaN(x) !== true;
    var __expect2 = false;
  }
  x = undefined;
  x /= undefined;
  {
    var __result3 = isNaN(x) !== true;
    var __expect3 = false;
  }
  x = null;
  x /= null;
  {
    var __result4 = isNaN(x) !== true;
    var __expect4 = false;
  }
  