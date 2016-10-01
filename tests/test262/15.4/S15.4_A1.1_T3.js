  x = [];
  x[4294967296] = 1;
  {
    var __result1 = x[0] !== undefined;
    var __expect1 = false;
  }
  {
    var __result2 = x["4294967296"] !== 1;
    var __expect2 = false;
  }
  y = [];
  y[4294967297] = 1;
  {
    var __result3 = y[1] !== undefined;
    var __expect3 = false;
  }
  {
    var __result4 = y["4294967297"] !== 1;
    var __expect4 = false;
  }
  z = [];
  z[1.1] = 1;
  {
    var __result5 = z[1] !== undefined;
    var __expect5 = false;
  }
  {
    var __result6 = z["1.1"] !== 1;
    var __expect6 = false;
  }
  