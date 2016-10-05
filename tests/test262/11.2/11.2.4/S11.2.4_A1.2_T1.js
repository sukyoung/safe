  f_arg = (function () 
  {
    return arguments;
  });
  {
    var __result1 = f_arg(1, 2, 3).length !== 3;
    var __expect1 = false;
  }
  {
    var __result2 = f_arg(1, 2, 3)[0] !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = f_arg(1, 2, 3)[1] !== 2;
    var __expect3 = false;
  }
  {
    var __result4 = f_arg(1, 2, 3)[2] !== 3;
    var __expect4 = false;
  }
  {
    var __result5 = f_arg(1, 2, 3)[3] !== undefined;
    var __expect5 = false;
  }
  