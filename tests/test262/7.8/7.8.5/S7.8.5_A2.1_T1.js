  {
    var __result1 = /1a/.source !== "1a";
    var __expect1 = false;
  }
  {
    var __result2 = /aa/.source !== "aa";
    var __expect2 = false;
  }
  {
    var __result3 = /,;/.source !== ",;";
    var __expect3 = false;
  }
  {
    var __result4 = /  /.source !== "  ";
    var __expect4 = false;
  }
  {
    var __result5 = /a\u0041/.source !== "a\\u0041";
    var __expect5 = false;
  }
  