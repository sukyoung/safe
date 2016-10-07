  {
    var __result1 = isNaN(true % undefined) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = isNaN(undefined % true) !== true;
    var __expect2 = false;
  }
  {
    var __result3 = isNaN(new Boolean(true) % undefined) !== true;
    var __expect3 = false;
  }
  {
    var __result4 = isNaN(undefined % new Boolean(true)) !== true;
    var __expect4 = false;
  }
  