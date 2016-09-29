  y = + Infinity;
  x = new Array();
  x[0] = 0.999999999999999;
  x[1] = 0.5;
  x[2] = + 0;
  x[3] = - 0;
  x[4] = - 0.5;
  x[5] = - 0.999999999999999;
  xnum = 6;
  for (i = 0;i < xnum;i++)
  {
    {
      var __result1 = Math.pow(x[i], y) !== + 0;
      var __expect1 = false;
    }
  }
  