  var x = "1";
  var y = x--;
  {
    var __result1 = y !== 1;
    var __expect1 = false;
  }
  var x = "x";
  var y = x--;
  {
    var __result2 = isNaN(y) !== true;
    var __expect2 = false;
  }
  var x = new String("-1");
  var y = x--;
  {
    var __result3 = y !== - 1;
    var __expect3 = false;
  }
  