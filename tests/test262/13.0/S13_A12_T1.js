  ALIVE = "Letov is alive";
  function __func() 
  {
    return ALIVE;
  }
  ;
  {
    var __result1 = delete __func;
    var __expect1 = false;
  }
  {
    var __result2 = __func() !== ALIVE;
    var __expect2 = false;
  }
  