  function f1(x) 
  {
    var x;
    return typeof x;
  }
var __result1 = ! (f1(1) === "number");
var __expect1 = false;
  function f2(x) 
  {
    var x;
    return x;
  }
var __result2 = ! (f2(1) === 1);
var __expect2 = false;
