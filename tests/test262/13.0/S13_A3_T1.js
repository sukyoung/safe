  var __func = (function __exp__func(arg) 
  {
    if (arg === 1)
    {
      return arg;
    }
    else
    {
      return __exp__func(arg - 1) * arg;
    }
  });
  var fact_of_3 = __func(3);
  {
    var __result1 = fact_of_3 !== 6;
    var __expect1 = false;
  }
  