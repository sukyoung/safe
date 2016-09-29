  var __str__instance = new String("ABC\u0041\u0042\u0043");
  {
    var __result1 = __str__instance.length !== 6;
    var __expect1 = false;
  }
  __str__instance.valueOf = (function () 
  {
    return "ed";
  });
  __str__instance.toString = (function () 
  {
    return "ed";
  });
  {
    var __result2 = __str__instance != "ed";
    var __expect2 = false;
  }
  {
    var __result3 = __str__instance.length !== 6;
    var __expect3 = false;
  }
  