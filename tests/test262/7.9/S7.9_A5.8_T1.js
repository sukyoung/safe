  var x = 0, y = 0;
  var z = x + ++ y;
  {
    var __result1 = (z !== 1) && (y !== 1) && (x !== 0);
    var __expect1 = false;
  }
  z = x + ++ y;
  {
    var __result2 = (z !== 2) && (y !== 2) && (x !== 0);
    var __expect2 = false;
  }
  z = x + ++ y;
  {
    var __result3 = (z !== 3) && (y !== 3) && (x !== 0);
    var __expect3 = false;
  }
  