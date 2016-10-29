  {
    var __result1 = Number("1234.5678e9") !== (Number("1234") + (Number("5678") * 0.0001)) * 1000000000;
    var __expect1 = false;
  }
  {
    var __result2 = + ("1234.5678e-9") !== (Number("1234") + (Number("5678") * 0.0001)) * 1E-9;
    var __expect2 = false;
  }
  