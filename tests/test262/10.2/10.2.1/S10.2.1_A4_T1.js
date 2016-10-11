  function f1(x) 
  {
    return x;
    function x() 
    {
      return 7;
    }
  }
  {
    var __result1 = ! (f1().constructor.prototype === Function.prototype);
    var __expect1 = false;
  }
  function f2(x) 
  {
    return typeof x;
    function x() 
    {
      return 7;
    }
  }
  {
    var __result2 = ! (f2() === "function");
    var __expect2 = false;
  }
  function f3() 
  {
    return typeof arguments;
    function arguments() 
    {
      return 7;
    }
  }
  {
    var __result3 = ! (f3() === "function");
    var __expect3 = false;
  }
  