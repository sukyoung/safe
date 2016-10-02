  function __func__INC(arg) 
  {
    return arg + 1;
  }
  ;
  function __func__MULT(incrementator, arg, mult) 
  {
    return incrementator(arg) * mult;
  }
  ;
  {
    var __result1 = __func__MULT(__func__INC, 2, 2) !== 6;
    var __expect1 = false;
  }
  