  {
    var __result1 = "1" + undefined !== "1undefined";
    var __expect1 = false;
  }
  {
    var __result2 = undefined + "1" !== "undefined1";
    var __expect2 = false;
  }
  {
    var __result3 = new String("1") + undefined !== "1undefined";
    var __expect3 = false;
  }
  {
    var __result4 = undefined + new String("1") !== "undefined1";
    var __expect4 = false;
  }
  