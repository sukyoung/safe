  var __str = new String(1000000000000000000000);
  {
    var __result1 = typeof __str !== "object";
    var __expect1 = false;
  }
  {
    var __result2 = __str.constructor !== String;
    var __expect2 = false;
  }
  {
    var __result3 = __str != "1e+21";
    var __expect3 = false;
  }
  __str = new String(10000000000000000000000);
  {
    var __result4 = typeof __str !== "object";
    var __expect4 = false;
  }
  {
    var __result5 = __str.constructor !== String;
    var __expect5 = false;
  }
  {
    var __result6 = __str != "1e+22";
    var __expect6 = false;
  }
  