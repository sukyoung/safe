  function __func(__arg) 
  {
    __arg = 2;
    delete arguments[0];
    {
      var __result1 = arguments[0] !== undefined;
      var __expect1 = false;
    }
    return __arg;
  }
  {
    var __result2 = __func(1) !== 2;
    var __expect2 = false;
  }
  