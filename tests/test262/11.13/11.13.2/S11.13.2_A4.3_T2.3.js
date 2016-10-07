  x = 1;
  x %= null;
  {
    var __result1 = isNaN(x) !== true;
    var __expect1 = false;
  }
  x = null;
  x %= 1;
  {
    var __result2 = x !== 0;
    var __expect2 = false;
  }
  x = new Number(1);
  x %= null;
  {
    var __result3 = isNaN(x) !== true;
    var __expect3 = false;
  }
  x = null;
  x %= new Number(1);
  {
    var __result4 = x !== 0;
    var __expect4 = false;
  }
  