  {
    var __result1 = ("xx" <= "xy") !== true;
    var __expect1 = false;
  }
  {
    var __result2 = ("xy" <= "xx") !== false;
    var __expect2 = false;
  }
  {
    var __result3 = ("x" <= "y") !== true;
    var __expect3 = false;
  }
  {
    var __result4 = ("aab" <= "aba") !== true;
    var __expect4 = false;
  }
  {
    var __result5 = ("\u0061\u0061\u0061\u0062" <= "\u0061\u0061\u0061\u0061") !== false;
    var __expect5 = false;
  }
  {
    var __result6 = ("a\u0000a" <= "a\u0000b") !== true;
    var __expect6 = false;
  }
  {
    var __result7 = ("aB" <= "aa") !== true;
    var __expect7 = false;
  }
  