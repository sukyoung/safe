  {
    var __result1 = /a\1/.source !== "a\\1";
    var __expect1 = false;
  }
  {
    var __result2 = /a\a/.source !== "a\\a";
    var __expect2 = false;
  }
  {
    var __result3 = /,\;/.source !== ",\\;";
    var __expect3 = false;
  }
  {
    var __result4 = / \ /.source !== " \\ ";
    var __expect4 = false;
  }
  