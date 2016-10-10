  {
    var __result1 = ("xy" >= "xx") !== true;
    var __expect1 = false;
  }
  {
    var __result2 = ("xx" >= "xy") !== false;
    var __expect2 = false;
  }
  {
    var __result3 = ("y" >= "x") !== true;
    var __expect3 = false;
  }
  {
    var __result4 = ("aba" >= "aab") !== true;
    var __expect4 = false;
  }
  {
    var __result5 = ("\u0061\u0061\u0061\u0061" >= "\u0061\u0061\u0061\u0062") !== false;
    var __expect5 = false;
  }
  {
    var __result6 = ("a\u0000b" >= "a\u0000a") !== true;
    var __expect6 = false;
  }
  {
    var __result7 = ("aa" >= "aB") !== true;
    var __expect7 = false;
  }
  