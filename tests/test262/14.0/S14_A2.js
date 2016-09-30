  {
    var __result1 = typeof f !== 'undefined';
    var __expect1 = false;
  }
  {
    var __result2 = (function f(arg) 
    {
      if (arg === 0)
        return 1;
      else
        return f(arg - 1) * arg;
    })(3) !== 6;
    var __expect2 = false;
  }
  ;
  