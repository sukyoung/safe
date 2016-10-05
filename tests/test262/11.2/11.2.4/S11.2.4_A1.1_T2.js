  function f_arg(x, y) 
  {
    return arguments;
  }
  {
    var __result1 = f_arg().length !== 0;
    var __expect1 = false;
  }
  {
    var __result2 = f_arg()[0] !== undefined;
    var __expect2 = false;
  }
  {
    var __result3 = f_arg.length !== 2;
    var __expect3 = false;
  }
  