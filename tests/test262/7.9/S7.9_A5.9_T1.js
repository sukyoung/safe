  var x = 1, y = 1;
  var z = x + + + y;
  {
    var __result1 = (z !== 2) && (y !== 1) && (x !== 1);
    var __expect1 = false;
  }
  z = x + + + y;
  {
    var __result2 = (z !== 2) && (y !== 1) && (x !== 1);
    var __expect2 = false;
  }
  z = x + + + + y;
  {
    var __result3 = (z !== 2) && (y !== 1) && (x !== 1);
    var __expect3 = false;
  }
  