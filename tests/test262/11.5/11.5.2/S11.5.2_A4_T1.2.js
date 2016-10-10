  {
    var __result1 = isNaN(Number.NaN / Number.NaN) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = isNaN(+ 0 / Number.NaN) !== true;
    var __expect2 = false;
  }
  {
    var __result3 = isNaN(- 0 / Number.NaN) !== true;
    var __expect3 = false;
  }
  {
    var __result4 = isNaN(Number.POSITIVE_INFINITY / Number.NaN) !== true;
    var __expect4 = false;
  }
  {
    var __result5 = isNaN(Number.NEGATIVE_INFINITY / Number.NaN) !== true;
    var __expect5 = false;
  }
  {
    var __result6 = isNaN(Number.MAX_VALUE / Number.NaN) !== true;
    var __expect6 = false;
  }
  {
    var __result7 = isNaN(Number.MIN_VALUE / Number.NaN) !== true;
    var __expect7 = false;
  }
  {
    var __result8 = isNaN(1 / Number.NaN) !== true;
    var __expect8 = false;
  }
  