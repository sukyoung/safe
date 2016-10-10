  {
    var __result1 = true < true !== false;
    var __expect1 = false;
  }
  {
    var __result2 = new Boolean(true) < true !== false;
    var __expect2 = false;
  }
  {
    var __result3 = true < new Boolean(true) !== false;
    var __expect3 = false;
  }
  {
    var __result4 = new Boolean(true) < new Boolean(true) !== false;
    var __expect4 = false;
  }
  