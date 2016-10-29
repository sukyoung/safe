  {
    var __result1 = ~ 2147483647 !== - 2147483648;
    var __expect1 = false;
  }
  {
    var __result2 = ~ 2147483648 !== ~ - 2147483648;
    var __expect2 = false;
  }
  {
    var __result3 = ~ 2147483649 !== ~ - 2147483647;
    var __expect3 = false;
  }
  {
    var __result4 = ~ 4294967295 !== ~ - 1;
    var __expect4 = false;
  }
  {
    var __result5 = ~ 4294967296 !== ~ 0;
    var __expect5 = false;
  }
  {
    var __result6 = ~ 4294967297 !== ~ 1;
    var __expect6 = false;
  }
  