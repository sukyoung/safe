  function __func(__arg) 
  {
    __arg = 2;
    delete arguments[0];
    {
      var __result1 = arguments[0] !== undefined;
      var __expect1 = false;
    }
    arguments[0] = "A";
    {
      var __result2 = arguments[0] !== "A";
      var __expect2 = false;
    }
    return __arg;
  }
  {
    var __result3 = __func(1) !== 2;
    var __expect3 = false;
  }
  