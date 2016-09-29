  var x = - 0.000000000000001;
  {
    var __result1 = ! isNaN(Math.sqrt(x));
    var __expect1 = false;
  }
  var x = - 1;
  {
    var __result2 = ! isNaN(Math.sqrt(x));
    var __expect2 = false;
  }
  var x = - Infinity;
  {
    var __result3 = ! isNaN(Math.sqrt(x));
    var __expect3 = false;
  }
  