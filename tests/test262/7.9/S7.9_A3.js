  function f1() 
  {
    return 1;
  }
  {
    var __result1 = f1() !== 1;
    var __expect1 = false;
  }
  function f2() 
  {
    return;
    1;
  }
  {
    var __result2 = f2() !== undefined;
    var __expect2 = false;
  }
  