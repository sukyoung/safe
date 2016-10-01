  var __str__instance = new String("globglob");
  {
    var __result1 = ! (__str__instance.hasOwnProperty("length"));
    var __expect1 = false;
  }
  {
    var __result2 = __str__instance.length !== 8;
    var __expect2 = false;
  }
  __str__instance.length = - 1;
  {
    var __result3 = __str__instance.length !== 8;
    var __expect3 = false;
  }
  with (__str__instance)
    length = 0;
  {
    var __result4 = __str__instance.length !== 8;
    var __expect4 = false;
  }
  __str__instance.length++;
  {
    var __result5 = __str__instance.length !== 8;
    var __expect5 = false;
  }
  