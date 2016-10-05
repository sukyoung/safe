  var x = false;
  var y = x++;
  {
    var __result1 = y !== 0;
    var __expect1 = false;
  }
  var x = new Boolean(true);
  var y = x++;
  {
    var __result2 = y !== 1;
    var __expect2 = false;
  }
  