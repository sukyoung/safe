  {
    var __result1 = (1 ? 0 : 1) !== 0;
    var __expect1 = false;
  }
  var y = new Number(1);
  {
    var __result2 = (1 ? y : 0) !== y;
    var __expect2 = false;
  }
  var y = new Number(NaN);
  {
    var __result3 = (y ? y : 1) !== y;
    var __expect3 = false;
  }
  