  x = [];
  x[NaN] = 1;
  {
    var __result1 = x[0] !== undefined;
    var __expect1 = false;
  }
  {
    var __result2 = x["NaN"] !== 1;
    var __expect2 = false;
  }
  y = [];
  y[Number.POSITIVE_INFINITY] = 1;
  {
    var __result3 = y[0] !== undefined;
    var __expect3 = false;
  }
  {
    var __result4 = y["Infinity"] !== 1;
    var __expect4 = false;
  }
  z = [];
  z[Number.NEGATIVE_INFINITY] = 1;
  {
    var __result5 = z[0] !== undefined;
    var __expect5 = false;
  }
  {
    var __result6 = z["-Infinity"] !== 1;
    var __expect6 = false;
  }
  