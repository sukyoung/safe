  {
    var __result1 = (1.1 < 1) !== false;
    var __expect1 = false;
  }
  {
    var __result2 = (1 < 1.1) !== true;
    var __expect2 = false;
  }
  {
    var __result3 = (- 1.1 < - 1) !== true;
    var __expect3 = false;
  }
  {
    var __result4 = (- 1 < - 1.1) !== false;
    var __expect4 = false;
  }
  {
    var __result5 = (0 < 0.1) !== true;
    var __expect5 = false;
  }
  {
    var __result6 = (- 0.1 < 0) !== true;
    var __expect6 = false;
  }
  {
    var __result7 = (Number.MAX_VALUE / 2 < Number.MAX_VALUE) !== true;
    var __expect7 = false;
  }
  {
    var __result8 = (Number.MIN_VALUE < Number.MIN_VALUE * 2) !== true;
    var __expect8 = false;
  }
  