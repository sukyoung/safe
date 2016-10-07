  {
    var __result1 = "1" <= 1 !== true;
    var __expect1 = false;
  }
  {
    var __result2 = 1 <= "1" !== true;
    var __expect2 = false;
  }
  {
    var __result3 = new String("1") <= 1 !== true;
    var __expect3 = false;
  }
  {
    var __result4 = 1 <= new String("1") !== true;
    var __expect4 = false;
  }
  {
    var __result5 = "1" <= new Number(1) !== true;
    var __expect5 = false;
  }
  {
    var __result6 = new Number(1) <= "1" !== true;
    var __expect6 = false;
  }
  {
    var __result7 = new String("1") <= new Number(1) !== true;
    var __expect7 = false;
  }
  {
    var __result8 = new Number(1) <= new String("1") !== true;
    var __expect8 = false;
  }
  {
    var __result9 = "x" <= 1 !== false;
    var __expect9 = false;
  }
  {
    var __result10 = 1 <= "x" !== false;
    var __expect10 = false;
  }
  