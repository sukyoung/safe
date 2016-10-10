  function f_arg(x, y, z) 
  {
    return z;
  }
  {
    var __result1 = f_arg(x = 1, y = x, x + y) !== 2;
    var __expect1 = false;
  }
  