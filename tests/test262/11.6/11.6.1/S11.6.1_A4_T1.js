  {
    var __result1 = isNaN(Number.NaN + 1) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = isNaN(1 + Number.NaN) !== true;
    var __expect2 = false;
  }
  {
    var __result3 = isNaN(Number.NaN + Number.POSITIVE_INFINITY) !== true;
    var __expect3 = false;
  }
  {
    var __result4 = isNaN(Number.POSITIVE_INFINITY + Number.NaN) !== true;
    var __expect4 = false;
  }
  {
    var __result5 = isNaN(Number.NaN + Number.NEGATIVE_INFINITY) !== true;
    var __expect5 = false;
  }
  {
    var __result6 = isNaN(Number.NEGATIVE_INFINITY + Number.NaN) !== true;
    var __expect6 = false;
  }
  