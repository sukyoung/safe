  function f1(x, x) 
  {
    return x;
  }
  {
    var __result1 = ! (f1(1, 2) === 2);
    var __expect1 = false;
  }
  function f2(x, x, x) 
  {
    return x * x * x;
  }
  {
    var __result2 = ! (f2(1, 2, 3) === 27);
    var __expect2 = false;
  }
  function f3(x, x) 
  {
    return 'a' + x;
  }
  {
    var __result3 = ! (f3(1, 2) === 'a2');
    var __expect3 = false;
  }
  