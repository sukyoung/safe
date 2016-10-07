  {
    var __result1 = ("x" > "0") !== true;
    var __expect1 = false;
  }
  {
    var __result2 = ("0" > "-") !== true;
    var __expect2 = false;
  }
  {
    var __result3 = ("0" > ".") !== true;
    var __expect3 = false;
  }
  {
    var __result4 = ("-" > "+") !== true;
    var __expect4 = false;
  }
  {
    var __result5 = ("-1" > "-0") !== true;
    var __expect5 = false;
  }
  {
    var __result6 = ("-1" > "+1") !== true;
    var __expect6 = false;
  }
  {
    var __result7 = ("1e-10" > "1") !== true;
    var __expect7 = false;
  }
  