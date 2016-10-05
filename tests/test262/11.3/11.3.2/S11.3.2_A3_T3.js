  var x = "1";
  x--;
  {
    var __result1 = x !== 1 - 1;
    var __expect1 = false;
  }
  var x = "x";
  x--;
  {
    var __result2 = isNaN(x) !== true;
    var __expect2 = false;
  }
  var x = new Number("-1");
  x--;
  {
    var __result3 = x !== - 1 - 1;
    var __expect3 = false;
  }
  