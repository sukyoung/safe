  x = true;
  x -= 1;
  {
    var __result1 = x !== 0;
    var __expect1 = false;
  }
  x = 1;
  x -= true;
  {
    var __result2 = x !== 0;
    var __expect2 = false;
  }
  x = new Boolean(true);
  x -= 1;
  {
    var __result3 = x !== 0;
    var __expect3 = false;
  }
  x = 1;
  x -= new Boolean(true);
  {
    var __result4 = x !== 0;
    var __expect4 = false;
  }
  x = true;
  x -= new Number(1);
  {
    var __result5 = x !== 0;
    var __expect5 = false;
  }
  x = new Number(1);
  x -= true;
  {
    var __result6 = x !== 0;
    var __expect6 = false;
  }
  x = new Boolean(true);
  x -= new Number(1);
  {
    var __result7 = x !== 0;
    var __expect7 = false;
  }
  x = new Number(1);
  x -= new Boolean(true);
  {
    var __result8 = x !== 0;
    var __expect8 = false;
  }
  