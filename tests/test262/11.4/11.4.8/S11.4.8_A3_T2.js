  {
    var __result1 = ~ 0.1 !== - 1;
    var __expect1 = false;
  }
  {
    var __result2 = ~ new Number(- 0.1) !== - 1;
    var __expect2 = false;
  }
  {
    var __result3 = ~ NaN !== - 1;
    var __expect3 = false;
  }
  {
    var __result4 = ~ new Number(NaN) !== - 1;
    var __expect4 = false;
  }
  {
    var __result5 = ~ 1 !== - 2;
    var __expect5 = false;
  }
  {
    var __result6 = ~ new Number(- 2) !== 1;
    var __expect6 = false;
  }
  {
    var __result7 = ~ Infinity !== - 1;
    var __expect7 = false;
  }
  