  {
    var __result1 = ("" == "") !== true;
    var __expect1 = false;
  }
  {
    var __result2 = (" " == " ") !== true;
    var __expect2 = false;
  }
  {
    var __result3 = (" " == "") !== false;
    var __expect3 = false;
  }
  {
    var __result4 = ("string" == "string") !== true;
    var __expect4 = false;
  }
  {
    var __result5 = (" string" == "string ") !== false;
    var __expect5 = false;
  }
  {
    var __result6 = ("1.0" == "1") !== false;
    var __expect6 = false;
  }
  {
    var __result7 = ("0xff" == "255") !== false;
    var __expect7 = false;
  }
  