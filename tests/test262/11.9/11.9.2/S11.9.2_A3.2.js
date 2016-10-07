  {
    var __result1 = (true != 1) !== false;
    var __expect1 = false;
  }
  {
    var __result2 = (false != "0") !== false;
    var __expect2 = false;
  }
  {
    var __result3 = (true != new Boolean(true)) !== false;
    var __expect3 = false;
  }
  {
    var __result4 = (true != {
      valueOf : (function () 
      {
        return 1;
      })
    }) !== false;
    var __expect4 = false;
  }
  