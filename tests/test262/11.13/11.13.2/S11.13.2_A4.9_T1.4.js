  x = null;
  x &= undefined;
  {
    var __result1 = x !== 0;
    var __expect1 = false;
  }
  x = undefined;
  x &= null;
  {
    var __result2 = x !== 0;
    var __expect2 = false;
  }
  x = undefined;
  x &= undefined;
  {
    var __result3 = x !== 0;
    var __expect3 = false;
  }
  x = null;
  x &= null;
  {
    var __result4 = x !== 0;
    var __expect4 = false;
  }
  