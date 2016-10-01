  x = [];
  x[true] = 1;
  {
    var __result1 = x[1] !== undefined;
    var __expect1 = false;
  }
  {
    var __result2 = x["true"] !== 1;
    var __expect2 = false;
  }
  x[false] = 0;
  {
    var __result3 = x[0] !== undefined;
    var __expect3 = false;
  }
  {
    var __result4 = x["false"] !== 0;
    var __expect4 = false;
  }
  