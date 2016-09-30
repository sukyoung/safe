  function __func(arg) 
  {
    return ++ arg;
  }
  ;
  {
    var __result1 = typeof __func !== "function";
    var __expect1 = false;
  }
  {
    var __result2 = __func(1) !== 2;
    var __expect2 = false;
  }
  