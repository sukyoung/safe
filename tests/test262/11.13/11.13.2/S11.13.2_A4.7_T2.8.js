  x = true;
  x >>= undefined;
  {
    var __result1 = x !== 1;
    var __expect1 = false;
  }
  x = undefined;
  x >>= true;
  {
    var __result2 = x !== 0;
    var __expect2 = false;
  }
  x = new Boolean(true);
  x >>= undefined;
  {
    var __result3 = x !== 1;
    var __expect3 = false;
  }
  x = undefined;
  x >>= new Boolean(true);
  {
    var __result4 = x !== 0;
    var __expect4 = false;
  }
  