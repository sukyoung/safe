  function f1() 
  {
    var x;
    return typeof x;
  }
var __result1 = ! (f1() === "undefined");
var __expect1 = false;
  function f2() 
  {
    var x;
    return x;
  }
var __result2 = ! (f2() === undefined);
var __expect2 = false;
