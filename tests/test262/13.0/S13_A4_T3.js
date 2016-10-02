  function __func(arg1, arg2, arg3) 
  {
    return arg1 += (arg2 += arg3);
  }
  ;
  {
    var __result1 = typeof __func !== "function";
    var __expect1 = false;
  }
  {
    var __result2 = __func(10, 20, 30) !== 60;
    var __expect2 = false;
  }
  