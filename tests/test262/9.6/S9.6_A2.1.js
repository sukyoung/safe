  {
    var __result1 = (0 >>> 0) !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = (1 >>> 0) !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = (- 1 >>> 0) !== 4294967295;
    var __expect3 = false;
  }
  {
    var __result4 = (4294967295 >>> 0) !== 4294967295;
    var __expect4 = false;
  }
  {
    var __result5 = (4294967294 >>> 0) !== 4294967294;
    var __expect5 = false;
  }
  {
    var __result6 = (4294967296 >>> 0) !== 0;
    var __expect6 = false;
  }
  