  {
    var __result1 = isNaN(Number.NEGATIVE_INFINITY % Number.POSITIVE_INFINITY) !== true;
    var __expect1 = false;
  }
  {
    var __result2 = isNaN(Number.NEGATIVE_INFINITY % Number.NEGATIVE_INFINITY) !== true;
    var __expect2 = false;
  }
  {
    var __result3 = isNaN(Number.POSITIVE_INFINITY % Number.POSITIVE_INFINITY) !== true;
    var __expect3 = false;
  }
  {
    var __result4 = isNaN(Number.POSITIVE_INFINITY % Number.NEGATIVE_INFINITY) !== true;
    var __expect4 = false;
  }
  {
    var __result5 = isNaN(Number.NEGATIVE_INFINITY % 1) !== true;
    var __expect5 = false;
  }
  {
    var __result6 = isNaN(Number.NEGATIVE_INFINITY % - 1) !== true;
    var __expect6 = false;
  }
  {
    var __result7 = isNaN(Number.POSITIVE_INFINITY % 1) !== true;
    var __expect7 = false;
  }
  {
    var __result8 = isNaN(Number.POSITIVE_INFINITY % - 1) !== true;
    var __expect8 = false;
  }
  {
    var __result9 = isNaN(Number.NEGATIVE_INFINITY % Number.MAX_VALUE) !== true;
    var __expect9 = false;
  }
  {
    var __result10 = isNaN(Number.NEGATIVE_INFINITY % - Number.MAX_VALUE) !== true;
    var __expect10 = false;
  }
  {
    var __result11 = isNaN(Number.POSITIVE_INFINITY % Number.MAX_VALUE) !== true;
    var __expect11 = false;
  }
  {
    var __result12 = isNaN(Number.POSITIVE_INFINITY % - Number.MAX_VALUE) !== true;
    var __expect12 = false;
  }
  