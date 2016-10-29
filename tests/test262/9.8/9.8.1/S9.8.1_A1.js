  {
    var __result1 = String(NaN) !== "NaN";
    var __expect1 = false;
  }
  {
    var __result2 = String(Number.NaN) !== "NaN";
    var __expect2 = false;
  }
  {
    var __result3 = String(Number("asasa")) !== "NaN";
    var __expect3 = false;
  }
  