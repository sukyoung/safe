  var __str = String(true);
  {
    var __result1 = typeof __str !== "string";
    var __expect1 = false;
  }
  {
    var __result2 = __str !== "true";
    var __expect2 = false;
  }
  __str = String(false);
  {
    var __result3 = typeof __str !== "string";
    var __expect3 = false;
  }
  {
    var __result4 = __str !== "false";
    var __expect4 = false;
  }
  __str = String(Boolean(true));
  {
    var __result5 = typeof __str !== "string";
    var __expect5 = false;
  }
  {
    var __result6 = __str !== "true";
    var __expect6 = false;
  }
  __str = String(Boolean(false));
  {
    var __result7 = typeof __str !== "string";
    var __expect7 = false;
  }
  {
    var __result8 = __str !== "false";
    var __expect8 = false;
  }
  