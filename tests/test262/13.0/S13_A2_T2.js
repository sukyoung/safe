  var x = (function __func(arg) 
  {
    return arg + arguments[1];
  })(1, "1");
  {
    var __result1 = x !== "11";
    var __expect1 = false;
  }
  {
    var __result2 = typeof __func !== 'undefined';
    var __expect2 = false;
  }
  