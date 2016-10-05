  var x = 1.1;
  var y = x--;
  {
    var __result1 = y !== 1.1;
    var __expect1 = false;
  }
  var x = new Number(- 0.1);
  var y = x--;
  {
    var __result2 = y !== - 0.1;
    var __expect2 = false;
  }
  