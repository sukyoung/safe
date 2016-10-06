  {
    var __result1 = ~ "1" !== - 2;
    var __expect1 = false;
  }
  {
    var __result2 = ~ new String("0") !== - 1;
    var __expect2 = false;
  }
  {
    var __result3 = ~ "x" !== - 1;
    var __expect3 = false;
  }
  {
    var __result4 = ~ "" !== - 1;
    var __expect4 = false;
  }
  {
    var __result5 = ~ new String("-2") !== 1;
    var __expect5 = false;
  }
  