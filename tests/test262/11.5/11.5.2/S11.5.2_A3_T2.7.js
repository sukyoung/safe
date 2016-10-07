  {
    var __result1 = "1" / null !== Number.POSITIVE_INFINITY;
    var __expect1 = false;
  }
  {
    var __result2 = null / "1" !== 0;
    var __expect2 = false;
  }
  {
    var __result3 = new String("1") / null !== Number.POSITIVE_INFINITY;
    var __expect3 = false;
  }
  {
    var __result4 = null / new String("1") !== 0;
    var __expect4 = false;
  }
  