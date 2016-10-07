  {
    var __result1 = (true != Number.NaN) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = (- 1 != Number.NaN) !== true;
    var __expect2 = false;
  }
  {
    var __result3 = (Number.NaN != Number.NaN) !== true;
    var __expect3 = false;
  }
  {
    var __result4 = (Number.POSITIVE_INFINITY != Number.NaN) !== true;
    var __expect4 = false;
  }
  {
    var __result5 = (Number.NEGATIVE_INFINITY != Number.NaN) !== true;
    var __expect5 = false;
  }
  {
    var __result6 = (Number.MAX_VALUE != Number.NaN) !== true;
    var __expect6 = false;
  }
  {
    var __result7 = (Number.MIN_VALUE != Number.NaN) !== true;
    var __expect7 = false;
  }
  {
    var __result8 = ("string" != Number.NaN) !== true;
    var __expect8 = false;
  }
  {
    var __result9 = (new Object() != Number.NaN) !== true;
    var __expect9 = false;
  }
  