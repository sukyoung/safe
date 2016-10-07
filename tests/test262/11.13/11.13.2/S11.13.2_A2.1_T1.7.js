  var x = 4;
  var z = (x >>= 1);
  {
    var __result1 = z !== 2;
    var __expect1 = false;
  }
  var x = 4;
  var y = 1;
  var z = (x >>= y);
  {
    var __result2 = z !== 2;
    var __expect2 = false;
  }
  