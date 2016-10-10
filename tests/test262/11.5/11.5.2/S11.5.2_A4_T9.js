  {
    var __result1 = Number.MAX_VALUE / 0.9 !== Number.POSITIVE_INFINITY;
    var __expect1 = false;
  }
  {
    var __result2 = Number.MAX_VALUE / - 0.9 !== Number.NEGATIVE_INFINITY;
    var __expect2 = false;
  }
  {
    var __result3 = Number.MAX_VALUE / 1 !== Number.MAX_VALUE;
    var __expect3 = false;
  }
  {
    var __result4 = Number.MAX_VALUE / - 1 !== - Number.MAX_VALUE;
    var __expect4 = false;
  }
  {
    var __result5 = Number.MAX_VALUE / (Number.MAX_VALUE / 0.9) === (Number.MAX_VALUE / Number.MAX_VALUE) / 0.9;
    var __expect5 = false;
  }
  