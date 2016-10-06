  var x;
  var y = x--;
  {
    var __result1 = isNaN(y) !== true;
    var __expect1 = false;
  }
  var x = null;
  var y = x--;
  {
    var __result2 = y !== 0;
    var __expect2 = false;
  }
  