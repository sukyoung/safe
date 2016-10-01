  var x = [];
  x[4294967295] = 1;
  {
    var __result1 = x.length !== 0;
    var __expect1 = false;
  }
  var y = [];
  y[1] = 1;
  y[4294967295] = 1;
  {
    var __result2 = y.length !== 2;
    var __expect2 = false;
  }
  