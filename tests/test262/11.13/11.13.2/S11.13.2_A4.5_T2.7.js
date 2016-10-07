  x = "1";
  x -= null;
  {
    var __result1 = x !== 1;
    var __expect1 = false;
  }
  x = null;
  x -= "1";
  {
    var __result2 = x !== - 1;
    var __expect2 = false;
  }
  x = new String("1");
  x -= null;
  {
    var __result3 = x !== 1;
    var __expect3 = false;
  }
  x = null;
  x -= new String("1");
  {
    var __result4 = x !== - 1;
    var __expect4 = false;
  }
  