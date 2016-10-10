  {
    var __result1 = true.toString() !== "true";
    var __expect1 = false;
  }
  {
    var __result2 = false["toString"]() !== "false";
    var __expect2 = false;
  }
  {
    var __result3 = new Boolean(true).toString() !== "true";
    var __expect3 = false;
  }
  {
    var __result4 = new Boolean(false)["toString"]() !== "false";
    var __expect4 = false;
  }
  