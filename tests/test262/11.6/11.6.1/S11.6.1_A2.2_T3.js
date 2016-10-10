  function f1() 
  {
    return 0;
  }
  {
    var __result1 = f1 + 1 !== f1.toString() + 1;
    var __expect1 = false;
  }
  function f2() 
  {
    return 0;
  }
  f2.valueOf = (function () 
  {
    return 1;
  });
  {
    var __result2 = 1 + f2 !== 1 + 1;
    var __expect2 = false;
  }
  function f3() 
  {
    return 0;
  }
  f3.toString = (function () 
  {
    return 1;
  });
  {
    var __result3 = 1 + f3 !== 1 + 1;
    var __expect3 = false;
  }
  function f4() 
  {
    return 0;
  }
  f4.valueOf = (function () 
  {
    return - 1;
  });
  f4.toString = (function () 
  {
    return 1;
  });
  {
    var __result4 = f4 + 1 !== 1 - 1;
    var __expect4 = false;
  }
  