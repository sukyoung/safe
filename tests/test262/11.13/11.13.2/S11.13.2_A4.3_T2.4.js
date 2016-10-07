  x = 1;
  x %= undefined;
  {
    var __result1 = isNaN(x) !== true;
    var __expect1 = false;
  }
  x = undefined;
  x %= 1;
  {
    var __result2 = isNaN(x) !== true;
    var __expect2 = false;
  }
  x = new Number(1);
  x %= undefined;
  {
    var __result3 = isNaN(x) !== true;
    var __expect3 = false;
  }
  x = undefined;
  x %= new Number(1);
  {
    var __result4 = isNaN(x) !== true;
    var __expect4 = false;
  }
  