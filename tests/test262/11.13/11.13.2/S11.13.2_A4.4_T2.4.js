  x = true;
  x += undefined;
  {
    var __result1 = isNaN(x) !== true;
    var __expect1 = false;
  }
  x = undefined;
  x += true;
  {
    var __result2 = isNaN(x) !== true;
    var __expect2 = false;
  }
  x = new Boolean(true);
  x += undefined;
  {
    var __result3 = isNaN(x) !== true;
    var __expect3 = false;
  }
  x = undefined;
  x += new Boolean(true);
  {
    var __result4 = isNaN(x) !== true;
    var __expect4 = false;
  }
  