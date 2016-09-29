  {
    var __result1 = typeof Boolean() !== "boolean";
    var __expect1 = false;
  }
  {
    var __result2 = typeof Boolean(1) !== "boolean";
    var __expect2 = false;
  }
  {
    var __result3 = typeof Boolean(new String("1")) !== "boolean";
    var __expect3 = false;
  }
  {
    var __result4 = typeof Boolean(new Object(1)) !== "boolean";
    var __expect4 = false;
  }
  