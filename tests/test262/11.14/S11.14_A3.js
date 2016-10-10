  var x = 0;
  var y = 0;
  var z = 0;
  {
    var __result1 = (x = 1, y = 2, z = 3) !== 3;
    var __expect1 = false;
  }
  var x = 0;
  var y = 0;
  var z = 0;
  x = 1, y = 2, z = 3;
  {
    var __result2 = x !== 1;
    var __expect2 = false;
  }
  {
    var __result3 = y !== 2;
    var __expect3 = false;
  }
  {
    var __result4 = z !== 3;
    var __expect4 = false;
  }
  