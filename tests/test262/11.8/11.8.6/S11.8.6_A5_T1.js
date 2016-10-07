  var __err = new Error;
  {
    var __result1 = ! (__err instanceof Error);
    var __expect1 = false;
  }
  {
    var __result2 = __err instanceof TypeError;
    var __expect2 = false;
  }
  var err__ = Error('failed');
  {
    var __result3 = ! (err__ instanceof Error);
    var __expect3 = false;
  }
  {
    var __result4 = err__ instanceof TypeError;
    var __expect4 = false;
  }
  