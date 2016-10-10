  function truncate(x) 
  {
    if (x > 0)
    {
      return Math.floor(x);
    }
    else
    {
      return Math.ceil(x);
    }
  }
  x = 1.3;
  y = 1.1;
  {
    var __result1 = x % y !== 0.19999999999999996;
    var __expect1 = false;
  }
  x = - 1.3;
  y = 1.1;
  {
    var __result2 = x % y !== - 0.19999999999999996;
    var __expect2 = false;
  }
  x = 1.3;
  y = - 1.1;
  {
    var __result3 = x % y !== 0.19999999999999996;
    var __expect3 = false;
  }
  x = - 1.3;
  y = - 1.1;
  {
    var __result4 = x % y !== - 0.19999999999999996;
    var __expect4 = false;
  }
  x = 1.3;
  y = 1.1;
  {
    var __result5 = x % y !== x - truncate(x / y) * y;
    var __expect5 = false;
  }
  x = - 1.3;
  y = 1.1;
  {
    var __result6 = x % y !== x - truncate(x / y) * y;
    var __expect6 = false;
  }
  x = 1.3;
  y = - 1.1;
  {
    var __result7 = x % y !== x - truncate(x / y) * y;
    var __expect7 = false;
  }
  x = - 1.3;
  y = - 1.1;
  {
    var __result8 = x % y !== x - truncate(x / y) * y;
    var __expect8 = false;
  }
  