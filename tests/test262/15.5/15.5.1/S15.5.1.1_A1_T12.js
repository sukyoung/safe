  var __str = String(1 / "a");
  {
    var __result1 = typeof __str !== "string";
    var __expect1 = false;
  }
  {
    var __result2 = __str !== "NaN";
    var __expect2 = false;
  }
  __str = String("b" * null);
  {
    var __result3 = typeof __str !== "string";
    var __expect3 = false;
  }
  {
    var __result4 = __str !== "NaN";
    var __expect4 = false;
  }
  __str = String(Number.NaN);
  {
    var __result5 = typeof __str !== "string";
    var __expect5 = false;
  }
  {
    var __result6 = __str !== "NaN";
    var __expect6 = false;
  }
  