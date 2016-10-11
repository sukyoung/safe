  {
    var __result1 = Number("12345e6") !== + ("12345") * 1000000;
    var __expect1 = false;
  }
  {
    var __result2 = Number("12345e-6") !== Number("12345") * 0.000001;
    var __expect2 = false;
  }
  