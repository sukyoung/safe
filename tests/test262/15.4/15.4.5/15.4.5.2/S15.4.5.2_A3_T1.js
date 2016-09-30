  var x = [];
  x.length = 1;
  {
    var __result1 = x.length !== 1;
    var __expect1 = false;
  }
  x[5] = 1;
  x.length = 10;
  {
    var __result2 = x.length !== 10;
    var __expect2 = false;
  }
  {
    var __result3 = x[5] !== 1;
    var __expect3 = false;
  }
  