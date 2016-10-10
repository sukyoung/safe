  var x = true;
  -- x;
  {
    var __result1 = x !== 1 - 1;
    var __expect1 = false;
  }
  var x = new Boolean(false);
  -- x;
  {
    var __result2 = x !== 0 - 1;
    var __expect2 = false;
  }
  