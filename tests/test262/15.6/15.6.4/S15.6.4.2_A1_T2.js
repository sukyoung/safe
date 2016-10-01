  {
    var __result1 = Boolean.prototype.toString(true) !== "false";
    var __expect1 = false;
  }
  {
    var __result2 = (new Boolean()).toString(true) !== "false";
    var __expect2 = false;
  }
  {
    var __result3 = (new Boolean(false)).toString(true) !== "false";
    var __expect3 = false;
  }
  {
    var __result4 = (new Boolean(true)).toString(false) !== "true";
    var __expect4 = false;
  }
  {
    var __result5 = (new Boolean(1)).toString(false) !== "true";
    var __expect5 = false;
  }
  {
    var __result6 = (new Boolean(0)).toString(true) !== "false";
    var __expect6 = false;
  }
  {
    var __result7 = (new Boolean(new Object())).toString(false) !== "true";
    var __expect7 = false;
  }
  