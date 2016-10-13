  function f1(a) 
  {
    delete a;
    return a;
  }
var __result1 = f1(1) !== 1;
var __expect1 = false;
