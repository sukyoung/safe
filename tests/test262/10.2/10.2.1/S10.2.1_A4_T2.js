  function f1() 
  {
    var x;
    return x;
    function x() 
    {
      return 7;
    }
  }
var __result1 = ! (f1().constructor.prototype === Function.prototype);
var __expect1 = false;
  function f2() 
  {
    var x;
    return typeof x;
    function x() 
    {
      return 7;
    }
  }
var __result2 = ! (f2() === "function");
var __expect2 = false;
