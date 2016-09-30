  function __func(param1, param2, param3) 
  {
    return arguments.length;
  }
  {
    var __result1 = __func('A') !== 1;
    var __expect1 = false;
  }
  {
    var __result2 = __func('A', 'B', 1, 2, __func) !== 5;
    var __expect2 = false;
  }
  