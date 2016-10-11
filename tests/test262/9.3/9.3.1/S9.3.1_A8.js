  {
    var __result1 = Number("1234e5") !== Number("1234") * 100000;
    var __expect1 = false;
  }
  {
    var __result2 = Number("1234.e5") !== + ("1234") * 100000;
    var __expect2 = false;
  }
  