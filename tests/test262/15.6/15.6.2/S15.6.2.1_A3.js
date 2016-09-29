  var x1 = new Boolean(1);
  {
    var __result1 = x1.valueOf() !== true;
    var __expect1 = false;
  }
  var x2 = new Boolean();
  {
    var __result2 = x2.valueOf() !== false;
    var __expect2 = false;
  }
  var x2 = new Boolean(0);
  {
    var __result3 = x2.valueOf() !== false;
    var __expect3 = false;
  }
  var x2 = new Boolean(new Object());
  {
    var __result4 = x2.valueOf() !== true;
    var __expect4 = false;
  }
  