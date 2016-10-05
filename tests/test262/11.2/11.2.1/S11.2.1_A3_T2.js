  {
    var __result1 = 1..toString() !== "1";
    var __expect1 = false;
  }
  {
    var __result2 = 1.1.toFixed(5) !== "1.10000";
    var __expect2 = false;
  }
  {
    var __result3 = 1["toString"]() !== "1";
    var __expect3 = false;
  }
  {
    var __result4 = 1.["toFixed"](5) !== "1.00000";
    var __expect4 = false;
  }
  {
    var __result5 = new Number(1).toString() !== "1";
    var __expect5 = false;
  }
  {
    var __result6 = new Number(1)["toFixed"](5) !== "1.00000";
    var __expect6 = false;
  }
  