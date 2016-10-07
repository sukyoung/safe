  {
    var __result1 = isNaN(1 / undefined) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = isNaN(undefined / 1) !== true;
    var __expect2 = false;
  }
  {
    var __result3 = isNaN(new Number(1) / undefined) !== true;
    var __expect3 = false;
  }
  {
    var __result4 = isNaN(undefined / new Number(1)) !== true;
    var __expect4 = false;
  }
  