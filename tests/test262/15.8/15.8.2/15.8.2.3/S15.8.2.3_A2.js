  var x = 1.000000000000001;
  {
    var __result1 = ! isNaN(Math.asin(x));
    var __expect1 = false;
  }
  x = 2;
  {
    var __result2 = ! isNaN(Math.asin(x));
    var __expect2 = false;
  }
  x = + Infinity;
  {
    var __result3 = ! isNaN(Math.asin(x));
    var __expect3 = false;
  }
  